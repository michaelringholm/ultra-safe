package com.ultrasafe;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

public class SafeServiceImpl implements SafeService {
    private final ObservableList<EntryItem> entries;
    private final StatusUpdater statusUpdater;
    private final RecentManager recentManager;
    private File currentFile;
    private char[] masterPassword;

    public SafeServiceImpl(ObservableList<EntryItem> entries,
                           StatusUpdater statusUpdater,
                           RecentManager recentManager) {
        this.entries = entries;
        this.statusUpdater = statusUpdater;
        this.recentManager = recentManager;
    }

    @Override
    public void newSafe(Stage stage) {
        entries.clear();
        currentFile = null;
        masterPassword = PasswordDialog.askPassword(stage, "Create Safe", "Set a master password");
        updateStatus();
    }

    @Override
    public void openSafe(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ultra Safe (*.safe)", "*.safe"));
        File f = fc.showOpenDialog(stage);
        if (f != null) openSafe(stage, f);
    }

    @Override
    public void openSafe(Stage stage, File f) {
        char[] pw = PasswordDialog.askPassword(stage, "Unlock", "Enter password for:\n" + f.getName());
        if (pw == null) return;
        try {
            SafeStore store = SafeStore.load(f, pw);
            entries.setAll(store.getEntries());
            currentFile = f;
            masterPassword = pw;
            recentManager.add(f, x -> openSafe(stage, x));
        } catch (Exception ex) {
            Alerts.error("Failed to open", ex.getMessage());
        }
        updateStatus();
    }

    @Override
    public void saveSafe(boolean saveAs, Stage stage) {
        if (masterPassword == null) {
            Alerts.info("Locked", "Unlock or create a safe first.");
            return;
        }
        File target = currentFile;
        if (saveAs || target == null) {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ultra Safe (*.safe)", "*.safe"));
            target = fc.showSaveDialog(stage);
            if (target == null) return;
            if (!target.getName().endsWith(".safe")) {
                target = new File(target.getParent(), target.getName() + ".safe");
            }
        }
        try {
            new SafeStore(entries).save(target, masterPassword);
            currentFile = target;
            recentManager.add(target, x -> openSafe(stage, x));
            Alerts.info("Saved", "Safe saved:\n" + target.getAbsolutePath());
        } catch (Exception ex) {
            Alerts.error("Save failed", ex.getMessage());
        }
        updateStatus();
    }

    @Override
    public void lockSafe() {
        masterPassword = null;
        updateStatus();
    }

    @Override
    public void addEntry(Stage stage) {
        EntryItem empty = new EntryItem(UUID.randomUUID().toString(), "", "", "", null);
        Optional<EntryItem> res = EntryEditorDialog.show(stage, empty);
        res.ifPresent(entries::add);
        updateStatus();
    }

    @Override
    public void editEntry(Stage stage, EntryItem item) {
        if (item == null) {
            Alerts.info("No selection", "Please select an entry to edit.");
            return;
        }
        Optional<EntryItem> res = EntryEditorDialog.show(stage, item);
        res.ifPresent(updated -> {
            int idx = entries.indexOf(item);
            entries.set(idx, updated);
        });
        updateStatus();
    }

    @Override
    public void deleteEntry(EntryItem item) {
        if (item == null) {
            Alerts.info("No selection", "Please select an entry to delete.");
            return;
        }
        boolean ok = Alerts.confirm("Delete", "Delete selected entry? This cannot be undone.");
        if (ok) entries.remove(item);
        updateStatus();
    }

    @Override
    public void exportEntry(Stage stage, EntryItem item) {
        if (item == null) {
            Alerts.info("No selection", "Please select an entry to export.");
            return;
        }
        if (item.getFileBytes() == null) {
            Alerts.info("No file", "This entry has no attached file.");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setInitialFileName(item.getFileName() == null ? "attachment.bin" : item.getFileName());
        File dest = fc.showSaveDialog(stage);
        if (dest == null) return;
        try {
            Files.write(dest.toPath(), item.getFileBytes());
            Alerts.info("Exported", "File written to:\n" + dest.getAbsolutePath());
        } catch (Exception ex) {
            Alerts.error("Export failed", ex.getMessage());
        }
    }

    @Override
    public void updateStatus() {
        statusUpdater.update(masterPassword != null, currentFile, entries.size());
    }
}
