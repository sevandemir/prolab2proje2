package org.proje2.prolab2proje2.data;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

/**
 * PreProcessor cleans, encodes, and normalizes user records before sending to classifiers.
 */
public class PreProcessor {
    
    public static double minValueOfDataset = Double.MAX_VALUE;
    public static double maxValueOfDataset = Double.MIN_VALUE;
    public static double averageValueOfDataset = 0.0;

    /**
     * Cleans the dataset by removing empty/invalid records or logically incorrect entries.
     * @param userRecordList The dataset to filter.
     */
    public void dataCleaner(ArrayList<UserRecord> userRecordList) {
        if (userRecordList == null || userRecordList.isEmpty()) return;

        userRecordList.removeIf(record -> record == DataLoader.INVALID_USER_RECORD_TEMPLATE
            || record.getLineNetTotal() <= 0.0 
            || record.getClientCode() <= 0 
            || (!"K".equalsIgnoreCase(record.getGender().trim()) && !"E".equalsIgnoreCase(record.getGender().trim()))
        );
    }

    /**
     * Iterates through the list to encode gender values into numerical representations.
     * @param userRecordList The records to encode.
     */
    public void genderEncoder(ArrayList<UserRecord> userRecordList) {
        if (userRecordList == null || userRecordList.isEmpty()) return;

        for (UserRecord record : userRecordList) {
            record.setEncodedGender(encodeGenderFromUserInput(record.getGender()));
        }
    }

    /**
     * Computes statistics (min, max, average) used for Normalization.
     * @param userRecordList The dataset to analyze.
     */
    public void calculateNormalizationStats(ArrayList<UserRecord> userRecordList) {
        if (userRecordList == null || userRecordList.isEmpty()) {
            minValueOfDataset = 0.0;
            maxValueOfDataset = 1.0;
            averageValueOfDataset = 0.0;
            return;
        }

        DoubleSummaryStatistics stats = userRecordList.stream()
                                        .collect(Collectors.summarizingDouble(UserRecord::getLineNetTotal));

        averageValueOfDataset = stats.getAverage();
        maxValueOfDataset = stats.getMax();
        minValueOfDataset = stats.getMin();
    }

    /**
     * Normalizes numerical spending amounts across the entire dataset to a range of 0-1.
     * @param userRecordList The records to normalize.
     */
    public void normalizeData(ArrayList<UserRecord> userRecordList) {
        if (userRecordList == null || userRecordList.isEmpty()) return;

        for (UserRecord record : userRecordList) {
            record.setNormalizedLineTotal(normalizeInputFromUserInput(record.getLineNetTotal()));
        }
    }

    /**
     * Normalizes a single user's numerical input using previously computed statistics.
     * @param lineNetTotalInput The spending input.
     * @return Normalized double value.
     */
    public static double normalizeInputFromUserInput(double lineNetTotalInput) {
        if (maxValueOfDataset == minValueOfDataset) return 0.0;
        return (lineNetTotalInput - minValueOfDataset) / (maxValueOfDataset - minValueOfDataset);
    }
    
    /**
     * Converts a gender string to its numerical category (0 for female, 1 for male).
     * @param genderInput The input gender string (E/K).
     * @return Int category.
     */
    public static int encodeGenderFromUserInput(String genderInput) {
        if ("K".equalsIgnoreCase(genderInput)) {
            return 0; 
        } else {
            return 1; 
        }
    }
}
