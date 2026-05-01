package org.proje2.prolab2proje2.eval;

import java.util.*;

public class ConfusionMatrix {
    private final Map<String, Map<String, Integer>> matrix = new TreeMap<>();

    public void addPrediction(String actual, String predicted) {
        if (actual == null || actual.isEmpty()) actual = "Bilinmiyor";
        if (predicted == null || predicted.isEmpty()) predicted = "Bilinmiyor";
        
        matrix.putIfAbsent(actual, new TreeMap<>());
        Map<String, Integer> row = matrix.get(actual);
        row.put(predicted, row.getOrDefault(predicted, 0) + 1);
        
        // Ensure that predicted also exists as a key in matrix to keep it symmetric
        matrix.putIfAbsent(predicted, new TreeMap<>());
    }

    public Map<String, Map<String, Integer>> getMatrix() {
        return matrix;
    }

    public Set<String> getCategories() {
        return matrix.keySet();
    }

    public int getCount(String actual, String predicted) {
        if (!matrix.containsKey(actual)) return 0;
        return matrix.get(actual).getOrDefault(predicted, 0);
    }

    @Override
    public String toString() {
        if (matrix.isEmpty()) return "Veri yok.";
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s | %-15s | %s\n", "Gerçek Label", "Tahmin Edilen", "Adet"));
        sb.append("-".repeat(50)).append("\n");
        
        for (String actual : matrix.keySet()) {
            Map<String, Integer> row = matrix.get(actual);
            for (String predicted : row.keySet()) {
                if (!actual.equals(predicted)) {
                    sb.append(String.format("[X] %-13s -> %-15s : %d hata\n", actual, predicted, row.get(predicted)));
                } else {
                    sb.append(String.format("[V] %-13s -> %-15s : %d dogru\n", actual, predicted, row.get(predicted)));
                }
            }
            sb.append("-".repeat(50)).append("\n");
        }
        return sb.toString();
    }
}
