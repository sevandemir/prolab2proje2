package org.proje2.prolab2proje2.eval;

public class EvaluationResult {
    private final String algorithmName;
    private final double accuracy;
    private final double durationMs;
    private final double memoryUsedMB;
    private final ConfusionMatrix confusionMatrix;

    public EvaluationResult(String algorithmName, double accuracy, double durationMs, double memoryUsedMB, ConfusionMatrix confusionMatrix) {
        this.algorithmName = algorithmName;
        this.accuracy = accuracy;
        this.durationMs = durationMs;
        this.memoryUsedMB = memoryUsedMB;
        this.confusionMatrix = confusionMatrix;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getDurationMs() {
        return durationMs;
    }

    public double getMemoryUsedMB() {
        return memoryUsedMB;
    }

    public ConfusionMatrix getConfusionMatrix() {
        return confusionMatrix;
    }
}
