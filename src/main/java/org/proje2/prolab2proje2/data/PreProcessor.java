package org.proje2.prolab2proje2.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class PreProcessor{

    private Map<String,Integer> cityMap;

    public PreProcessor(){

        cityMap= new HashMap<>();
        initializeCityMap();
    }

    //City - ID pairs
    private void initializeCityMap(){
        cityMap.put("adana", 1);
        cityMap.put("adiyaman", 2);
        cityMap.put("afyonkarahisar", 3);
        cityMap.put("agri", 4);
        cityMap.put("amasya", 5);
        cityMap.put("ankara", 6);
        cityMap.put("antalya", 7);
        cityMap.put("artvin", 8);
        cityMap.put("aydin", 9);
        cityMap.put("balikesir", 10);
        cityMap.put("bilecik", 11);
        cityMap.put("bingol", 12);
        cityMap.put("bitlis", 13);
        cityMap.put("bolu", 14);
        cityMap.put("burdur", 15);
        cityMap.put("bursa", 16);
        cityMap.put("canakkale", 17);
        cityMap.put("cankiri", 18);
        cityMap.put("corum", 19);
        cityMap.put("denizli", 20);
        cityMap.put("diyarbakir", 21);
        cityMap.put("edirne", 22);
        cityMap.put("elazig", 23);
        cityMap.put("erzincan", 24);
        cityMap.put("erzurum", 25);
        cityMap.put("eskisehir", 26);
        cityMap.put("gaziantep", 27);
        cityMap.put("giresun", 28);
        cityMap.put("gumushane", 29);
        cityMap.put("hakkari", 30);
        cityMap.put("hatay", 31);
        cityMap.put("isparta", 32);
        cityMap.put("icel", 33); // Mersin
        cityMap.put("mersin", 33);
        cityMap.put("istanbul", 34);
        cityMap.put("izmir", 35);
        cityMap.put("kars", 36);
        cityMap.put("kastamonu", 37);
        cityMap.put("kayseri", 38);
        cityMap.put("kirklareli", 39);
        cityMap.put("kirsehir", 40);
        cityMap.put("kocaeli", 41);
        cityMap.put("konya", 42);
        cityMap.put("kutahya", 43);
        cityMap.put("malatya", 44);
        cityMap.put("manisa", 45);
        cityMap.put("kahramanmaras", 46);
        cityMap.put("mardin", 47);
        cityMap.put("mugla", 48);
        cityMap.put("mus", 49);
        cityMap.put("nevsehir", 50);
        cityMap.put("nigde", 51);
        cityMap.put("ordu", 52);
        cityMap.put("rize", 53);
        cityMap.put("sakarya", 54);
        cityMap.put("samsun", 55);
        cityMap.put("siirt", 56);
        cityMap.put("sinop", 57);
        cityMap.put("sivas", 58);
        cityMap.put("tekirdag", 59);
        cityMap.put("tokat", 60);
        cityMap.put("trabzon", 61);
        cityMap.put("tunceli", 62);
        cityMap.put("sanliurfa", 63);
        cityMap.put("usak", 64);
        cityMap.put("van", 65);
        cityMap.put("yozgat", 66);
        cityMap.put("zonguldak", 67);
        cityMap.put("aksaray", 68);
        cityMap.put("bayburt", 69);
        cityMap.put("karaman", 70);
        cityMap.put("kirikkale", 71);
        cityMap.put("batman", 72);
        cityMap.put("sirnak", 73);
        cityMap.put("bartin", 74);
        cityMap.put("ardahan", 75);
        cityMap.put("igdir", 76);
        cityMap.put("yalova", 77);
        cityMap.put("karabuk", 78);
        cityMap.put("kilis", 79);
        cityMap.put("osmaniye", 80);
        cityMap.put("duzce", 81);
    }
    
    public void dataCleaner(ArrayList<UserRecord> userRecordList){ //Pass a ArrayList of UserRecord's = userRecordList

        if(userRecordList.isEmpty()){return;}

        Iterator<UserRecord> iterator=userRecordList.iterator(); // Create a UserRecord iterator and iterate through userRecordList

        while(iterator.hasNext()){ //If next exists

            UserRecord record=iterator.next(); //Go to next item / Start from 0th index (default start = -1)

            //Delete item if: 
            if(record.getCategory()==null || record.getGender()==null ||record.getLineNetTotal()<=0 || record.getCityID()==0){
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

    public void cityEncoder(ArrayList<UserRecord> userRecordList){

        String cityName;
        Integer cityID;

        for(UserRecord record : userRecordList){

            cityName=record.getCityName();

            if(cityName!=null){

                cityID=cityMap.get(cityName.toLowerCase(Locale.forLanguageTag("tr-TR")).trim());

                if(cityID!=null){record.setCityID(cityID);}

                else{record.setCityID(0);}
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
            
            //BAK!! - Biraz fazla Edge case 
            if(min==max){record.setNormalizedLineTotal(0.0);} //If min==max;

            else{
                double normalizedLineTotal=(currentTotal-min)/(max-min); //Normalization formula scaling to 0-1
                record.setNormalizedLineTotal(normalizedLineTotal); //Set normalizedLineTotal
            }   
        }
    }
}
