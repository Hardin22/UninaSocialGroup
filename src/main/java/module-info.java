module org.example.uninasocialgroup {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.services.drive;
    requires google.api.client;
    requires com.google.api.client.json.jackson2;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;

    opens it.uninaSocialGroup.Application to javafx.graphics;
    exports it.uninaSocialGroup.Controllers to javafx.fxml;
    opens it.uninaSocialGroup.Controllers to javafx.fxml;
    opens it.uninaSocialGroup.Oggetti to javafx.base;
    requires jdk.httpserver;
    requires org.apache.tika.core;
    requires java.desktop;
}