package org.proje2.prolab2proje2.algorithms;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.proje2.prolab2proje2.data.UserRecord;

/**
 * BaseAlgorithm represents the common parent for machine learning models.
 */
public abstract class BaseAlgorithm implements IClassifier {
    protected List<UserRecord> dataSet;

    @Override
    public void trainModel(List<UserRecord> trainingDataset) {
        this.dataSet = trainingDataset;
    }

    /**
     * Finds the most frequent category within a list of user records.
     * Shared by both KNN and Decision Tree algorithms.
     * @param dataset Records to analyze.
     * @return Most common category name.
     */
    protected String predictCategoryByFrequency(List<UserRecord> dataset) {
        if (dataset == null || dataset.isEmpty()) return "Unknown";
        return dataset.stream()
                .collect(Collectors.groupingBy(UserRecord::getCategory, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }
}
