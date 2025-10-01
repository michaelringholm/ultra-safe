
package com.ultrasafe;

import com.google.gson.annotations.SerializedName;

import java.util.Base64;
import java.util.Objects;

public class EntryItem {
    @SerializedName("id")
    private String id;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("context")
    private String context;
    @SerializedName("fileName")
    private String fileName;
    @SerializedName("fileBytesB64")
    private String fileBytesB64;

    public EntryItem(String id, String username, String password, String context, byte[] fileBytes) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.context = context;
        setFileBytes(fileBytes);
    }

    public boolean matches(String filter) {
        if (filter == null || filter.isEmpty()) return true;
        String f = filter.toLowerCase();
        return (nonNull(username).toLowerCase().contains(f) ||
                nonNull(context).toLowerCase().contains(f) ||
                nonNull(fileName).toLowerCase().contains(f));
    }

    private String nonNull(String s) { return s == null ? "" : s; }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getContext() { return context; }
    public String getFileName() { return fileName; }
    public byte[] getFileBytes() { return fileBytesB64 == null ? null : Base64.getDecoder().decode(fileBytesB64); }

    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setContext(String context) { this.context = context; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileBytes(byte[] bytes) { this.fileBytesB64 = bytes == null ? null : Base64.getEncoder().encodeToString(bytes); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryItem)) return false;
        EntryItem that = (EntryItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
