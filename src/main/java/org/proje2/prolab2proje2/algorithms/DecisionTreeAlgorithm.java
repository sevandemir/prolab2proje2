package org.proje2.prolab2proje2.algorithms;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.proje2.prolab2proje2.data.UserRecord;

public class DecisionTreeAlgorithm implements IClassifier{

    private DecisionTreeNode rootNode;

    @Override
    public void trainModel(List<UserRecord> trainingDateset){
        
        List<UserRecord> maleList = splitDatasetByGender("E", trainingDateset);
        List<UserRecord> femaleList=splitDatasetByGender("K", trainingDateset);

        DecisionTreeNode maleBranch=CreateSpendingBranches(maleList);
        DecisionTreeNode femaleBranch=CreateSpendingBranches(femaleList);

        rootNode=new DecisionTreeNode("Gender",maleBranch,femaleBranch);
    }   

    @Override
    public String predictCategory(UserRecord targetRecord){

        if(rootNode==null){
            return "Model Not Trained";
        }
        return SearchInDecisionTree(rootNode, targetRecord);
    }

    private List<UserRecord> splitDatasetByGender(String gender , List<UserRecord> dataset){

        List <UserRecord> genderFilteredList=dataset.stream()
                                            .filter(userRecordIterator->userRecordIterator.getGender().equalsIgnoreCase(gender))
                                            .collect(Collectors.toList());

        return genderFilteredList;
    }

    private List<UserRecord> splitDatasetBySpending(List <UserRecord> dataset ,Boolean isHighExpense){

        if(isHighExpense){

            List<UserRecord> highExpenseRecords=dataset.stream()
                                                .filter(userRecordIterator->userRecordIterator.getLineNetTotal()>=50)
                                                .collect(Collectors.toList());
            return highExpenseRecords;
        }
        
        else{
            List <UserRecord> lowExpenseRecords=dataset.stream()
                                                .filter(userRecordIterator->userRecordIterator.getLineNetTotal()<50)
                                                .collect(Collectors.toList());
            return lowExpenseRecords;                                    
        }
    }

    private String predictCategoryByFrequency(List<UserRecord> dataset){

        String category=dataset.stream()
                        .collect(Collectors.groupingBy(UserRecord::getCategory , Collectors.counting()))
                        .entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("Unknown");

        return category;
    }

    private DecisionTreeNode CreateLeafNode(List <UserRecord> dataset){

        String category = predictCategoryByFrequency(dataset);

        return new DecisionTreeNode(category);  
    }

    private DecisionTreeNode CreateSpendingBranches(List <UserRecord> dataset){

        List<UserRecord> highExpenUserRecords=splitDatasetBySpending(dataset, true);
        List<UserRecord> lowExpenUserRecords=splitDatasetBySpending(dataset, false);

        DecisionTreeNode highExpenseLeaf=CreateLeafNode(highExpenUserRecords);
        DecisionTreeNode lowExpenseLeaf=CreateLeafNode(lowExpenUserRecords);

        DecisionTreeNode lastBranch=new DecisionTreeNode("LineNetTotal",highExpenseLeaf,lowExpenseLeaf);
        return lastBranch;
    }

    private String SearchInDecisionTree(DecisionTreeNode currentNode , UserRecord targetUserRecord){

        if(currentNode.isLeaf){
            return currentNode.resultCategory;
        }

        if(currentNode.attribute.equals("Gender")){

            if(targetUserRecord.getGender().equals("E")){
                return SearchInDecisionTree(currentNode.leftNode, targetUserRecord);
            }
            else{
                return SearchInDecisionTree(currentNode.rightNode, targetUserRecord);
            }
        }
        else if(currentNode.attribute.equals("LineNetTotal")){
            
            if(targetUserRecord.getLineNetTotal()>=50){
                return SearchInDecisionTree(currentNode.leftNode, targetUserRecord);
            }
            else if(targetUserRecord.getLineNetTotal()<50){
                return SearchInDecisionTree(currentNode.rightNode, targetUserRecord);
            }
        }

        return "Unknown";
    }

}
