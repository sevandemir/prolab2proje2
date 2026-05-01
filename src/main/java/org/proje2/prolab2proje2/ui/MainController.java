package org.proje2.prolab2proje2.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    @FXML private TextArea predictionLogArea;
    @FXML private TextField kValueField;
    @FXML private TextField maxDepthField;

    @FXML private RadioButton radioSingleFile;
    @FXML private RadioButton radioSeparateFiles;
    @FXML private VBox singleFileBox;
    @FXML private VBox separateFilesBox;

    @FXML private TextField filePathField;
    @FXML private TextField trainPathField;
    @FXML private TextField testPathField;

    @FXML private Label statusLabel;
    @FXML private Label avgAccuracyLabel;
    @FXML private BarChart<String, Number> accuracyChart;
    @FXML private BarChart<String, Number> durationChart;
    @FXML private BarChart<String, Number> memoryChart;

    // Single Prediction Fields
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField spendingField;
    @FXML private Label knnResultLabel;
    @FXML private Label dtResultLabel;

    private List<UserRecord> loadedData;
    private File selectedFile = new File("MarketSalesKocaeli.xlsx");
    private File selectedTrainFile;
    private File selectedTestFile;

    public void initialize() {
        if (radioSingleFile != null) {
            radioSingleFile.selectedProperty().addListener((obs, oldV, newV) -> {
                if (newV) {
                    singleFileBox.setVisible(true);
                    singleFileBox.setManaged(true);
                    separateFilesBox.setVisible(false);
                    separateFilesBox.setManaged(false);
                }
            });
            radioSeparateFiles.selectedProperty().addListener((obs, oldV, newV) -> {
                if (newV) {
                    singleFileBox.setVisible(false);
                    singleFileBox.setManaged(false);
                    separateFilesBox.setVisible(true);
                    separateFilesBox.setManaged(true);
                }
            });
        }
    }

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
    private void handleSelectTrainFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Training Dataset Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(logArea.getScene().getWindow());
        if (file != null) {
            selectedTrainFile = file;
            trainPathField.setText(file.getName());
            logArea.appendText("-> Selected training file: " + file.getAbsolutePath() + "\n");
        }
    }

    @FXML
    private void handleSelectTestFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Testing Dataset Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(logArea.getScene().getWindow());
        if (file != null) {
            selectedTestFile = file;
            testPathField.setText(file.getName());
            logArea.appendText("-> Selected testing file: " + file.getAbsolutePath() + "\n");
        }
    }

    @FXML
    private void handleStartTest() {
        String kText = kValueField.getText();
        String depthText = maxDepthField.getText();
        
        statusLabel.setText("Running Analysis...");
        logArea.clear();
        logArea.appendText("-> Preparing background task...\n");

        Task<List<EvaluationResult>> analysisTask = new Task<>() {
            @Override
            protected List<EvaluationResult> call() throws Exception {
                DataLoader loader = new DataLoader();
                Evaluator evaluator = new Evaluator();
                int K = Integer.parseInt(kText);
                int depth = Integer.parseInt(depthText);

                if (radioSeparateFiles != null && radioSeparateFiles.isSelected()) {
                    if (selectedTrainFile == null || selectedTestFile == null) {
                        throw new IllegalArgumentException("Please pick both training and testing files.");
                    }
                    updateMessage("Loading separate datasets...");
                    List<UserRecord> trainingData = loader.loadDataFromExcel(selectedTrainFile.getAbsolutePath());
                    List<UserRecord> testData = loader.loadDataFromExcel(selectedTestFile.getAbsolutePath());
                    loadedData = trainingData; // fallback for single prediction
                    Platform.runLater(() -> logArea.appendText("-> Loaded training data: " + trainingData.size() + " records.\n"));
                    Platform.runLater(() -> logArea.appendText("-> Loaded testing data: " + testData.size() + " records.\n"));

                    updateMessage("Running algorithms...");
                    return evaluator.evaluatePerformanceSeparate(K, depth, trainingData, testData, logArea);
                } else {
                    updateMessage("Loading single dataset...");
                    loadedData = loader.loadDataFromExcel(selectedFile.getAbsolutePath());
                    Platform.runLater(() -> logArea.appendText("-> Loaded single dataset: " + loadedData.size() + " records.\n"));

                    updateMessage("Running algorithms...");
                    return evaluator.evaluatePerformance(K, depth, loadedData, logArea);
                }
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

            // Clear and log the tracing output directly into the individual tab's TextArea
            if (predictionLogArea != null) {
                predictionLogArea.clear();
                predictionLogArea.appendText("=========================================\n");
                predictionLogArea.appendText("KİŞİ TAHMİNİ DETAYLI ANALİZİ\n");
                predictionLogArea.appendText("=========================================\n");
                predictionLogArea.appendText("Girdi Değerleri: Cinsiyet = " + genderCode + ", Harcama Tutarı = " + String.format("%.2f", spending) + " TL\n\n");

                predictionLogArea.appendText("--- [KNN Algoritması İncelemesi (K = " + K + ")] ---\n");
                predictionLogArea.appendText("Bulunan En Yakın " + K + " Komşu Satırı:\n");
                List<UserRecord> neighbours = knn.getLastNeighbours();
                
                int encGender = org.proje2.prolab2proje2.data.PreProcessor.encodeGenderFromUserInput(genderCode);
                double normLine = org.proje2.prolab2proje2.data.PreProcessor.normalizeInputFromUserInput(spending);
                UserRecord inputRec = new UserRecord(0, genderCode, spending, null);
                inputRec.setEncodedGender(encGender);
                inputRec.setNormalizedLineTotal(normLine);

                if (neighbours != null) {
                    for (int i = 0; i < neighbours.size(); i++) {
                        UserRecord n = neighbours.get(i);
                        double dist = knn.getEuclidDistance(inputRec, n);
                        predictionLogArea.appendText(String.format("%d) Client Code: %d, Gender: %s, LineNetTotal: %.2f, Category: %s | Mesafe: %.4f\n", 
                            (i + 1), n.getClientCode(), n.getGender(), n.getLineNetTotal(), n.getCategory(), dist));
                    }
                }
                predictionLogArea.appendText("En Sık Geçen Kategori: " + knnRes + "\n\n");

                predictionLogArea.appendText("--- [Karar Ağacı Algoritması İncelemesi (Max Depth = " + depth + ")] ---\n");
                predictionLogArea.appendText("Karar Yolu:\n");
                List<String> path = dt.getLastDecisionPath();
                if (path != null) {
                    for (int i = 0; i < path.size(); i++) {
                        predictionLogArea.appendText(String.format("%d) %s\n", (i + 1), path.get(i)));
                    }
                }
                predictionLogArea.appendText("=========================================\n");
            }

        } catch (Exception e) {
            if (predictionLogArea != null) {
                predictionLogArea.appendText("Prediction Error: " + e.getMessage() + "\n");
            }
        }
    }

    @FXML
    private void handleClearResults() {
        logArea.clear();
        if (predictionLogArea != null) {
            predictionLogArea.clear();
        }
        accuracyChart.getData().clear();
        durationChart.getData().clear();
        memoryChart.getData().clear();
        avgAccuracyLabel.setText("0.0%");
        knnResultLabel.setText("N/A");
        dtResultLabel.setText("N/A");
        statusLabel.setText("Ready");
    }

    private void updateCharts(List<EvaluationResult> results) {
        accuracyChart.getData().clear();
        durationChart.getData().clear();
        memoryChart.getData().clear();

        XYChart.Series<String, Number> accuracySeries = new XYChart.Series<>();
        accuracySeries.setName("Accuracy (%)");

        XYChart.Series<String, Number> durationSeries = new XYChart.Series<>();
        durationSeries.setName("Duration (ms)");

        XYChart.Series<String, Number> memorySeries = new XYChart.Series<>();
        memorySeries.setName("Memory (MB)");

        for (EvaluationResult result : results) {
            accuracySeries.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getAccuracy()));
            durationSeries.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getDurationMs()));
            memorySeries.getData().add(new XYChart.Data<>(result.getAlgorithmName(), result.getMemoryUsedMB()));
        }

        accuracyChart.getData().add(accuracySeries);
        durationChart.getData().add(durationSeries);
        memoryChart.getData().add(memorySeries);
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