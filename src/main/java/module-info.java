module org.proje2.prolab2proje2{

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires transitive javafx.graphics;

    opens org.proje2.prolab2proje2.ui to javafx.fxml;
    
    exports org.proje2.prolab2proje2.ui;
}