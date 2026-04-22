package org.proje2.prolab2proje2.data;

public class UserRecord{

    //Raw Data 
    private int clientCode;
    private String gender;
    private double lineNetTotal;
    private String category;
    private String cityName;

    //Encoded Data
    private int encodedGender;
    private int cityID;
    private double normalizedLineTotal;

    //Raw records without encoded/normalized data
    public UserRecord(int clientCode, String gender, double lineNetTotal, String category, String cityName){

        this.clientCode=clientCode;
        this.gender=gender;
        this.lineNetTotal=lineNetTotal;
        this.category=category;
        this.cityName=cityName;
    }

    //Getters - Raw
    public int getClientCode(){return clientCode;}
    public String getGender(){return gender;}
    public double getLineNetTotal(){return lineNetTotal;}
    public String getCityName(){return cityName;}
    public String getCategory(){return category;}

    //Getters - Encoded
    public int getEncodedGender(){return encodedGender;}
    public int getCityID(){return cityID;}
    public double getNormalizedLineTotal(){return normalizedLineTotal;}
    
    //Setters for encoding
    public void setEncodedGender(int encodedGender){this.encodedGender=encodedGender;}
    public void setCityID(int cityID){this.cityID=cityID;}
    public void setNormalizedLineTotal(double normalizedLineTotal){this.normalizedLineTotal=normalizedLineTotal;}

}
