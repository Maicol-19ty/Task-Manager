module com.infinitehorizons.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.core;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires java.mail;

    opens com.infinitehorizons.taskmanager to javafx.fxml;
    exports com.infinitehorizons.taskmanager;
    exports com.infinitehorizons.taskmanager.Controllers;
    opens com.infinitehorizons.taskmanager.Controllers to javafx.fxml;
}