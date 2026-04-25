package org.proje2.prolab2proje2.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.proje2.prolab2proje2.eval.Evaluator;
import org.proje2.prolab2proje2.data.DataLoader;
import org.proje2.prolab2proje2.data.UserRecord;
import java.util.List;

public class MainController {

    // FXML'deki fx:id="logArea" ile eşleşir
    @FXML
    private TextArea logArea;

    // FXML'deki fx:id="kValueField" ile eşleşir
    @FXML
    private TextField kValueField;

    @FXML
    private void handleStartTest(){
        try {
            logArea.clear(); // Yeni test için ekranı temizle
            logArea.appendText(">>> Status: Analysis Started...\n");

            // 1. Veri Yükleme (Modüler katmandan çağırıyoruz)
            DataLoader loader = new DataLoader();
            List<UserRecord> fullData = loader.loadDataFromExcel("MarketSalesKocaeli.xlsx");
            logArea.appendText(">>> Status: " + fullData.size() + " records loaded successfully.\n");

            // 2. K değerini arayüzdeki kutucuktan al
            int K = Integer.parseInt(kValueField.getText());
            
            // 3. Hesaplayıcıyı tetikle ve sonuçları TextArea'ya yazdır
            Evaluator evaluator = new Evaluator(K);
            
            // Evaluator içindeki metodu artık logArea'yı da göndererek çağırıyoruz
            evaluator.evaluatePerformance(fullData, logArea);

            logArea.appendText(">>> Status: Analysis Completed Successfully.\n");

        } catch (NumberFormatException e) {
            logArea.appendText("ERROR: Please enter a valid number for K value!\n");
        } catch (Exception e) {
            logArea.appendText("SYSTEM ERROR: " + e.getMessage() + "\n");
            e.printStackTrace(); // Hatanın detayını konsolda da gör
        }
    }
}