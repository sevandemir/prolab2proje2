package org.proje2.prolab2proje2.alghorithm;

import org.proje2.prolab2proje2.data.UserRecord;

public class KNNAlgorithm{

    double euclidDistance;

    private double CalculateEuclidDistance(UserRecord firstRecord , UserRecord secondRecord){

        double genderDifferenceScore=Math.pow(firstRecord.getEncodedGender()-secondRecord.getEncodedGender(),2);

        double normalizedLineTotalScore=Math.pow(firstRecord.getNormalizedLineTotal()-secondRecord.getNormalizedLineTotal(),2);

        euclidDistance = Math.sqrt(normalizedLineTotalScore+genderDifferenceScore);

        return euclidDistance;
    }













}
