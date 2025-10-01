
package com.ultrasafe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SafeStore {
    static class PlainDoc {
        int version = 1;
        List<EntryItem> entries = new ArrayList<>();
    }

    private final List<EntryItem> entries;

    public SafeStore(List<EntryItem> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public List<EntryItem> getEntries() { return entries; }

    public void save(File file, char[] password) throws Exception {
        Gson gson = new GsonBuilder().create();
        PlainDoc doc = new PlainDoc();
        doc.entries.addAll(entries);
        byte[] json = gson.toJson(doc).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] blob = CryptoUtils.encrypt(password, json);
        Files.write(file.toPath(), blob);
    }

    public static SafeStore load(File file, char[] password) throws Exception {
        byte[] blob = Files.readAllBytes(file.toPath());
        byte[] json = CryptoUtils.decrypt(password, blob);
        String js = new String(json, java.nio.charset.StandardCharsets.UTF_8);
        Gson gson = new GsonBuilder().create();
        PlainDoc doc = gson.fromJson(js, PlainDoc.class);
        return new SafeStore(doc.entries);
    }
}
