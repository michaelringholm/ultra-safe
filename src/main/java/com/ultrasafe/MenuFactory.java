package com.ultrasafe;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuFactory {

    public static VBox build(SafeService service,
                             Stage stage,
                             RecentManager recentManager,
                             TableView<EntryItem> table) {

        var fileMenu = buildFileMenu(service, stage, recentManager);
        var editMenu = buildEditMenu(service, stage, table);
        var exportMenu = buildExportMenu(service, stage, table);

        var menuBar = new MenuBar(fileMenu, editMenu, exportMenu);
        var quickBar = ToolbarFactory.build(service, stage);

        var vbox = new VBox(menuBar, quickBar);
        recentManager.load(f -> service.openSafe(stage, f));
        return vbox;
    }

    private static Menu buildFileMenu(SafeService service, Stage stage, RecentManager recentManager) {
        var mNew = new MenuItem("🆕 New");
        mNew.setOnAction(e -> service.newSafe(stage));

        var mOpen = new MenuItem("📂 Open...");
        mOpen.setOnAction(e -> service.openSafe(stage));

        var mSave = new MenuItem("💾 Save");
        mSave.setOnAction(e -> service.saveSafe(false, stage));

        var mSaveAs = new MenuItem("📑 Save As...");
        mSaveAs.setOnAction(e -> service.saveSafe(true, stage));

        var mLock = new MenuItem("🔒 Lock");
        mLock.setOnAction(e -> service.lockSafe());

        var menu = new Menu("File");
        menu.getItems().addAll(mNew, mOpen, recentManager.getMenu(), mSave, mSaveAs, mLock);
        return menu;
    }

    private static Menu buildEditMenu(SafeService service, Stage stage, TableView<EntryItem> table) {
        var mAdd = new MenuItem("➕ Add Entry");
        mAdd.setOnAction(e -> service.addEntry(stage));

        var mEdit = new MenuItem("📝 Edit Entry");
        mEdit.setOnAction(e -> service.editEntry(stage, table.getSelectionModel().getSelectedItem()));

        var mDel = new MenuItem("❌ Delete Entry");
        mDel.setOnAction(e -> service.deleteEntry(table.getSelectionModel().getSelectedItem()));

        var menu = new Menu("Edit");
        menu.getItems().addAll(mAdd, mEdit, mDel);
        return menu;
    }

    private static Menu buildExportMenu(SafeService service, Stage stage, TableView<EntryItem> table) {
        var mExport = new MenuItem("📄 Export File");
        mExport.setOnAction(e -> service.exportEntry(stage, table.getSelectionModel().getSelectedItem()));

        var menu = new Menu("Export");
        menu.getItems().add(mExport);
        return menu;
    }
}
