package org.proje2.prolab2proje2.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. FXML dosyasının yolunu belirtiyoruz. 
        // Resources altındaki yapı ile birebir aynı olmalı.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/proje2/prolab2proje2/ui/design.fxml"));
        
        // 2. Tasarımı yüklüyoruz
        Parent root = loader.load();
        
        // 3. Bir 'Sahne' oluşturuyoruz (Genişlik: 900, Yükseklik: 600)
        Scene scene = new Scene(root, 900, 600);
        
        // 4. CSS dosyasını bağlıyoruz (Daha sonra güzelleştirmek için)
        String css = getClass().getResource("/org/proje2/prolab2proje2/ui/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        // 5. Pencere ayarları
        primaryStage.setTitle("Prolab 2 - Algoritma Performans Analizi");
        primaryStage.setScene(scene);
        primaryStage.show(); // Pencereyi görünür yap
    }

    public static void main(String[] args) {
        // JavaFX uygulamasını başlatan komut
        launch(args);
    }
}