package org.proje2.prolab2proje2.eval;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * ConfusionMatrix stores and displays the true vs predicted categorization results.
 */
public class ConfusionMatrix {
    private final Map<String, Map<String, Integer>> matrix = new TreeMap<>();

    /**
     * Records a single instance of actual vs predicted category prediction.
     * @param actual The true category of the record.
     * @param predicted The category predicted by the model.
     */
    public void addPrediction(String actual, String predicted) {
        if (actual == null || actual.isEmpty()) actual = "Bilinmiyor";
        if (predicted == null || predicted.isEmpty()) predicted = "Bilinmiyor";
        
        matrix.putIfAbsent(actual, new TreeMap<>());
        Map<String, Integer> row = matrix.get(actual);
        row.put(predicted, row.getOrDefault(predicted, 0) + 1);
        
        // Ensure that predicted also exists as a key in matrix to keep it symmetric
        matrix.putIfAbsent(predicted, new TreeMap<>());
    }

    /**
     * Converts the confusion matrix data into a printable ASCII table.
     * Uses the exact customized breakdown per category requested by the user.
     * @return String representation of the confusion matrix.
     */
    @Override
    public String toString() {
        if (matrix.isEmpty()) return "No data.";
        
        StringBuilder sb = new StringBuilder();
        Set<String> allCategories = matrix.keySet();
        
        for (String actual : allCategories) {
            Map<String, Integer> row = matrix.get(actual);
            if (row == null || row.isEmpty()) continue;

            // Calculate metrics for this specific actual category
            int categoryTotal = 0;
            int categoryCorrect = 0;
            int categoryErrors = 0;

            for (String predicted : allCategories) {
                int count = row.getOrDefault(predicted, 0);
                categoryTotal += count;
                if (actual.equals(predicted)) {
                    categoryCorrect += count;
                } else {
                    categoryErrors += count;
                }
            }

            // Skip if this category was never seen in the actual test dataset
            if (categoryTotal == 0) continue;

            // Header for this actual category
            sb.append(String.format("=== %s (Total Records: %d) ===\n", actual, categoryTotal));
            sb.append(String.format("Correct Predictions: %d\n", categoryCorrect));
            sb.append(String.format("Error Predictions  : %d\n", categoryErrors));

            // Breakdown of incorrect predictions
            if (categoryErrors > 0) {
                sb.append("-> Incorrect Predictions Breakdown:\n");
                for (String predicted : allCategories) {
                    int count = row.getOrDefault(predicted, 0);
                    if (!actual.equals(predicted) && count > 0) {
                        sb.append(String.format("   * Classified as %-15s: %d time(s)\n", predicted, count));
                    }
                }
            } else {
                sb.append("-> No incorrect predictions made for this category.\n");
            }
            sb.append("-".repeat(50)).append("\n");
        }
        return sb.toString();
    }
}
