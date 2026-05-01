package org.proje2.prolab2proje2.algorithms;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.proje2.prolab2proje2.data.PreProcessor;
import org.proje2.prolab2proje2.data.UserRecord;

public class KNNAlgorithm extends BaseAlgorithm {

    private int k;
    private List<UserRecord> lastNeighbours;

    public KNNAlgorithm(int k){
        this.k = k; //Class' own K and parameter K
    }

    public List<UserRecord> getLastNeighbours() {
        return lastNeighbours;
    }

    @Override
    public String predictCategory(UserRecord targetRecord){
        if (dataSet == null || dataSet.isEmpty()) {
            return "Unknown";
        }
        this.lastNeighbours = calculateKNearestNeighbours(targetRecord, dataSet, k);
        return predictCategoryByFrequency(lastNeighbours);
    }

    private double calculateEuclidDistance(UserRecord firstRecord , UserRecord secondRecord){
        double genderDifferenceScore=Math.pow(firstRecord.getEncodedGender()-secondRecord.getEncodedGender(),2); // (x1-x2)²
        double normalizedLineTotalScore=Math.pow(firstRecord.getNormalizedLineTotal()-secondRecord.getNormalizedLineTotal(),2); //(y1-y2)²

        return Math.sqrt(normalizedLineTotalScore+genderDifferenceScore); //sqrt(a+b)
    }   

    public double getEuclidDistance(UserRecord firstRecord, UserRecord secondRecord) {
        return calculateEuclidDistance(firstRecord, secondRecord);
    }

    public List<UserRecord> calculateKNearestNeighbours(UserRecord targetRecord , List <UserRecord> allUserRecords , int K){
        List <UserRecord> KNeighbourList;
    
        KNeighbourList=allUserRecords.stream()//Iterate in whole list with stream                                     //Filter eliminates all items which doesnt fulfill the condition
                        .filter(userRecordIterator->userRecordIterator.getClientCode()!=targetRecord.getClientCode()) //Apply condition to userRecordIterator and filter if not fullfills
                        .sorted(Comparator.comparingDouble(userRecordIterator -> calculateEuclidDistance(targetRecord , userRecordIterator))) //Sort items according to euclid distance to target
                        .limit(K) //Limit the list with only first K items 
                        .collect(Collectors.toList()); //Wrap remaining items in a new UserRecord list 
                        
        return KNeighbourList;
    }

    public String predictCategoryByFrequency(List <UserRecord> KNeighbours){
        String predictedCategory;

        Map <String,Long> categoryCounts=KNeighbours.stream()
                                        .map(UserRecord::getCategory) //Turn each UserRecord into category
                                        .collect(Collectors.groupingBy(Function.identity(),Collectors.counting())); //Group by -> Function identity : "Gida" Counting : 5 -> Gida : 5 

        predictedCategory = categoryCounts.entrySet().stream()
                            .max(Map.Entry.comparingByValue()) //Take the maximum according to values -> Gida : 5
                            .map(Map.Entry::getKey) //Get key value -> "Gida" not the number 5
                            .orElse("Unknown"); //Shield 

        return predictedCategory;
    }

    public String predictCategoryForUserInputWithKNN(String genderInput, Double lineNetTotalInput, int kInput){
        String predictedCategoryForUser;

        int encodedGenderInput=PreProcessor.encodeGenderFromUserInput(genderInput);
        double normalizedLineNetTotalInput=PreProcessor.normalizeInputFromUserInput(lineNetTotalInput);

        UserRecord inputRecord = new UserRecord(0, genderInput, lineNetTotalInput, null);
        inputRecord.setEncodedGender(encodedGenderInput);
        inputRecord.setNormalizedLineTotal(normalizedLineNetTotalInput);

        KNNAlgorithm knn = new KNNAlgorithm(kInput); 
        knn.trainModel(dataSet);

        predictedCategoryForUser=knn.predictCategory(inputRecord);
        this.lastNeighbours = knn.getLastNeighbours();

        return predictedCategoryForUser;
    }
}
