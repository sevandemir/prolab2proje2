package org.proje2.prolab2proje2.data;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

public class PreProcessor{
    
    public static double minValueOfDataset=Double.MAX_VALUE;
    public static double maxValueOfDataset=Double.MIN_VALUE;
    public static double averageValueOfDataset=0.0;

    public void dataCleaner(ArrayList<UserRecord> userRecordList){ //Pass a ArrayList of UserRecord's = userRecordList

        userRecordList.removeIf(record->record==DataLoader.INVALID_USER_RECORD_TEMPLATE //Check for Invalid templates first to speed up the process
            || record.getLineNetTotal()<=0.0 //Logic error : Money spent cant be 0 or negative
            || record.getClientCode()<=0 //Logic error : Client code cant be 0 or negative 
            || (!"K".equalsIgnoreCase(record.getGender().trim()) && !"E".equalsIgnoreCase(record.getGender().trim())) //IF gender is not equal to "K/k - E/e" - Yoda condition :D
        );
    }

    public void genderEncoder(ArrayList<UserRecord> userRecordList){ //Pass a ArrayList of UserRecord's = userRecordList

        for(UserRecord record : userRecordList){ //Iterate through userRecordList

            if(record.getGender().equalsIgnoreCase("K")){

                record.setEncodedGender(0); //If female encodedGender=0;
            }
            if(record.getGender().equalsIgnoreCase("E")){
                record.setEncodedGender(1); //If male encodedGender=1;
            }
        }
    }

    public void calculateNormalizationStats(ArrayList<UserRecord> userRecordList){

        DoubleSummaryStatistics stats=userRecordList.stream()
                                        .collect(Collectors.summarizingDouble(UserRecord::getLineNetTotal));

        averageValueOfDataset=stats.getAverage();
        maxValueOfDataset=stats.getMax();
        minValueOfDataset=stats.getMin();
    }

    public void normalizeData(ArrayList<UserRecord> userRecordList){ //Pass a ArrayList of UserRecord's = userRecordList

        double currentLineNetTotal=0; 

        for(UserRecord record : userRecordList){ //Start normalization process

            currentLineNetTotal=record.getLineNetTotal();

            double normalizedLineTotal=(currentLineNetTotal-minValueOfDataset)/(maxValueOfDataset-minValueOfDataset); //Normalization formula scaling to 0-1
            record.setNormalizedLineTotal(normalizedLineTotal); //Set normalizedLineTotal
        }
    }

    public static double normalizeInputFromUserInput(double lineNetTotalInput){
        
        return (lineNetTotalInput-minValueOfDataset)/(maxValueOfDataset-minValueOfDataset); //Return normalized value of user input for KNN algorithm 
    }
    
    public static int encodeGenderFromUserInput(String genderInput){

        if("K".equalsIgnoreCase(genderInput)){
            return 0; //For female input
        }
        else{
            return 1; //For male input
        }
    }
}
