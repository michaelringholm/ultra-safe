package com.ultrasafe;

import javafx.stage.Stage;
import java.io.File;

public interface SafeService {
    void newSafe(Stage stage);
    void openSafe(Stage stage);
    void openSafe(Stage stage, File f);
    void saveSafe(boolean saveAs, Stage stage);
    void lockSafe();

    void addEntry(Stage stage);
    void editEntry(Stage stage, EntryItem item);
    void deleteEntry(EntryItem item);
    void exportEntry(Stage stage, EntryItem item);

    void updateStatus();
}
