module org.proje2.prolab2proje2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens org.proje2.prolab2proje2 to javafx.fxml;
    exports org.proje2.prolab2proje2;
}