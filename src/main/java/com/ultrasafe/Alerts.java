
package com.ultrasafe;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Alerts {
    public static void error(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(title);
        a.showAndWait();
    }
    public static void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(title);
        a.showAndWait();
    }
    public static boolean confirm(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setTitle(title);
        a.setHeaderText(title);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
