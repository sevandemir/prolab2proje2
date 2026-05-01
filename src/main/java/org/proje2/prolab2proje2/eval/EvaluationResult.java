package org.proje2.prolab2proje2.eval;

public class EvaluationResult {
    private final String algorithmName;
    private final double accuracy;
    private final double durationMs;

    public EvaluationResult(String algorithmName, double accuracy, double durationMs) {
        this.algorithmName = algorithmName;
        this.accuracy = accuracy;
        this.durationMs = durationMs;
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
}
