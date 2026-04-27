package org.proje2.prolab2proje2.data;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class DataLoader{
    
    public static final UserRecord INVALID_USER_RECORD_TEMPLATE = new UserRecord(0, null, 0.0, null);

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
                Cell clientCodeCell = excelRow.getCell(9); //Get Client Code Cell
                int clientCode;

                if(clientCodeCell!=null && clientCodeCell.getCellType()==CellType.STRING){ //If cell is not empty and contains a STRING!
                    clientCode=Integer.parseInt(clientCodeCell.getStringCellValue().trim()); //Get clientCode and cast it to int to process it in preprocessing
                }
                else{
                    recordList.add(INVALID_USER_RECORD_TEMPLATE); //If read an invalid input add with Invalid template so we can delete at preprocessing
                    continue;
                }

                //Line Net Total Block
                Cell lineNetTotalCell = excelRow.getCell(8); //Get cell of Line Net Total
                double lineNetTotal;

                if(lineNetTotalCell!=null && lineNetTotalCell.getCellType()==CellType.NUMERIC){ //If cell is not empty and contains a numeric 
                    lineNetTotal = lineNetTotalCell.getNumericCellValue(); //Get line net total
                }
                else{
                    recordList.add(INVALID_USER_RECORD_TEMPLATE); //If read an invalid input add with Invalid template so we can delete at preprocessing
                    continue;
                }
                
                //Gender Block
                Cell genderCell = excelRow.getCell(17); //Get cell of gender
                String gender;

                if(genderCell!=null && genderCell.getCellType()==CellType.STRING){ //If cell is not empty and contains a string 
                    gender = genderCell.getStringCellValue(); //Get gender
                }
                else{
                    recordList.add(INVALID_USER_RECORD_TEMPLATE); //If read an invalid input add with Invalid template so we can delete at preprocessing
                    continue;
                }
                
                //Category Block
                Cell categoryCell=excelRow.getCell(12); //Get cell of category
                String category;

                if(categoryCell!=null && categoryCell.getCellType()==CellType.STRING){ //If cell is not empty and contains a string 
                    category = categoryCell.getStringCellValue(); //Get category
                }
                else{
                    recordList.add(INVALID_USER_RECORD_TEMPLATE); //If read an invalid input add with Invalid template so we can delete at preprocessing
                    continue;
                }

                UserRecord userRecord = new UserRecord(clientCode, gender, lineNetTotal, category); //Create UserRecord object
                recordList.add(userRecord); //Add user record to Array List
            }
        }

        catch(IOException error){
            System.err.println("File error!");
        }
        finally{
            System.out.println("Done");
        }
        
        PreProcessor preProcessor = new PreProcessor(); //Initialize preprocessor

        preProcessor.dataCleaner(recordList); //Clean corrupted data with preprocessor data cleaner
        preProcessor.calculateNormalizationStats(recordList);//Calculate normalization values for KNN
        preProcessor.genderEncoder(recordList); //Encode gender for KNN algorithm
        preProcessor.normalizeData(recordList); //Normalize data for KNN algorithm

        return recordList; //Return record list
    }
}
