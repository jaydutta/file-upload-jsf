package com.server.client.model;

import java.io.Serializable;

public class FileItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String type;
    private long size;
    private String path;
    
    public FileItem() {}
    
    public FileItem(String name, String type, long size, String path) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.path = path;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
}