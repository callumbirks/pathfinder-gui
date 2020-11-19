module com.callumbirks {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.callumbirks to javafx.fxml;
    exports com.callumbirks;
}