package org.proje2.prolab2proje2.algorithms;

import java.util.List;
import org.proje2.prolab2proje2.data.UserRecord;

public abstract class BaseAlgorithm implements IClassifier {
    protected List<UserRecord> dataSet;

    @Override
    public void trainModel(List<UserRecord> trainingDataset) {
        this.dataSet = trainingDataset;
    }


    public List<UserRecord> getDataSet() {
        return dataSet;
    }

    public double calculateAccuracy(List<UserRecord> testData) {
        if (testData == null || testData.isEmpty()) return 0.0;
        int correct = 0;
        for (UserRecord record : testData) {
            if (record.getCategory().equalsIgnoreCase(predictCategory(record))) {
                correct++;
            }
        }
        return (double) correct / testData.size() * 100.0;
    }
}
