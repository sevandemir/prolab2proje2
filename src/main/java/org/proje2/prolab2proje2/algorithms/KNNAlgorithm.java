package org.proje2.prolab2proje2.algorithms;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.proje2.prolab2proje2.data.UserRecord;

public class KNNAlgorithm{

    private double calculateEuclidDistance(UserRecord firstRecord , UserRecord secondRecord){

        double genderDifferenceScore=Math.pow(firstRecord.getEncodedGender()-secondRecord.getEncodedGender(),2); // (x1-x2)²

        double normalizedLineTotalScore=Math.pow(firstRecord.getNormalizedLineTotal()-secondRecord.getNormalizedLineTotal(),2); //(y1-y2)²

        return Math.sqrt(normalizedLineTotalScore+genderDifferenceScore); //sqrt(a+b)
    }   

    public List<UserRecord> calculateKNearestNeighbours(UserRecord targetRecord , List <UserRecord> allUserRecords , int K){

        List <UserRecord> KNeighbourList;
    
        KNeighbourList=allUserRecords.stream() //Filter eliminates all items which doesnt fulfill the condition
                        .filter(userRecordIterator->userRecordIterator.getClientCode()!=targetRecord.getClientCode()) //Iterate through list with lambda(->) function 
                        .sorted(Comparator.comparingDouble(userRecordIterator -> calculateEuclidDistance(targetRecord , userRecordIterator))) //Sort items according to euclid distance to target
                        .limit(K) //Limit the list with only first K items 
                        .collect(Collectors.toList()); //Wrap remaining items in a new UserRecord list 
                        
        return KNeighbourList;
    }
}
