package org.proje2.prolab2proje2.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.proje2.prolab2proje2.data.PreProcessor;
import org.proje2.prolab2proje2.data.UserRecord;

public class DecisionTreeAlgorithm extends BaseAlgorithm {

    private DecisionTreeNode rootNode;
    private int maxDepth = 2; // Default depth
    private List<String> lastDecisionPath = new ArrayList<>();

    public DecisionTreeAlgorithm() {}

    public DecisionTreeAlgorithm(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public List<String> getLastDecisionPath() {
        return lastDecisionPath;
    }

    @Override
    public void trainModel(List<UserRecord> trainingDataset){
        this.dataSet = trainingDataset;
        this.rootNode = buildTree(trainingDataset, 0);
    }

    private DecisionTreeNode buildTree(List<UserRecord> data, int currentDepth) {
        // Base case: Leaf node if depth reached or data is pure/empty
        if (currentDepth >= maxDepth || data.isEmpty()) {
            return CreateLeafNode(data);
        }

        // Calculate best feature to split using Information Gain
        double bestGain = -1.0;
        String bestFeature = null;
        List<UserRecord> bestLeft = null;
        List<UserRecord> bestRight = null;

        // Try Gender split
        List<UserRecord> maleList = splitDatasetByGender("E", data);
        List<UserRecord> femaleList = splitDatasetByGender("K", data);
        double genderGain = calculateInformationGain(data, maleList, femaleList);
        if (genderGain > bestGain) {
            bestGain = genderGain;
            bestFeature = "Gender";
            bestLeft = maleList;
            bestRight = femaleList;
        }

        // Try Spending split (LineNetTotal)
        List<UserRecord> highExpense = splitDatasetBySpending(data, true);
        List<UserRecord> lowExpense = splitDatasetBySpending(data, false);
        double spendingGain = calculateInformationGain(data, highExpense, lowExpense);
        if (spendingGain > bestGain) {
            bestGain = spendingGain;
            bestFeature = "LineNetTotal";
            bestLeft = highExpense;
            bestRight = lowExpense;
        }

        // If no substantial gain or feature found, create a leaf node
        if (bestGain <= 0.0 || bestFeature == null) {
            return CreateLeafNode(data);
        }

        return new DecisionTreeNode(bestFeature, 
            buildTree(bestLeft, currentDepth + 1), 
            buildTree(bestRight, currentDepth + 1));
    }

    @Override
    public String predictCategory(UserRecord targetRecord){
        if(rootNode == null){
            return "Model Not Trained"; //Null shield if model not trained 
        }
        this.lastDecisionPath.clear();
        return SearchInDecisionTree(rootNode, targetRecord); //Return prediction
    }

    private double calculateEntropy(List<UserRecord> data) {
        if (data == null || data.isEmpty()) return 0.0;
        Map<String, Long> categoryCounts = data.stream()
            .collect(Collectors.groupingBy(UserRecord::getCategory, Collectors.counting()));
        double entropy = 0.0;
        for (long count : categoryCounts.values()) {
            double p = (double) count / data.size();
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }

    private double calculateInformationGain(List<UserRecord> parentData, List<UserRecord> leftChild, List<UserRecord> rightChild) {
        double parentEntropy = calculateEntropy(parentData);
        double leftEntropy = calculateEntropy(leftChild);
        double rightEntropy = calculateEntropy(rightChild);
        
        double leftWeight = (double) leftChild.size() / parentData.size();
        double rightWeight = (double) rightChild.size() / parentData.size();
        
        return parentEntropy - (leftWeight * leftEntropy + rightWeight * rightEntropy);
    }

    private List<UserRecord> splitDatasetByGender(String gender , List<UserRecord> dataset){ //Split dataset into 2 groups based on genders
        return dataset.stream()
            .filter(userRecordIterator->userRecordIterator.getGender().equalsIgnoreCase(gender))
            .collect(Collectors.toList());
    }

    private List<UserRecord> splitDatasetBySpending(List <UserRecord> dataset ,Boolean isHighExpense){ //Create high and low expense lists according to parameters
        if(isHighExpense){
            return dataset.stream()
                .filter(userRecordIterator->userRecordIterator.getLineNetTotal() > PreProcessor.averageValueOfDataset)
                .collect(Collectors.toList());
        } else {
            return dataset.stream()
                .filter(userRecordIterator->userRecordIterator.getLineNetTotal() <= PreProcessor.averageValueOfDataset)
                .collect(Collectors.toList());
        }
    }

    private String predictCategoryByFrequency(List<UserRecord> dataset){
        return dataset.stream()
            .collect(Collectors.groupingBy(UserRecord::getCategory , Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Unknown");
    }

    private DecisionTreeNode CreateLeafNode(List <UserRecord> dataset){
        String category = predictCategoryByFrequency(dataset); //Create a predicted category according to given dataset
        return new DecisionTreeNode(category); //Create a leaf node with predicted category
    }

    private String SearchInDecisionTree(DecisionTreeNode currentNode , UserRecord targetUserRecord){ //Recursive function until it reaches a conclusion(leaf node)
        if(currentNode.isLeaf){
            lastDecisionPath.add("Yaprak Düğüme Ulaşıldı: Tahmin Edilen Kategori = " + currentNode.resultCategory);
            return currentNode.resultCategory; //Break recursion if current node is leaf node
        }

        if(currentNode.attribute.equals("Gender")){ //If current nodes attribution is gender
            if(targetUserRecord.getGender().equalsIgnoreCase("E")){ //If male
                lastDecisionPath.add("Dallanma -> Cinsiyet: 'E' (Erkek) olduğu için Sol Düğüme gidildi.");
                return SearchInDecisionTree(currentNode.leftNode, targetUserRecord); //Move to left(males) node
            } else { //If female
                lastDecisionPath.add("Dallanma -> Cinsiyet: 'K' (Kadın) olduğu için Sağ Düğüme gidildi.");
                return SearchInDecisionTree(currentNode.rightNode, targetUserRecord);//Move to right(females) node
            }
        } else if(currentNode.attribute.equals("LineNetTotal")){ //If current nodes attribution is Total expense
            if(targetUserRecord.getLineNetTotal() > PreProcessor.averageValueOfDataset){ //If given record's expense is higher than average 
                lastDecisionPath.add(String.format("Dallanma -> Harcama Tutarı (%.2f) > Ortalama (%.2f) olduğu için Sol Düğüme gidildi.", targetUserRecord.getLineNetTotal(), PreProcessor.averageValueOfDataset));
                return SearchInDecisionTree(currentNode.leftNode, targetUserRecord); //Move to left(high expense) leaf node
            } else { //If given record's expense is lower than average 
                lastDecisionPath.add(String.format("Dallanma -> Harcama Tutarı (%.2f) <= Ortalama (%.2f) olduğu için Sağ Düğüme gidildi.", targetUserRecord.getLineNetTotal(), PreProcessor.averageValueOfDataset));
                return SearchInDecisionTree(currentNode.rightNode, targetUserRecord); //Move to right(lower expense) leaf node
            }
        }

        return "Unknown";
    }

    public String predictCategoryForUserInputWithDecisionTree(String genderInput, double lineNetTotalInput){
        UserRecord inputRecord = new UserRecord(0, genderInput, lineNetTotalInput, null);

        DecisionTreeAlgorithm decisionTree = new DecisionTreeAlgorithm(maxDepth);
        decisionTree.trainModel(dataSet);

        String result = decisionTree.predictCategory(inputRecord);
        this.lastDecisionPath = decisionTree.getLastDecisionPath();
        return result;
    }
}
