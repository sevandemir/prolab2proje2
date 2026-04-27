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

            logArea.clear(); //Clear log area for new test 
            logArea.appendText("-> Status: Analysis started...\n");

            // 1. Veri Yükleme (Modüler katmandan çağırıyoruz)
            DataLoader loader = new DataLoader();
            List<UserRecord> fullData = loader.loadDataFromExcel("MarketSalesKocaeli.xlsx");
            logArea.appendText("-> Status: " + fullData.size() + " records loaded successfully.\n");

            // 2. K değerini arayüzdeki kutucuktan al
            int K = Integer.parseInt(kValueField.getText());
            
            // 3. Hesaplayıcıyı tetikle ve sonuçları TextArea'ya yazdır
            Evaluator evaluator = new Evaluator();
            
            // Evaluator içindeki metodu artık logArea'yı da göndererek çağırıyoruz
            evaluator.evaluatePerformance(K,fullData, logArea);

        }
        catch(NumberFormatException error){
            logArea.appendText("Input Error: Please enter a valid number for K value!\n");
        }
        catch (Exception error) {
            logArea.appendText("System Error: " + error.getMessage() + "\n");
            error.printStackTrace(); // Hatanın detayını konsolda da gör
        }
        finally{
            logArea.appendText("-> Status: Analysis completed.\n");
        }
    }
}