module gui {
    requires lib;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires jdk.compiler;
    exports pl.edu.pwr.lczerwinski.queues_nfz.gui;
    opens pl.edu.pwr.lczerwinski.queues_nfz.gui to javafx.fxml;
}