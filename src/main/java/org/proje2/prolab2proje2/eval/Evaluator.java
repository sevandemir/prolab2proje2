package org.proje2.prolab2proje2.eval;

import java.util.Collections;
import java.util.List;

import org.proje2.prolab2proje2.algorithms.DecisionTreeAlgorithm;
import org.proje2.prolab2proje2.algorithms.KNNAlgorithm;
import org.proje2.prolab2proje2.data.UserRecord;

import javafx.scene.control.TextArea;

public class Evaluator{

    public void evaluatePerformance(int K, List<UserRecord> fullDataset , TextArea logArea){

        Collections.shuffle(fullDataset); //Shuffle dataset to be sure dataset is all random

        int splitPoint = (int)(fullDataset.size()*0.8); //Split dataset %80 training - %20 testing

        List<UserRecord> trainingData=fullDataset.subList(0, splitPoint); //Training dataset(%80 of full database)
        List<UserRecord> testData=fullDataset.subList(splitPoint,fullDataset.size()); //Testing dataset(%20 of full database)

        //Decision Tree Algorithm 
        long decisionTreeStartTime = System.nanoTime(); //Decision Tree starting time

        DecisionTreeAlgorithm DecisionTree = new DecisionTreeAlgorithm(); //Create new Decision Tree
        DecisionTree.trainModel(trainingData); //Train model with training data

        int decisionTreeCorrectPredictions=0; //Correct predictions of Decision Tree model

        for(UserRecord userRecord :testData){ //Take each user record from testing data 

            if(userRecord.getCategory().equalsIgnoreCase(DecisionTree.predictCategory(userRecord))){ //Compare predictions and real category
                decisionTreeCorrectPredictions++; //Increment correct predictions if guessed correct
            }
        }
        long decisionTreeFinishTime=System.nanoTime(); //Decision tree finishing time

        //KNN Algorithm
        long KNNAlgorithmStartTime = System.nanoTime(); //KNN starting time

        KNNAlgorithm KNNAlgorithm = new KNNAlgorithm(K); //Create KNN model
        KNNAlgorithm.trainModel(trainingData); //Train model with training data

        int KNNAlgorithmCorrectPredictions=0; //Correct predictions of KNN model

        for(UserRecord userRecord : testData){ //Take each user record from testing data
            if(userRecord.getCategory().equalsIgnoreCase(KNNAlgorithm.predictCategory(userRecord))){ //Compare predictions and real category
                KNNAlgorithmCorrectPredictions++; //Increment correct predictions if guessed correct
            }
        }

        Long KNNAlgorithmFinishTime=System.nanoTime(); //KNN finishing time

        printTestResults("KNN Algorithm", KNNAlgorithmStartTime, KNNAlgorithmFinishTime, KNNAlgorithmCorrectPredictions, testData.size(), logArea); //Print out KNN algorithm results
        printTestResults("Decision Tree Algorithm", decisionTreeStartTime, decisionTreeFinishTime, decisionTreeCorrectPredictions, testData.size() , logArea); //Print out Decision Tree results 
    }

    private void printTestResults(String name, long startTime, long endTime, int correctPredictions, int totalData, TextArea logArea){

        double predictionAccuracy=(double)correctPredictions/ (double)totalData * 100.00; //Double casting to make double dividing(Not integer dividing) and in % type
        double predictionDuration= (endTime-startTime) / (1_000_000.0); //Format nanoseconds to miliseconds for better readability

        logArea.appendText(name + " Prediction Accuracy: " + String.format("%.2f", predictionAccuracy)+"\n"); //Format accuracy to 2 numbers after . 
        logArea.appendText(name + " Prediction Duration: " + String.format("%.4f", predictionDuration)+ "ms\n"); //Format duration to 4 numbers after . 
    }
}
