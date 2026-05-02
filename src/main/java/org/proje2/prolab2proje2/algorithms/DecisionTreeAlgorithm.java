package org.proje2.prolab2proje2.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.proje2.prolab2proje2.data.UserRecord;

/**
 * DecisionTreeAlgorithm implements a classification model using entropy and information gain.
 */
public class DecisionTreeAlgorithm extends BaseAlgorithm {

    private DecisionTreeNode rootNode;
    private final int maxDepth; // Default depth
    private final List<String> lastDecisionPath = new ArrayList<>();

    /**
     * Constructs a Decision Tree with a specified maximum depth.
     * @param maxDepth Maximum recursion depth allowed for building nodes.
     */
    public DecisionTreeAlgorithm(int maxDepth) {
        this.maxDepth = Math.min(maxDepth, 25);
    }

    /**
     * Returns the traversal and decisions taken during the most recent prediction.
     * @return List of branching decision string details.
     */
    public List<String> getLastDecisionPath() {
        return lastDecisionPath;
    }

    /**
     * Builds and trains the decision tree structure from the provided training data.
     * @param trainingDataset Dataset to build tree from.
     */
    @Override
    public void trainModel(List<UserRecord> trainingDataset){
        this.dataSet = trainingDataset;
        this.rootNode = buildTree(trainingDataset, 0);
    }

    /**
     * Recursively builds the decision tree using the best feature split.
     * @param data Subset of records to split.
     * @param currentDepth Current recursion depth.
     * @return DecisionTreeNode (internal branch or leaf node).
     */
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
        double chosenThreshold = 0.0;

        // Try Gender split
        List<UserRecord> maleList = splitDatasetByGender("E", data);
        List<UserRecord> femaleList = splitDatasetByGender("K", data);
        if (!maleList.isEmpty() && !femaleList.isEmpty()) {
            double genderGain = calculateInformationGain(data, maleList, femaleList);
            if (genderGain > bestGain) {
                bestGain = genderGain;
                bestFeature = "Gender";
                bestLeft = maleList;
                bestRight = femaleList;
            }
        }

        // Try several dynamic thresholds for LineNetTotal
        double minTotal = data.stream().mapToDouble(UserRecord::getLineNetTotal).min().orElse(0.0);
        double maxTotal = data.stream().mapToDouble(UserRecord::getLineNetTotal).max().orElse(0.0);
        double subsetAvg = data.stream().mapToDouble(UserRecord::getLineNetTotal).average().orElse(0.0);

        double[] candidates = {
            subsetAvg,
            minTotal + (maxTotal - minTotal) * 0.25,
            minTotal + (maxTotal - minTotal) * 0.50,
            minTotal + (maxTotal - minTotal) * 0.75
        };

        double bestSpendingGain = -1.0;
        double bestSpendingThreshold = subsetAvg;
        List<UserRecord> spendingBestLeft = null;
        List<UserRecord> spendingBestRight = null;

        for (double thresh : candidates) {
            List<UserRecord> high = splitDatasetBySpending(data, true, thresh);
            List<UserRecord> low = splitDatasetBySpending(data, false, thresh);
            if (high.isEmpty() || low.isEmpty()) continue;
            double gain = calculateInformationGain(data, high, low);
            if (gain > bestSpendingGain) {
                bestSpendingGain = gain;
                bestSpendingThreshold = thresh;
                spendingBestLeft = high;
                spendingBestRight = low;
            }
        }

        if (bestSpendingGain > bestGain) {
            bestGain = bestSpendingGain;
            bestFeature = "LineNetTotal";
            bestLeft = spendingBestLeft;
            bestRight = spendingBestRight;
            chosenThreshold = bestSpendingThreshold;
        }

        // If no substantial gain or feature found, create a leaf node
        if (bestGain <= 0.0) {
            return CreateLeafNode(data);
        }

        if ("Gender".equals(bestFeature)) {
            return new DecisionTreeNode(bestFeature, 
                buildTree(bestLeft, currentDepth + 1), 
                buildTree(bestRight, currentDepth + 1));
        } else {
            return new DecisionTreeNode(bestFeature, chosenThreshold,
                buildTree(bestLeft, currentDepth + 1), 
                buildTree(bestRight, currentDepth + 1));
        }
    }

    /**
     * Predicts the product category for a given user record.
     * @param targetRecord The user record to be predicted.
     * @return Predicted category name.
     */
    @Override
    public String predictCategory(UserRecord targetRecord){
        if(rootNode == null){
            return "Model Not Trained"; //Null shield if model not trained 
        }
        this.lastDecisionPath.clear();
        return SearchInDecisionTree(rootNode, targetRecord); //Return prediction
    }

    /**
     * Calculates Shannon Entropy to measure impurity in data.
     * @param data Subset of user records.
     * @return Calculated entropy value.
     */
    private double calculateUncertainty(List<UserRecord> data) {
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

    /**
     * Calculates Information Gain using parent and child entropies.
     * @param parentData Parent dataset.
     * @param leftChild Left branch dataset.
     * @param rightChild Right branch dataset.
     * @return Difference in entropy.
     */
    private double calculateInformationGain(List<UserRecord> parentData, List<UserRecord> leftChild, List<UserRecord> rightChild) {
        double parentEntropy = calculateUncertainty(parentData);
        double leftEntropy = calculateUncertainty(leftChild);
        double rightEntropy = calculateUncertainty(rightChild);
        
        double leftWeight = (double) leftChild.size() / parentData.size();
        double rightWeight = (double) rightChild.size() / parentData.size();
        
        return parentEntropy - (leftWeight * leftEntropy + rightWeight * rightEntropy);
    }

    /**
     * Filters user records matching the specified gender.
     * @param gender Target gender string.
     * @param dataset Records to filter.
     * @return Filtered list.
     */
    private List<UserRecord> splitDatasetByGender(String gender , List<UserRecord> dataset){ 
        return dataset.stream()
            .filter(userRecordIterator->userRecordIterator.getGender().equalsIgnoreCase(gender))
            .collect(Collectors.toList());
    }

    private List<UserRecord> splitDatasetBySpending(List <UserRecord> dataset ,Boolean isHighExpense, double threshold){
        if(isHighExpense){
            return dataset.stream()
                .filter(userRecordIterator->userRecordIterator.getLineNetTotal() > threshold)
                .collect(Collectors.toList());
        } else {
            return dataset.stream()
                .filter(userRecordIterator->userRecordIterator.getLineNetTotal() <= threshold)
                .collect(Collectors.toList());
        }
    }

    /**
     * Creates a leaf node with the most frequent category.
     * @param dataset Leaf dataset.
     * @return DecisionTreeNode leaf.
     */
    private DecisionTreeNode CreateLeafNode(List <UserRecord> dataset){
        String category = predictCategoryByFrequency(dataset); 
        return new DecisionTreeNode(category); 
    }

    /**
     * Recursively traverses nodes down the tree based on attributes of the target user record.
     * @param currentNode Node to start searching from.
     * @param targetUserRecord Input user record.
     * @return Final leaf result category string.
     */
    private String SearchInDecisionTree(DecisionTreeNode currentNode , UserRecord targetUserRecord){ 
        if(currentNode.isLeaf){
            lastDecisionPath.add("Yaprak Düğüme Ulaşıldı: Tahmin Edilen Kategori = " + currentNode.resultCategory);
            return currentNode.resultCategory; 
        }

        if(currentNode.attribute.equals("Gender")){ 
            if(targetUserRecord.getGender().equalsIgnoreCase("E")){ 
                lastDecisionPath.add("Dallanma -> Cinsiyet: 'E' (Erkek) olduğu için Sol Düğüme gidildi.");
                return SearchInDecisionTree(currentNode.leftNode, targetUserRecord); 
            } else { 
                lastDecisionPath.add("Dallanma -> Cinsiyet: 'K' (Kadın) olduğu için Sağ Düğüme gidildi.");
                return SearchInDecisionTree(currentNode.rightNode, targetUserRecord);
            }
        } else if(currentNode.attribute.equals("LineNetTotal")){ 
            if(targetUserRecord.getLineNetTotal() > currentNode.threshold){ 
                lastDecisionPath.add(String.format("Dallanma -> Harcama Tutarı (%.2f) > Ortalama (%.2f) olduğu için Sol Düğüme gidildi.", targetUserRecord.getLineNetTotal(), currentNode.threshold));
                return SearchInDecisionTree(currentNode.leftNode, targetUserRecord); 
            } else { 
                lastDecisionPath.add(String.format("Dallanma -> Harcama Tutarı (%.2f) <= Ortalama (%.2f) olduğu için Sağ Düğüme gidildi.", targetUserRecord.getLineNetTotal(), currentNode.threshold));
                return SearchInDecisionTree(currentNode.rightNode, targetUserRecord); 
            }
        }

        return "Unknown";
    }

    /**
     * Handles single user prediction via manual inputs from UI and processes with Decision Tree.
     * @param genderInput Given gender (E/K).
     * @param lineNetTotalInput Given spending amount.
     * @return Predicted category.
     */
    public String predictCategoryForUserInputWithDecisionTree(String genderInput, double lineNetTotalInput){
        UserRecord inputRecord = new UserRecord(0, genderInput, lineNetTotalInput, null);
        return predictCategory(inputRecord);
    }
}
