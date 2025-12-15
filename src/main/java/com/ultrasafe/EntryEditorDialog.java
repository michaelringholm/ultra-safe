
package com.ultrasafe;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Optional;

public class EntryEditorDialog {
    public static Optional<EntryItem> show(Stage owner, EntryItem original) {
        Stage dlg = new Stage();
        dlg.initOwner(owner);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("Edit Entry");

        TextField tfUser = new TextField(original.getUsername());
        HBox pwBox = buildPasswordBox(original);
        TextField tfCtx = new TextField(original.getContext());
        FileControls fileControls = buildFileControls(owner, original);

        Button btnOk = new Button("OK");
        Button btnCancel = new Button("Cancel");
        btnOk.setDefaultButton(true);
        btnCancel.setCancelButton(true);

        btnOk.setOnAction(e -> dlg.close());
        btnCancel.setOnAction(e -> { dlg.setUserData(null); dlg.close(); });

        GridPane gp = new GridPane();
        gp.setPadding(new Insets(12));
        gp.setHgap(8);
        gp.setVgap(8);
        gp.addRow(0, new Label("👤 Username:"), tfUser);
        gp.addRow(1, new Label("🔑 Password:"), pwBox);
        gp.addRow(2, new Label("📝 Context:"), tfCtx);
        gp.addRow(3, new Label("📎 File:"), fileControls.box);
        gp.addRow(4, btnOk, btnCancel);

        dlg.setScene(new Scene(gp));
        dlg.showAndWait();

        if (dlg.getUserData() != null) return Optional.empty();
        String pw = PasswordBoxHelper.getPassword(pwBox);
        EntryItem e = new EntryItem(original.getId(), tfUser.getText(), pw, tfCtx.getText(), fileControls.bytes[0]);
        e.setFileName(fileControls.fileName.getText().isEmpty() ? null : fileControls.fileName.getText());
        return Optional.of(e);
    }

    private static HBox buildPasswordBox(EntryItem original) {
        PasswordField pf = new PasswordField();
        pf.setText(original.getPassword());
        TextField tfVisible = new TextField(original.getPassword());
        tfVisible.setManaged(false);
        tfVisible.setVisible(false);

        Button eye = new Button("👁");
        eye.setOnAction(ev -> {
            boolean showing = tfVisible.isVisible();
            if (showing) {
                pf.setText(tfVisible.getText());
                tfVisible.setVisible(false);
                tfVisible.setManaged(false);
                pf.setVisible(true);
                pf.setManaged(true);
            } else {
                tfVisible.setText(pf.getText());
                tfVisible.setVisible(true);
                tfVisible.setManaged(true);
                pf.setVisible(false);
                pf.setManaged(false);
            }
        });

        Button suggest = new Button("🎲 Suggest");
        suggest.setOnAction(ev -> {
            String generated = generatePassword();
            pf.setText(generated);
            tfVisible.setText(generated);
        });
        return new HBox(5, pf, tfVisible, eye, suggest);
    }

    private static String generatePassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            int idx = random.nextInt(chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    private static class PasswordBoxHelper {
        static String getPassword(HBox pwBox) {
            for (javafx.scene.Node n : pwBox.getChildren()) {
                if (n instanceof PasswordField && n.isVisible()) return ((PasswordField)n).getText();
                if (n instanceof TextField && n.isVisible()) return ((TextField)n).getText();
            }
            return "";
        }
    }

    private static class FileControls {
        final HBox box;
        final TextField fileName;
        final byte[][] bytes;

        FileControls(HBox box, TextField fileName, byte[][] bytes) {
            this.box = box; this.fileName = fileName; this.bytes = bytes;
        }
    }

    private static FileControls buildFileControls(Stage owner, EntryItem original) {
        TextField tfFile = new TextField(original.getFileName() == null ? "" : original.getFileName());
        tfFile.setEditable(false);
        final byte[][] fileRef = new byte[1][];
        fileRef[0] = original.getFileBytes();

        Button attach = new Button("📎 Attach...");
        attach.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            File f = fc.showOpenDialog(owner);
            if (f != null) {
                try {
                    fileRef[0] = Files.readAllBytes(f.toPath());
                    tfFile.setText(f.getName());
                } catch (Exception ex) {
                    Alerts.error("Attach failed", ex.getMessage());
                }
            }
        });

        Button clear = new Button("🗑️ Remove");
        clear.setOnAction(e -> { fileRef[0] = null; tfFile.setText(""); });

        HBox box = new HBox(5, tfFile, attach, clear);
        return new FileControls(box, tfFile, fileRef);
    }


}
