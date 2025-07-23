package com.dashboard.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    public static List<String[]> readCsvFromResource(String resourcePath) {
        List<String[]> records = new ArrayList<>();

        try {
            InputStream inputStream = CsvUtils.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                System.out.println(resourcePath + " not found in resources/");
                return records;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

                records = csvReader.readAll();
            }
        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + resourcePath);
            e.printStackTrace();
        }

        return records;
    }

    public static boolean isBlankOrNullString(String value) {
        return value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null");
    }

    public static boolean isIntegerString(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumericString(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
