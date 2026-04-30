package org.proje2.prolab2proje2.algorithms;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.proje2.prolab2proje2.data.PreProcessor;
import org.proje2.prolab2proje2.data.UserRecord;

public class DecisionTreeAlgorithm implements IClassifier{

    private DecisionTreeNode rootNode;
    private List<UserRecord> dataSet;
    private int maxDepth = 2; // Default depth

    public DecisionTreeAlgorithm() {}

    public DecisionTreeAlgorithm(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void trainModel(List<UserRecord> trainingDateset){
        dataSet = trainingDateset;
        rootNode = buildTree(trainingDateset, 0);
    }

    private DecisionTreeNode buildTree(List<UserRecord> data, int currentDepth) {
        // Base case: Leaf node if depth reached or data is pure/empty
        if (currentDepth >= maxDepth || data.isEmpty()) {
            return CreateLeafNode(data);
        }

        // Depth 0: Split by Gender
        if (currentDepth == 0) {
            List<UserRecord> maleList = splitDatasetByGender("E", data);
            List<UserRecord> femaleList = splitDatasetByGender("K", data);
            
            return new DecisionTreeNode("Gender", 
                buildTree(maleList, currentDepth + 1), 
                buildTree(femaleList, currentDepth + 1));
        } 
        // Depth 1: Split by Spending
        else if (currentDepth == 1) {
            List<UserRecord> highExpense = splitDatasetBySpending(data, true);
            List<UserRecord> lowExpense = splitDatasetBySpending(data, false);
            
            return new DecisionTreeNode("LineNetTotal", 
                buildTree(highExpense, currentDepth + 1), 
                buildTree(lowExpense, currentDepth + 1));
        }

        return CreateLeafNode(data);
    }

    @Override
    public String predictCategory(UserRecord targetRecord){

        if(rootNode==null){
            return "Model Not Trained"; //Null shield if model not trained 
        }
        return SearchInDecisionTree(rootNode, targetRecord); //Return prediction
    }

    private List<UserRecord> splitDatasetByGender(String gender , List<UserRecord> dataset){ //Split dataset into 2 groups based on genders

        List <UserRecord> genderFilteredList=dataset.stream() //Start dataset stream
                                            .filter(userRecordIterator->userRecordIterator.getGender().equalsIgnoreCase(gender)) //Filter stream according to given gender
                                            .collect(Collectors.toList()); //Collect remaining items in a list 

        return genderFilteredList;
    }

    private List<UserRecord> splitDatasetBySpending(List <UserRecord> dataset ,Boolean isHighExpense){ //Create high and low expense lists according to parameters

        if(isHighExpense){

            List<UserRecord> highExpenseRecords=dataset.stream() //Start dataset stream
                                                .filter(userRecordIterator->userRecordIterator.getLineNetTotal()>PreProcessor.averageValueOfDataset) //If higher than thresold then its high expense
                                                .collect(Collectors.toList()); //Collect remaining items in a list 
            return highExpenseRecords;
        }
        
        else{
            List <UserRecord> lowExpenseRecords=dataset.stream()
                                                .filter(userRecordIterator->userRecordIterator.getLineNetTotal()<=PreProcessor.averageValueOfDataset) //If lower than average then its low expense
                                                .collect(Collectors.toList());
            return lowExpenseRecords;                                    
        }
    }

    private String predictCategoryByFrequency(List<UserRecord> dataset){

        String category=dataset.stream() //Start dataset stream
                        .collect(Collectors.groupingBy(UserRecord::getCategory , Collectors.counting())) //Group by category:count key-value pairs 
                        .entrySet().stream() //Start key-value entry stream 
                        .max(Map.Entry.comparingByValue()) //Compare Map.Entry -> Take maximum "Gida:4"
                        .map(Map.Entry::getKey) //Get entrys key : "Gida"
                        .orElse("Unknown");

        return category; //Return key
    }

    private DecisionTreeNode CreateLeafNode(List <UserRecord> dataset){

        String category = predictCategoryByFrequency(dataset); //Create a predicted category according to given dataset

        return new DecisionTreeNode(category); //Create a leaf node with predicted category
    }

    private DecisionTreeNode CreateSpendingBranches(List <UserRecord> dataset){

        List<UserRecord> highExpenseUserRecords=splitDatasetBySpending(dataset, true); //Create high expense records 
        List<UserRecord> lowExpenseUserRecords=splitDatasetBySpending(dataset, false); //Create low expense records 

        DecisionTreeNode highExpenseLeaf=CreateLeafNode(highExpenseUserRecords); //Create leaf node for high expenses 
        DecisionTreeNode lowExpenseLeaf=CreateLeafNode(lowExpenseUserRecords); //Create leaf node for low expenses

        DecisionTreeNode lastBranch=new DecisionTreeNode("LineNetTotal",highExpenseLeaf,lowExpenseLeaf); //Create last branch for 2 leaf nodes
        return lastBranch;
    }

    private String SearchInDecisionTree(DecisionTreeNode currentNode , UserRecord targetUserRecord){ //Recursive function until it reaches a conclusion(leaf node)

        if(currentNode.isLeaf){
            return currentNode.resultCategory; //Break recursion if current node is leaf node
        }

        if(currentNode.attribute.equals("Gender")){ //If current nodes attribution is gender

            if(targetUserRecord.getGender().equalsIgnoreCase("E")){ //If male
                return SearchInDecisionTree(currentNode.leftNode, targetUserRecord); //Move to left(males) node
            }
            else{//If female
                return SearchInDecisionTree(currentNode.rightNode, targetUserRecord);//Move to right(females) node
            }
        }
        else if(currentNode.attribute.equals("LineNetTotal")){ //If current nodes attribution is Total expense
            
            if(targetUserRecord.getLineNetTotal()>PreProcessor.averageValueOfDataset){ //If given record's expense is higher than average 
                return SearchInDecisionTree(currentNode.leftNode, targetUserRecord); //Move to left(high expense) leaf node
            }
            else if(targetUserRecord.getLineNetTotal()<=PreProcessor.averageValueOfDataset){ //If given record's expense is lower than average 
                return SearchInDecisionTree(currentNode.rightNode, targetUserRecord); //Move to right(lower expense) leaf node
            }
        }

        return "Unknown";
    }

    public String predictCategoryForUserInputWithDecisionTree(String genderInput, double lineNetTotalInput){

        String predictedCategoryForUser;

        UserRecord inputRecord = new UserRecord(0, genderInput, lineNetTotalInput, null);

        DecisionTreeAlgorithm DecisionTree = new DecisionTreeAlgorithm();
        DecisionTree.trainModel(dataSet);

        predictedCategoryForUser=DecisionTree.predictCategory(inputRecord);

        return predictedCategoryForUser;
    }
}
