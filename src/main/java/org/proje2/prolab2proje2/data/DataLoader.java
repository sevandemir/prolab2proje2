package org.proje2.prolab2proje2.data;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class DataLoader{
    
    public ArrayList<UserRecord> loadDataFromExcel(String filePath){

        ArrayList<UserRecord> recordList = new ArrayList<>(); //Create a new record list made of UserRecords
        File newFile = new File(filePath); //File for dataset

        try(Workbook workbook = WorkbookFactory.create(newFile)){

            Sheet excelSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = excelSheet.iterator(); //Row iterator to iterate through whole sheet

            if(rowIterator.hasNext()){
                rowIterator.next(); //Skip headline 
            }

            while(rowIterator.hasNext()){ //Iterating through the dataset

                Row excelRow = rowIterator.next(); //Catch lines

                //Client Code Block
                Cell clientCodeCell = excelRow.getCell(9);
                int clientCode;

                if(clientCodeCell!=null && clientCodeCell.getCellType()==CellType.NUMERIC){ //If cell is not empty and contains a numeric
                    clientCode=(int)clientCodeCell.getNumericCellValue(); //Get cell value
                }
                else{clientCode=0;} //Assign 0 for preprocessor

                //Line Net Total Block
                Cell lineNetTotalCell = excelRow.getCell(8);
                double lineNetTotal;

                if(lineNetTotalCell!=null && lineNetTotalCell.getCellType()==CellType.NUMERIC){
                    lineNetTotal = lineNetTotalCell.getNumericCellValue();
                }
                else{lineNetTotal=0;}
                
                //Gender Block
                Cell genderCell = excelRow.getCell(17);
                String gender;

                if(genderCell!=null && genderCell.getCellType()==CellType.STRING){ //If cell is not empty and contains a string 
                    gender = genderCell.getStringCellValue(); //Get cell value
                }
                else{gender=null;} //Assign null for preprocessor
                
                //Category Block
                Cell categoryCell=excelRow.getCell(12);
                String category;

                if(categoryCell!=null && categoryCell.getCellType()==CellType.STRING){
                    category = categoryCell.getStringCellValue();
                }
                else{category=null;}

                UserRecord userRecord = new UserRecord(clientCode, gender, lineNetTotal, category); //Create UserRecord object
                recordList.add(userRecord); //Add user record to Array List
            }
        }

        catch(IOException error){
            System.err.println("File error!");
        }
        return recordList; //Return record list for preprocessors 
    }
}
