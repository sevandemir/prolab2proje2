package org.proje2.prolab2proje2.data;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * DataLoader handles reading and parsing of customer records from an Excel file.
 */
public class DataLoader {
    
    public static final UserRecord INVALID_USER_RECORD_TEMPLATE = new UserRecord(0, null, 0.0, null);

    /**
     * Reads all rows from the provided Excel file, sanitizes the fields, and preprocesses the records.
     * Overloaded to compute normalization stats by default.
     * @param filePath Path to the Excel dataset file.
     * @return Sanitized list of valid UserRecords.
     */
    public ArrayList<UserRecord> loadDataFromExcel(String filePath) {
        return loadDataFromExcel(filePath, true);
    }

    /**
     * Reads all rows from the Excel file and filters them.
     * Allows skipping statistics calculation for testing datasets to prevent data leakage.
     * @param filePath Path to the Excel dataset file.
     * @param calculateStats If true, updates min, max, average statistics on PreProcessor.
     * @return List of parsed records.
     */
    public ArrayList<UserRecord> loadDataFromExcel(String filePath, boolean calculateStats) {
        ArrayList<UserRecord> recordList = new ArrayList<>();
        File newFile = new File(filePath);

        try (Workbook workbook = WorkbookFactory.create(newFile)) {
            Sheet excelSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = excelSheet.iterator();

            // Skip the header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row excelRow = rowIterator.next();

                // Extract Client Code (Index 9)
                Cell clientCodeCell = excelRow.getCell(9);
                if (clientCodeCell == null || (clientCodeCell.getCellType() != CellType.NUMERIC && clientCodeCell.getCellType() != CellType.STRING)) {
                    continue;
                }
                Integer clientCode = getIntegerCellValue(clientCodeCell);
                if (clientCode == null) continue;

                // Extract Line Net Total (Index 8)
                Cell lineNetTotalCell = excelRow.getCell(8);
                if (lineNetTotalCell == null || (lineNetTotalCell.getCellType() != CellType.NUMERIC && lineNetTotalCell.getCellType() != CellType.STRING)) {
                    continue;
                }
                Double lineNetTotal = getDoubleCellValue(lineNetTotalCell);
                if (lineNetTotal == null) continue;

                // Extract Gender (Index 17)
                Cell genderCell = excelRow.getCell(17);
                if (genderCell == null || (genderCell.getCellType() != CellType.NUMERIC && genderCell.getCellType() != CellType.STRING)) {
                    continue;
                }
                String gender = getStringCellValue(genderCell);
                if (gender == null) continue;

                // Extract Category (Index 12)
                Cell categoryCell = excelRow.getCell(12);
                if (categoryCell == null || (categoryCell.getCellType() != CellType.NUMERIC && categoryCell.getCellType() != CellType.STRING)) {
                    continue;
                }
                String category = getStringCellValue(categoryCell);
                if (category == null) continue;

                // Instantiate valid UserRecord
                UserRecord userRecord = new UserRecord(clientCode, gender, lineNetTotal, category);
                recordList.add(userRecord);
            }
        } catch (IOException error) {
            System.err.println("File error while reading data!");
        } finally {
            System.out.println("Excel file reading completed.");
        }

        // Apply preprocessing pipeline
        PreProcessor preProcessor = new PreProcessor();
        preProcessor.dataCleaner(recordList); 
        if (calculateStats) {
            preProcessor.calculateNormalizationStats(recordList);
        }
        preProcessor.genderEncoder(recordList); 
        preProcessor.normalizeData(recordList); 

        return recordList;
    }

    /**
     * Extracts an integer safely from a cell, handling both Numeric and String formats.
     */
    private Integer getIntegerCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Extracts a double safely from a cell, handling both Numeric and String formats.
     */
    private Double getDoubleCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Extracts a string safely from a cell, handling both String and Numeric formats.
     */
    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return null;
    }
}
