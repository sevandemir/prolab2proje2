package org.proje2.prolab2proje2.data;

import java.util.ArrayList;

public class PreProcessor{
    
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

    public void normalizeData(ArrayList<UserRecord> userRecordList){ //Pass a ArrayList of UserRecord's = userRecordList
    
        double max=Double.MIN_VALUE; //Initialize with min value to find bigger one
        double min=Double.MAX_VALUE; //Initialize with max vlaue to find smaller one
        double currentLineNetTotal=0; 

        for(UserRecord record : userRecordList){ //Iterate through userRecordList

            currentLineNetTotal = record.getLineNetTotal();

            if(currentLineNetTotal>max){max=currentLineNetTotal;} //Find max
            if(currentLineNetTotal<min){min=currentLineNetTotal;} //Find min
        }

        for(UserRecord record : userRecordList){ //Start normalization process

            currentLineNetTotal=record.getLineNetTotal();

            double normalizedLineTotal=(currentLineNetTotal-min)/(max-min); //Normalization formula scaling to 0-1
            record.setNormalizedLineTotal(normalizedLineTotal); //Set normalizedLineTotal
        }
    }
}
