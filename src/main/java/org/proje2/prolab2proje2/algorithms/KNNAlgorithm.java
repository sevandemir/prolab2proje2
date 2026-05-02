package org.proje2.prolab2proje2.algorithms;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.proje2.prolab2proje2.data.PreProcessor;
import org.proje2.prolab2proje2.data.UserRecord;

/**
 * K-Nearest Neighbors (KNN) Algorithm.
 * This class predicts the category of new records based on the similarities in the dataset.
 */
public class KNNAlgorithm extends BaseAlgorithm {

    private final int k;
    private List<UserRecord> lastNeighbours;

    /**
     * Constructor setting the number of neighbors (K) for the algorithm.
     * @param k The number of nearest neighbors to be considered.
     */
    public KNNAlgorithm(int k){
        this.k = Math.min(k, 25); 
    }

    /**
     * Returns the nearest neighbors found in the last prediction.
     * @return List of nearest neighbor records.
     */
    public List<UserRecord> getLastNeighbours() {
        return lastNeighbours;
    }

    /**
     * Predicts the product category for a given user record.
     * @param targetRecord The user record to be predicted.
     * @return Predicted category name.
     */
    @Override
    public String predictCategory(UserRecord targetRecord){
        if (dataSet == null || dataSet.isEmpty()) {
            return "Unknown";
        }
        // Calculate and store nearest neighbors
        this.lastNeighbours = calculateKNearestNeighbours(targetRecord, dataSet, k);
        // Return majority category among neighbors
        return predictCategoryByFrequency(lastNeighbours);
    }

    /**
     * Calculates the Euclidean distance between two user records.
     * @param firstRecord The first user.
     * @param secondRecord The second user.
     * @return Calculated Euclidean distance.
     */
    public double calculateEuclidDistance(UserRecord firstRecord , UserRecord secondRecord){
        // Square of the difference between genders
        double genderDifferenceScore=Math.pow(firstRecord.getEncodedGender()-secondRecord.getEncodedGender(),2);
        // Square of the difference between normalized line totals
        double normalizedLineTotalScore=Math.pow(firstRecord.getNormalizedLineTotal()-secondRecord.getNormalizedLineTotal(),2);

        // Square root of the sum of both scores (sqrt(a+b))
        return Math.sqrt(normalizedLineTotalScore+genderDifferenceScore);
    }   

    /**
     * Finds the top K nearest neighbors for a target record in the full dataset.
     * @param targetRecord Target user record.
     * @param allUserRecords Available past customer records.
     * @param K Number of neighbors to find.
     * @return List of the K nearest neighbors.
     */
    public List<UserRecord> calculateKNearestNeighbours(UserRecord targetRecord , List <UserRecord> allUserRecords , int K){
        List <UserRecord> KNeighbourList;
    
        KNeighbourList=allUserRecords.stream()
                        // Exclude self by matching ClientCode
                        .filter(userRecordIterator->userRecordIterator.getClientCode()!=targetRecord.getClientCode())
                        // Sort by ascending Euclidean distance to target
                        .sorted(Comparator.comparingDouble(userRecordIterator -> calculateEuclidDistance(targetRecord , userRecordIterator)))
                        // Take the first K items (closest ones)
                        .limit(K) 
                        .collect(Collectors.toList());
                        
        return KNeighbourList;
    }


    /**
     * Handles single user prediction via manual inputs from UI and processes with KNN.
     * @param genderInput Given gender (E/K).
     * @param lineNetTotalInput Given spending amount.
     * @return Predicted category.
     */
    public String predictCategoryForUserInputWithKNN(String genderInput, Double lineNetTotalInput){
        // Encode gender and normalize spending
        int encodedGenderInput=PreProcessor.encodeGenderFromUserInput(genderInput);
        double normalizedLineNetTotalInput=PreProcessor.normalizeInputFromUserInput(lineNetTotalInput);

        // Instantiate new user record and set properties
        UserRecord inputRecord = new UserRecord(0, genderInput, lineNetTotalInput, null);
        inputRecord.setEncodedGender(encodedGenderInput);
        inputRecord.setNormalizedLineTotal(normalizedLineNetTotalInput);

        // Predict with currently trained model
        return predictCategory(inputRecord);
    }
}
