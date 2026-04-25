package org.proje2.prolab2proje2.data;

import java.util.ArrayList;
import java.util.Iterator;

public class PreProcessor{
    
    public void dataCleaner(ArrayList<UserRecord> userRecordList){ //Pass a ArrayList of UserRecord's = userRecordList

        if(userRecordList.isEmpty()){return;}

        Iterator<UserRecord> iterator=userRecordList.iterator(); // Create a UserRecord iterator and iterate through userRecordList

        while(iterator.hasNext()){ //If next exists

            UserRecord record=iterator.next(); //Go to next item / Start from 0th index (default start = -1)

            //Delete item if: 
            if(record.getCategory()==null || record.getGender()==null ||record.getLineNetTotal()<=0 || record.getClientCode()<=0){
                iterator.remove(); //Remove item 
            }
        }
    }

    public void genderEncoder(ArrayList<UserRecord> userRecordList){ //Pass a ArrayList of UserRecord's = userRecordList

        if(userRecordList.isEmpty()){return;}

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
        double currentTotal=0; 

        for(UserRecord record : userRecordList){ //Iterate through userRecordList

            currentTotal = record.getLineNetTotal();

            if(currentTotal>max){max=currentTotal;} //Find max
            if(currentTotal<min){min=currentTotal;} //Find min
        }

        for(UserRecord record : userRecordList){

            currentTotal=record.getLineNetTotal();
            
            if(min==max){record.setNormalizedLineTotal(0.0);} //If min==max;

            else{
                double normalizedLineTotal=(currentTotal-min)/(max-min); //Normalization formula scaling to 0-1
                record.setNormalizedLineTotal(normalizedLineTotal); //Set normalizedLineTotal
            }   
        }
    }
}
