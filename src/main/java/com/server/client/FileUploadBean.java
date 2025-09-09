package com.server.client;




import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import com.server.client.model.FileItem;

@ManagedBean(name = "fileUploadBean")
@ViewScoped
public class FileUploadBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Part uploadedFile;
    private String selectedCategory;
    private List<String> categories;
    private List<FileItem> uploadedFiles;
    private String uploadDirectory;
    
   public FileUploadBean() {
	   init();
   }
    
    public void init() {
        categories = Arrays.asList("Documents", "Images", "Videos", "Others");
        uploadedFiles = new ArrayList<>();
        
        // Set upload directory
        String contextPath = FacesContext.getCurrentInstance()
                .getExternalContext().getRealPath("/");
        uploadDirectory = contextPath + "uploads" + File.separator;
        
        // Create upload directory if it doesn't exist
        createUploadDirectory();
        
        // Load existing files
        loadExistingFiles();
    }
    
    private void createUploadDirectory() {
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    private void loadExistingFiles() {
        File directory = new File(uploadDirectory);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            String contentType = Files.probeContentType(file.toPath());
                            uploadedFiles.add(new FileItem(
                                file.getName(),
                                contentType != null ? contentType : "unknown",
                                file.length(),
                                file.getAbsolutePath()
                            ));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    public void upload() {
        if (uploadedFile == null) {
            addMessage("Please select a file to upload.", FacesMessage.SEVERITY_WARN);
            return;
        }
        
        if (selectedCategory == null || selectedCategory.trim().isEmpty()) {
            addMessage("Please select a category.", FacesMessage.SEVERITY_WARN);
            return;
        }
        
        try {
            String fileName = getFileName(uploadedFile);
            if (fileName == null || fileName.trim().isEmpty()) {
                addMessage("Invalid file name.", FacesMessage.SEVERITY_ERROR);
                return;
            }
            
            // Create category directory
            String categoryDir = uploadDirectory + selectedCategory + File.separator;
            File categoryDirectory = new File(categoryDir);
            if (!categoryDirectory.exists()) {
                categoryDirectory.mkdirs();
            }
            
            // Save file
            String filePath = categoryDir + fileName;
            try (InputStream inputStream = uploadedFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(filePath)) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            // Add to uploaded files list
            FileItem fileItem = new FileItem(
                fileName,
                uploadedFile.getContentType(),
                uploadedFile.getSize(),
                filePath
            );
            uploadedFiles.add(fileItem);
            
            addMessage("File '" + fileName + "' uploaded successfully to category '" + 
                      selectedCategory + "'.", FacesMessage.SEVERITY_INFO);
            
            // Reset form
            uploadedFile = null;
            selectedCategory = null;
            
        } catch (IOException e) {
            addMessage("Error uploading file: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            e.printStackTrace();
        }
    }
    
    public void save() {
        // This method can be used to save metadata to database
        // For now, we'll just show a success message
        addMessage("Files metadata saved successfully!", FacesMessage.SEVERITY_INFO);
    }
    
    public void deleteFile(FileItem fileItem) {
        try {
            File file = new File(fileItem.getPath());
            if (file.exists() && file.delete()) {
                uploadedFiles.remove(fileItem);
                addMessage("File '" + fileItem.getName() + "' deleted successfully.", 
                          FacesMessage.SEVERITY_INFO);
            } else {
                addMessage("Error deleting file '" + fileItem.getName() + "'.", 
                          FacesMessage.SEVERITY_ERROR);
            }
        } catch (Exception e) {
            addMessage("Error deleting file: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String token : contentDisposition.split(";")) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1).trim()
                            .replace("\"", "");
                }
            }
        }
        return null;
    }
    
    private void addMessage(String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, message, null));
    }
    
    // Getters and Setters
    public Part getUploadedFile() {
        return uploadedFile;
    }
    
    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    
    public String getSelectedCategory() {
        return selectedCategory;
    }
    
    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
    
    public List<String> getCategories() {
        return categories;
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    public List<FileItem> getUploadedFiles() {
        return uploadedFiles;
    }
    
    public void setUploadedFiles(List<FileItem> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }
}