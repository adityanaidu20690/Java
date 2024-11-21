package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String hostname = "xxxxxxxx";
        String portNumber = "1545";
        String sid = "xxxxxxxxx";
        String jdbcUrl = "jdbc:oracle:thin:@" + hostname + ":" + portNumber + ":" + sid;
        String username = "xxxxxxxxxxxx";
        String password = "xxxxxxxxxxx";
        int batchSize = 1000;
        String inputFilePath = "C:\\Users\\gilla1s\\Desktop\\input.csv"; // Input file containing values
        String outputPath = "C:\\Users\\gilla1s\\Desktop\\output.csv";   // Output CSV file

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password); FileWriter csvWriter = new FileWriter(outputPath)) {

            // Initialize input file reader
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
                String query = "SELECT MPT_MPAN_PK FROM MDQ_APP_MPAN WHERE MPT_MPAN_PK = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    boolean headersWritten = false;
                    List<String> batch = new ArrayList<>();
                    String line;
                    int totalProcessedRows = 0;

                    while ((line = reader.readLine()) != null) {
                        batch.add(line.trim());
                        if (batch.size() == batchSize) {
                            totalProcessedRows += processBatch(statement, batch, csvWriter, headersWritten);
                            headersWritten = true;
                            batch.clear();
                            System.out.println("Total rows taken so far: " + totalProcessedRows);
                        }
                    }

                    // Process any remaining records in the last batch
                    if (!batch.isEmpty()) {
                        totalProcessedRows += processBatch(statement, batch, csvWriter, headersWritten);
                        System.out.println("Transferred rows so far: " + totalProcessedRows);
                    }
                }
            }

            csvWriter.flush();
            System.out.println("Data exported successfully to " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int processBatch(PreparedStatement statement, List<String> batch, FileWriter csvWriter, boolean headersWritten) throws SQLException, IOException {
        int processedRows = 0;

        for (String value : batch) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!headersWritten && resultSet.isBeforeFirst()) { // Check if ResultSet is not empty
                    writeHeaders(resultSet, csvWriter);
                    headersWritten = true;
                }

                while (resultSet.next()) {
                    writeRow(resultSet, csvWriter);
                    processedRows++;
                }
            }
        }

        if (processedRows > 0) {
            csvWriter.flush();
            System.out.println("Processed " + processedRows + " rows in this batch.");
        } else {
            System.out.println("No matching records found for the current batch.");
        }
        return processedRows;
    }

    private static void writeHeaders(ResultSet resultSet, FileWriter csvWriter) throws SQLException, IOException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            csvWriter.append(metaData.getColumnName(i));
            if (i < columnCount) {
                csvWriter.append(",");
            }
        }
        csvWriter.append("\n");
    }

    private static void writeRow(ResultSet resultSet, FileWriter csvWriter) throws SQLException, IOException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String value = resultSet.getString(i);
            csvWriter.append(value == null ? "" : value.replace(",", " ")); // Replace commas to avoid breaking CSV
            if (i < columnCount) {
                csvWriter.append(",");
            }
        }
        csvWriter.append("\n");
    }
}
