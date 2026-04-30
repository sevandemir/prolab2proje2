package org.proje2.prolab2proje2.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.proje2.prolab2proje2.algorithms.DecisionTreeAlgorithm;
import org.proje2.prolab2proje2.algorithms.KNNAlgorithm;
import org.proje2.prolab2proje2.eval.EvaluationResult;
import org.proje2.prolab2proje2.eval.Evaluator;
import org.proje2.prolab2proje2.data.DataLoader;
import org.proje2.prolab2proje2.data.UserRecord;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML private TextArea logArea;
    @FXML private TextField kValueField;
    @FXML private TextField maxDepthField;
    @FXML private TextField filePathField;
    @FXML private Label statusLabel;
    @FXML private Label avgAccuracyLabel;
    @FXML private BarChart<String, Number> accuracyChart;
    @FXML private BarChart<String, Number> durationChart;

    // Single Prediction Fields
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField spendingField;
    @FXML private Label knnResultLabel;
    @FXML private Label dtResultLabel;

    private List<UserRecord> loadedData;
    private File selectedFile = new File("MarketSalesKocaeli.xlsx");

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Dataset Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(logArea.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            filePathField.setText(file.getName());
            logArea.appendText("-> Selected dataset: " + file.getAbsolutePath() + "\n");
        }
    }

    @FXML
    private void handleStartTest() {
        String kText = kValueField.getText();
        String depthText = maxDepthField.getText();
        String filePath = selectedFile.getAbsolutePath();
        
        statusLabel.setText("Running Analysis...");
        logArea.clear();
        logArea.appendText("-> Preparing background task...\n");

        Task<List<EvaluationResult>> analysisTask = new Task<>() {
            @Override
            protected List<EvaluationResult> call() throws Exception {
                updateMessage("Loading dataset...");
                DataLoader loader = new DataLoader();
                loadedData = loader.loadDataFromExcel(filePath);
                
                Platform.runLater(() -> logArea.appendText("-> Dataset loaded: " + loadedData.size() + " records.\n"));

                int K = Integer.parseInt(kText);
                int depth = Integer.parseInt(depthText);
                
                updateMessage("Running algorithms...");
                Evaluator evaluator = new Evaluator();
                return evaluator.evaluatePerformance(K, depth, loadedData, logArea);
            }
        };

        analysisTask.setOnSucceeded(event -> {
            List<EvaluationResult> results = analysisTask.getValue();
            updateCharts(results);
            updateSummary(results);
            statusLabel.setText("Analysis Complete");
            logArea.appendText("-> All tests completed successfully.\n");
        });

        analysisTask.setOnFailed(event -> {
            Throwable e = analysisTask.getException();
            logArea.appendText("Error: " + e.getMessage() + "\n");
            statusLabel.setText("System Error");
        });

        Thread backgroundThread = new Thread(analysisTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    @FXML
    private void handleSinglePrediction() {
        if (loadedData == null || loadedData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please load the dataset first by running a benchmark!");
            alert.showAndWait();
            return;
        }

        try {
            String genderDisplay = genderComboBox.getValue();
            String genderCode = genderDisplay.startsWith("E") ? "E" : "K";
            double spending = Double.parseDouble(spendingField.getText());
            int K = Integer.parseInt(kValueField.getText());
            int depth = Integer.parseInt(maxDepthField.getText());

            // KNN Prediction
            KNNAlgorithm knn = new KNNAlgorithm(K);
            knn.trainModel(loadedData);
            String knnRes = knn.predictCategoryForUserInputWithKNN(genderCode, spending, K);
            knnResultLabel.setText(knnRes);

            // Decision Tree Prediction
            DecisionTreeAlgorithm dt = new DecisionTreeAlgorithm(depth);
            dt.trainModel(loadedData);
            String dtRes = dt.predictCategoryForUserInputWithDecisionTree(genderCode, spending);
            dtResultLabel.setText(dtRes);

            logArea.appendText("-> Single Prediction: Gender=" + genderCode + ", Spending=" + spending + "\n");
            logArea.appendText("   KNN (K=" + K + "): " + knnRes + " | DT (Depth=" + depth + "): " + dtRes + "\n");

        } catch (Exception e) {
            logArea.appendText("Prediction Error: " + e.getMessage() + "\n");
        }
    }

    @FXML
    private void handleClearResults() {
        logArea.clear();
        accuracyChart.getData().clear();
        durationChart.getData().clear();
        avgAccuracyLabel.setText("0.0%");
        knnResultLabel.setText("N/A");
        dtResultLabel.setText("N/A");
        statusLabel.setText("Ready");
    }

    private void updateCharts(List<EvaluationResult> results) {
        accuracyChart.getData().clear();
        durationChart.getData().clear();

        XYChart.Series<String, Number> accuracySeries = new XYChart.Series<>();
        accuracySeries.setName("Accuracy (%)");

        XYChart.Series<String, Number> durationSeries = new XYChart.Series<>();
        durationSeries.setName("Duration (ms)");

        for (EvaluationResult result : results) {
            accuracySeries.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getAccuracy()));
            durationSeries.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getDurationMs()));
        }

        accuracyChart.getData().add(accuracySeries);
        durationChart.getData().add(durationSeries);
    }

    private void updateSummary(List<EvaluationResult> results) {
        if (results.isEmpty()) return;
        
        double totalAccuracy = 0;
        for (EvaluationResult r : results) {
            totalAccuracy += r.getAccuracy();
        }
        double avg = totalAccuracy / results.size();
        avgAccuracyLabel.setText(String.format("%.1f%%", avg));
    }
}