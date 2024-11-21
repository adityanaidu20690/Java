package com.example;

import java.io.File;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class Main {

    public static void main(String[] args) {
        // Source file to be zipped
        String sourceFilePath = "C:\\Users\\xxxx\\Desktop\\input1.csv"; // Replace with the file's path
        // Destination ZIP file
        String zipFilePath = "C:\\Users\\xxxxx\\Desktop\\output.zip"; // Replace with the desired ZIP path
        // Password for encryption
        String password = "xxxxx@123";

        try {
            // Create a ZipFile instance with the password
            ZipFile zipFile = new ZipFile(zipFilePath, password.toCharArray());

            // Set ZIP parameters
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE); // Default compression
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);   // Normal compression level
            zipParameters.setEncryptFiles(true);                          // Enable encryption
            zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // Use ZIP Standard encryption

            // Add the file to the ZIP archive
            File fileToAdd = new File(sourceFilePath);
            if (fileToAdd.exists()) {
                zipFile.addFile(fileToAdd, zipParameters);
                System.out.println("File successfully zipped with password protection.");
            } else {
                System.out.println("Source file not found: " + sourceFilePath);
            }
        } catch (ZipException e) {
            System.err.println("Error creating password-protected ZIP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
