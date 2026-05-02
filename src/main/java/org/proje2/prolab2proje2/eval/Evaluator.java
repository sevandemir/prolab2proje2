package org.proje2.prolab2proje2.eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.proje2.prolab2proje2.algorithms.BaseAlgorithm;
import org.proje2.prolab2proje2.algorithms.DecisionTreeAlgorithm;
import org.proje2.prolab2proje2.algorithms.KNNAlgorithm;
import org.proje2.prolab2proje2.data.UserRecord;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Evaluator assesses the execution and accuracy performance of machine learning algorithms.
 */
public class Evaluator {

    /**
     * Splits a single dataset into 80% train and 20% test data, then runs evaluations.
     * @param K Value of K nearest neighbors for KNN.
     * @param maxDepth Maximum branching depth allowed for Decision Tree.
     * @param fullDataset The total dataset.
     * @param logArea Text area output logging target.
     * @return List containing performance metrics of the algorithms.
     */
    public List<EvaluationResult> evaluatePerformance(int K, int maxDepth, List<UserRecord> fullDataset, TextArea logArea) {
        Collections.shuffle(fullDataset); // Shuffle dataset to ensure randomness

        int splitPoint = (int) (fullDataset.size() * 0.8); // Split dataset: 80% training, 20% testing

        List<UserRecord> trainingData = new ArrayList<>(fullDataset.subList(0, splitPoint));
        List<UserRecord> testData = new ArrayList<>(fullDataset.subList(splitPoint, fullDataset.size()));

        return evaluatePerformanceSeparate(K, maxDepth, trainingData, testData, logArea);
    }

    /**
     * Accepts and evaluates pre-split or distinct training and testing datasets.
     * @param K Value of K nearest neighbors for KNN.
     * @param maxDepth Maximum branching depth allowed for Decision Tree.
     * @param trainingData Dataset records used for training.
     * @param testData Dataset records used for validation.
     * @param logArea Text area output logging target.
     * @return List containing performance metrics of the algorithms.
     */
    public List<EvaluationResult> evaluatePerformanceSeparate(int K, int maxDepth, List<UserRecord> trainingData, List<UserRecord> testData, TextArea logArea) {
        List<EvaluationResult> results = new ArrayList<>();

        // --- Evaluate KNN Algorithm ---
        results.add(evaluateModel("KNN Algorithm", new KNNAlgorithm(K), trainingData, testData, logArea));

        // --- Evaluate Decision Tree Algorithm ---
        results.add(evaluateModel("Decision Tree Algorithm", new DecisionTreeAlgorithm(maxDepth), trainingData, testData, logArea));

        return results;
    }

    /**
     * Shared generic method executing training, tracking execution time/RAM, and logging output.
     * @param name Name of the algorithm.
     * @param model Classifier algorithm model to evaluate.
     * @param trainingData Training records.
     * @param testData Test records.
     * @param logArea Text area output logging target.
     * @return EvaluationResult performance metrics.
     */
    private EvaluationResult evaluateModel(String name, BaseAlgorithm model, List<UserRecord> trainingData, List<UserRecord> testData, TextArea logArea) {
        Runtime.getRuntime().gc(); // Suggest Garbage Collector
        long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long startTime = System.nanoTime();

        model.trainModel(trainingData);

        int correct = 0;
        ConfusionMatrix matrix = new ConfusionMatrix();
        for (UserRecord record : testData) {
            String actual = record.getCategory();
            String predicted = model.predictCategory(record);
            matrix.addPrediction(actual, predicted);
            if (actual != null && actual.equalsIgnoreCase(predicted)) {
                correct++;
            }
        }
        long endTime = System.nanoTime();
        long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double memoryUsedMB = Math.max(0, (memAfter - memBefore) / (1024.0 * 1024.0));

        return calculateResult(name, startTime, endTime, correct, testData.size(), memoryUsedMB, matrix, logArea);
    }

    /**
     * Calculates the statistical accuracy and records test results.
     * @param name Name of the algorithm.
     * @param startTime NanoTime start timestamp.
     * @param endTime NanoTime end timestamp.
     * @param correctPredictions Count of correct predictions.
     * @param totalData Size of the test dataset.
     * @param memoryUsedMB Amount of MB memory utilized.
     * @param matrix Confusion matrix calculated.
     * @param logArea TextArea to emit logs to.
     * @return Calculated EvaluationResult.
     */
    private EvaluationResult calculateResult(String name, long startTime, long endTime, int correctPredictions, int totalData, double memoryUsedMB, ConfusionMatrix matrix, TextArea logArea) {
        double accuracy = totalData == 0 ? 0 : (double) correctPredictions / (double) totalData * 100.0;
        double durationMs = (endTime - startTime) / 1_000_000.0;

        Platform.runLater(() -> {
            logArea.appendText("\n=== " + name + " ===\n");
            logArea.appendText(String.format("Accuracy: %.2f%%\n", accuracy));
            logArea.appendText(String.format("Duration: %.2f ms\n", durationMs));
            logArea.appendText(String.format("Memory Used: %.4f MB\n", memoryUsedMB));
            logArea.appendText("\nConfusion Matrix:\n" + matrix.toString());
        });

        return new EvaluationResult(name, accuracy, durationMs, memoryUsedMB, matrix);
    }
}
