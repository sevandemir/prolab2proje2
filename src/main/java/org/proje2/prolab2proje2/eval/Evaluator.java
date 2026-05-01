package org.proje2.prolab2proje2.eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.proje2.prolab2proje2.algorithms.DecisionTreeAlgorithm;
import org.proje2.prolab2proje2.algorithms.KNNAlgorithm;
import org.proje2.prolab2proje2.data.UserRecord;

import javafx.scene.control.TextArea;

public class Evaluator {

    public List<EvaluationResult> evaluatePerformance(int K, int maxDepth, List<UserRecord> fullDataset, TextArea logArea) {
        List<EvaluationResult> results = new ArrayList<>();
        Collections.shuffle(fullDataset); // Shuffle dataset to ensure randomness

        int splitPoint = (int) (fullDataset.size() * 0.8); // Split dataset: 80% training, 20% testing

        List<UserRecord> trainingData = new ArrayList<>(fullDataset.subList(0, splitPoint));
        List<UserRecord> testData = new ArrayList<>(fullDataset.subList(splitPoint, fullDataset.size()));

        return evaluatePerformanceSeparate(K, maxDepth, trainingData, testData, logArea);
    }

    public List<EvaluationResult> evaluatePerformanceSeparate(int K, int maxDepth, List<UserRecord> trainingData, List<UserRecord> testData, TextArea logArea) {
        List<EvaluationResult> results = new ArrayList<>();

        // --- KNN Algorithm ---
        Runtime.getRuntime().gc(); // Suggest Garbage Collector
        long knnMemBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long knnStartTime = System.nanoTime();

        KNNAlgorithm knn = new KNNAlgorithm(K);
        knn.trainModel(trainingData);

        int knnCorrect = 0;
        ConfusionMatrix knnMatrix = new ConfusionMatrix();
        for (UserRecord record : testData) {
            String actual = record.getCategory();
            String predicted = knn.predictCategory(record);
            knnMatrix.addPrediction(actual, predicted);
            if (actual != null && actual.equalsIgnoreCase(predicted)) {
                knnCorrect++;
            }
        }
        long knnEndTime = System.nanoTime();
        long knnMemAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double knnMemoryUsedMB = Math.max(0, (knnMemAfter - knnMemBefore) / (1024.0 * 1024.0));

        results.add(calculateResult("KNN Algorithm", knnStartTime, knnEndTime, knnCorrect, testData.size(), knnMemoryUsedMB, knnMatrix, logArea));

        // --- Decision Tree Algorithm ---
        Runtime.getRuntime().gc(); // Suggest Garbage Collector
        long dtMemBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long dtStartTime = System.nanoTime();

        DecisionTreeAlgorithm dt = new DecisionTreeAlgorithm(maxDepth);
        dt.trainModel(trainingData);

        int dtCorrect = 0;
        ConfusionMatrix dtMatrix = new ConfusionMatrix();
        for (UserRecord record : testData) {
            String actual = record.getCategory();
            String predicted = dt.predictCategory(record);
            dtMatrix.addPrediction(actual, predicted);
            if (actual != null && actual.equalsIgnoreCase(predicted)) {
                dtCorrect++;
            }
        }
        long dtEndTime = System.nanoTime();
        long dtMemAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double dtMemoryUsedMB = Math.max(0, (dtMemAfter - dtMemBefore) / (1024.0 * 1024.0));

        results.add(calculateResult("Decision Tree Algorithm", dtStartTime, dtEndTime, dtCorrect, testData.size(), dtMemoryUsedMB, dtMatrix, logArea));

        return results;
    }

    private EvaluationResult calculateResult(String name, long startTime, long endTime, int correctPredictions, int totalData, double memoryUsedMB, ConfusionMatrix matrix, TextArea logArea) {
        double accuracy = totalData == 0 ? 0 : (double) correctPredictions / (double) totalData * 100.0;
        double durationMs = (endTime - startTime) / 1_000_000.0;

        logArea.appendText("\n=== " + name + " ===\n");
        logArea.appendText(String.format("Accuracy: %.2f%%\n", accuracy));
        logArea.appendText(String.format("Duration: %.2f ms\n", durationMs));
        logArea.appendText(String.format("Memory Used: %.4f MB\n", memoryUsedMB));
        logArea.appendText("\nConfusion Matrix:\n" + matrix.toString());

        return new EvaluationResult(name, accuracy, durationMs, memoryUsedMB, matrix);
    }
}
