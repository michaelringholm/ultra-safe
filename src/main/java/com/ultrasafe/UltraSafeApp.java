package com.ultrasafe;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

public class UltraSafeApp extends Application {

    private final TableView<EntryItem> table = new TableView<>();
    private final ObservableList<EntryItem> entries = FXCollections.observableArrayList();
    private final Label status = new Label("🔒 Locked");
    private File currentFile = null;
    private char[] masterPassword = null;
    private Menu recentMenu; // field at class top
    private File lastDir = null;
    private Stage primaryStage;
    private final RecentManager recentManager = new RecentManager();
    public Stage getPrimaryStage() { return primaryStage; }


    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Ultra Safe");

        Label status = new Label("🔒 Locked");

        SafeService service = new SafeServiceImpl(
                entries,
                (unlocked, file, count) -> {
                    String fname = file == null ? "(unsaved)" : file.getName();
                    status.setText((unlocked ? "🔓 Unlocked" : "🔒 Locked")
                            + " • " + fname + " • Entries: " + count);
                },
                recentManager
        );

        // Build table first, so we can pass it to MenuFactory
        TableView<EntryItem> tableView = setupTable(service, stage);

        BorderPane root = new BorderPane();
        root.setCenter(tableView);
        root.setTop(MenuFactory.build(service, stage, recentManager, tableView));
        root.setBottom(new HBox(status));

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    private TableView<EntryItem> setupTable(SafeService service, Stage stage) {
        TableColumn<EntryItem, String> ctxCol = new TableColumn<>("📝 Context");
        ctxCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getContext() == null ? "" : d.getValue().getContext()));
        ctxCol.setPrefWidth(260);

        TableColumn<EntryItem, String> userCol = new TableColumn<>("👤 Username");
        userCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        userCol.setPrefWidth(180);

        TableColumn<EntryItem, String> fileCol = new TableColumn<>("📎 File");
        fileCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFileName() == null ? "" : d.getValue().getFileName()));
        fileCol.setPrefWidth(260);

        table.getColumns().setAll(ctxCol, userCol, fileCol);
        table.setItems(entries);

        table.setRowFactory(tv -> {
            TableRow<EntryItem> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    service.editEntry(stage, row.getItem());
                }
            });
            return row;
        });
        return table;
    }

    private HBox setupFilter() {
        TextField filter = new TextField();
        filter.setPromptText("Filter...");
        filter.textProperty().addListener((obs, o, n) -> {
            table.setItems(entries.filtered(e -> e.matches(n)));
        });
        HBox bottom = new HBox(10, status, new Label("   "), filter);
        bottom.setPadding(new Insets(6));
        return bottom;
    }

    private Button btn(String text, Runnable action) {
        Button b = new Button(text);
        b.setOnAction(e -> action.run());
        return b;
    }

    public void addEntry(Stage stage) {
        EntryItem empty = new EntryItem(UUID.randomUUID().toString(), "", "", "", null);
        Optional<EntryItem> res = EntryEditorDialog.show(stage, empty);
        res.ifPresent(entries::add);
        updateStatus();
    }

    private void editSelected(Stage stage) {
        EntryItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Optional<EntryItem> res = EntryEditorDialog.show(stage, sel);
        res.ifPresent(item -> {
            int idx = entries.indexOf(sel);
            entries.set(idx, item);
        });
    }

    private void deleteSelected() {
        EntryItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        boolean ok = Alerts.confirm("Delete", "Delete selected entry? This cannot be undone.");
        if (ok) entries.remove(sel);
        updateStatus();
    }

    private void exportSelected(Stage stage) {
        EntryItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (sel.getFileBytes() == null) {
            Alerts.info("No file", "This entry has no attached file.");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setInitialFileName(sel.getFileName() == null ? "attachment.bin" : sel.getFileName());
        if (lastDir != null) fc.setInitialDirectory(lastDir);
        File dest = fc.showSaveDialog(stage);
        if (dest == null) return;
        try {
            Files.write(dest.toPath(), sel.getFileBytes());
            Alerts.info("Exported", "File written to:\n" + dest.getAbsolutePath());
        } catch (Exception ex) {
            Alerts.error("Export failed", ex.getMessage());
        }
    }

    public void updateStatus() {
        boolean locked = masterPassword == null;
        String file = currentFile == null ? "(unsaved)" : currentFile.getName();
        status.setText((locked ? "🔒 Locked" : "🔓 Unlocked") + " • Safe: " + file + " • Entries: " + entries.size());
    }

    private static String safeStr(String s) {
        return s == null ? "" : s;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
