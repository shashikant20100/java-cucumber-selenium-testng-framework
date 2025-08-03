package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.LogUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelDataProvider {

    /**
     * Reads test data from Excel file and returns as Object[][]
     * @param filePath Path to Excel file
     * @param sheetName Name of the sheet to read
     * @return Object[][] containing test data
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        try {
            LogUtil.info("Reading test data from Excel: " + filePath + ", Sheet: " + sheetName);

            FileInputStream file = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file");
            }

            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();

            Object[][] data = new Object[rowCount][colCount];

            // Start from row 1 (skip header row)
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    data[i-1][j] = getCellValue(cell);
                }
            }

            workbook.close();
            file.close();

            LogUtil.info("Successfully read " + rowCount + " rows of test data");
            return data;

        } catch (Exception e) {
            LogUtil.error("Failed to read Excel data: " + e.getMessage());
            throw new RuntimeException("Excel data reading failed", e);
        }
    }

    /**
     * Reads test data from Excel and returns as List of Maps
     * @param filePath Path to Excel file
     * @param sheetName Name of the sheet to read
     * @return List<Map<String, String>> containing test data with column headers as keys
     */
    public static List<Map<String, String>> getTestDataAsMap(String filePath, String sheetName) {
        try {
            LogUtil.info("Reading test data as Map from Excel: " + filePath + ", Sheet: " + sheetName);

            FileInputStream file = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file");
            }

            List<Map<String, String>> testData = new ArrayList<>();

            // Get header row
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValue(cell).toString());
            }

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        String value = getCellValue(cell).toString();
                        rowData.put(headers.get(j), value);
                    }
                    testData.add(rowData);
                }
            }

            workbook.close();
            file.close();

            LogUtil.info("Successfully read " + testData.size() + " rows of test data as Map");
            return testData;

        } catch (Exception e) {
            LogUtil.error("Failed to read Excel data as Map: " + e.getMessage());
            throw new RuntimeException("Excel data reading failed", e);
        }
    }

    /**
     * Gets cell value based on cell type
     * @param cell Excel cell
     * @return Object containing cell value
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return (int) cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * Creates a sample Excel file with smartwatch brand test data
     * @param filePath Path where to create the Excel file
     */
    public static void createSampleTestDataFile(String filePath) {
        try {
            LogUtil.info("Creating sample test data Excel file: " + filePath);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("SmartWatchBrands");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("brand");
            headerRow.createCell(1).setCellValue("minPrice");
            headerRow.createCell(2).setCellValue("maxPrice");
            headerRow.createCell(3).setCellValue("description");

            // Create data rows
            String[][] testData = {
                {"Noise", "1000", "5000", "Popular Indian smartwatch brand"},
                {"boAt", "1000", "5000", "Audio and wearables brand"},
                {"Fire-Boltt", "1000", "5000", "Affordable smartwatch brand"},
                {"Amazfit", "1000", "5000", "Premium fitness-focused brand"},
                {"Realme", "1000", "5000", "Smartphone brand's smartwatch line"}
            };

            for (int i = 0; i < testData.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < testData[i].length; j++) {
                    if (j == 1 || j == 2) { // minPrice and maxPrice columns
                        row.createCell(j).setCellValue(Integer.parseInt(testData[i][j]));
                    } else {
                        row.createCell(j).setCellValue(testData[i][j]);
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            // Save the file
            java.io.FileOutputStream fileOut = new java.io.FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            LogUtil.info("Sample test data Excel file created successfully");

        } catch (Exception e) {
            LogUtil.error("Failed to create sample Excel file: " + e.getMessage());
            throw new RuntimeException("Excel file creation failed", e);
        }
    }
}
