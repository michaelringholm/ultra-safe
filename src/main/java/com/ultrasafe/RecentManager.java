package com.ultrasafe;

import com.google.gson.Gson;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import java.io.*;
import java.util.*;

public class RecentManager {
    private final File storage = new File(System.getProperty("user.home"), ".ultrasafe-recent.json");
    private final Menu recentMenu = new Menu("Recent");

    public Menu getMenu() { return recentMenu; }

    public void add(File f, java.util.function.Consumer<File> openAction) {
        if (f == null) return;
        // remove duplicates
        recentMenu.getItems().removeIf(mi -> f.equals(mi.getUserData()));

        MenuItem item = new MenuItem(f.getName());
        item.setUserData(f);
        item.setOnAction(e -> openAction.accept(f));
        recentMenu.getItems().add(0, item);

        if (recentMenu.getItems().size() > 5) {
            recentMenu.getItems().remove(5, recentMenu.getItems().size());
        }
        save();
    }

    public void load(java.util.function.Consumer<File> openAction) {
        if (!storage.exists()) return;
        try (Reader r = new FileReader(storage)) {
            String[] paths = new Gson().fromJson(r, String[].class);
            if (paths != null) {
                for (String p : paths) {
                    File f = new File(p);
                    if (f.exists()) add(f, openAction);
                }
            }
        } catch (Exception ignore) {}
    }

    private void save() {
        try (Writer w = new FileWriter(storage)) {
            List<String> paths = new ArrayList<>();
            for (MenuItem mi : recentMenu.getItems()) {
                Object u = mi.getUserData();
                if (u instanceof File) paths.add(((File) u).toString());
            }
            new Gson().toJson(paths, w);
        } catch (Exception ignore) {}
    }
}
