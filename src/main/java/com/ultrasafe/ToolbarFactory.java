package com.ultrasafe;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;

public class ToolbarFactory {
    public static ToolBar build(SafeService service, Stage stage) {
        Button bAdd = new Button("➕ Add Entry");
        bAdd.setOnAction(e -> service.addEntry(stage));
        return new ToolBar(bAdd);
    }


    @Deprecated
    private ToolBar setupToolbar(Stage stage) {
        /*Button bNew = btn("🆕 New", () -> newSafe(stage));
        Button bOpen = btn("📂 Open", () -> openSafe(stage));
        Button bSave = btn("💾 Save", () -> saveSafe(false, stage));
        Button bSaveAs = btn("📑 Save As", () -> saveSafe(true, stage));
        Button bLock = btn("🔒 Lock", this::lockSafe);
        Button bAdd = btn("➕ Add", () -> addEntry(stage));
        Button bEdit = btn("📝 Edit", () -> editSelected(stage));
        Button bDel = btn("❌ Delete", this::deleteSelected);
        Button bExport = btn("📄 Export", () -> exportSelected(stage));

        ToolBar tb = new ToolBar(
                bNew, bOpen, bSave, bSaveAs, bLock,
                new Separator(), bAdd, bEdit, bDel,
                new Separator(), bExport
        );*/
        ToolBar tb = new ToolBar();
        return tb;
    }
}
