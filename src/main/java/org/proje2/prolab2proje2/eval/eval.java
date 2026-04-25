package org.proje2.prolab2proje2.eval;

import java.util.Collections;
import java.util.List;

import org.proje2.prolab2proje2.algorithms.DecisionTreeAlgorithm;
import org.proje2.prolab2proje2.algorithms.KNNAlgorithm;
import org.proje2.prolab2proje2.data.UserRecord;

public class eval{

    public void evaluatePerformance(List<UserRecord> fullDataset){

        Collections.shuffle(fullDataset);

        int splitPoint = (int)(fullDataset.size()*0.8);

        List<UserRecord> trainingData=fullDataset.subList(0, splitPoint);
        List<UserRecord> testData=fullDataset.subList(splitPoint,fullDataset.size());

        long decisionTreeStartTime = System.nanoTime();

        DecisionTreeAlgorithm DecisionTree = new DecisionTreeAlgorithm();
        DecisionTree.trainModel(trainingData);

        int decisionTreeCorrectPredictions=0;

        for(UserRecord userRecord :testData){

            if(userRecord.getCategory().equalsIgnoreCase(DecisionTree.predictCategory(userRecord))){
                decisionTreeCorrectPredictions++;
            }
        }
        long decisionTreeFinishTime=System.nanoTime();

        int K=5;

        long KNNAlgorithmStartTime = System.nanoTime();

        KNNAlgorithm KNNAlgorithm = new KNNAlgorithm(K);
        KNNAlgorithm.trainModel(trainingData);

        int KNNAlgorithmCorrectPredictions=0;

        for(UserRecord userRecord : testData){
            if(userRecord.getCategory().equalsIgnoreCase(KNNAlgorithm.predictCategory(userRecord))){
                KNNAlgorithmCorrectPredictions++;
            }
        }

        Long KNNAlgorithmFinishTime=System.nanoTime();

        printTestResults("KNN Algorithm", KNNAlgorithmStartTime, KNNAlgorithmFinishTime, KNNAlgorithmCorrectPredictions, testData.size());
        printTestResults("Decision Tree Algorithm", decisionTreeStartTime, decisionTreeFinishTime, decisionTreeCorrectPredictions, testData.size());
    }

    void printTestResults(String name, long startTime, long endTime, int correctPredictions, int totalData){

        double predictionAccuracy=(double)correctPredictions/ (double)totalData * 100.00;
        double predictionDuration= (endTime-startTime) / (1_000_000.0);

        System.out.println(name + " Prediction Accuracy: " + String.format("%.2f", predictionAccuracy));
        System.out.println(name + " Prediction Duration: " + String.format("%.4f", predictionDuration)+ "ms");
    }
}
