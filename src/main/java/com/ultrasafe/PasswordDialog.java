
package com.ultrasafe;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PasswordDialog {
    public static char[] askPassword(Stage owner, String title, String prompt) {
        final char[][] out = new char[1][];
        var dlg = new Stage();
        dlg.initOwner(owner);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle(title);

        var lbl = new Label(prompt);
        var pf = new PasswordField();
        var ok = new Button("Unlock 🔓");
        var cancel = new Button("Cancel");

        ok.setDefaultButton(true);
        cancel.setCancelButton(true);

        ok.setOnAction(ev -> {
            out[0] = pf.getText().toCharArray();
            dlg.close();
        });
        cancel.setOnAction(ev -> {
            out[0] = null;
            dlg.close();
        });

        var gp = buildGridPane(lbl, pf, ok, cancel);
        dlg.setScene(new Scene(gp));
        dlg.showAndWait();
        return out[0];
    }

    private static GridPane buildGridPane(Label lbl, PasswordField pf, Button ok, Button cancel) {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(12));
        gp.setHgap(8);
        gp.setVgap(8);
        gp.add(lbl, 0, 0, 2, 1);
        gp.add(new Label("Master password:"), 0, 1);
        gp.add(pf, 1, 1);
        gp.add(ok, 0, 2);
        gp.add(cancel, 1, 2);
        return gp;
    }
}
