package org.proje2.prolab2proje2.eval;

public record EvaluationResult(String algorithmName,
                               double accuracy,
                               double durationMs,
                               double memoryUsedMB,
                               ConfusionMatrix confusionMatrix) {
}
