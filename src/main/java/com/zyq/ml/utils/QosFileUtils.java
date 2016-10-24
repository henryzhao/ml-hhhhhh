package com.zyq.ml.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.zyq.ml.data.Properties;

import au.com.bytecode.opencsv.CSVReader;

public class QosFileUtils {
	
	
	
	
	public static List<String[]> readData(String filePath){
		List<String[]> list = null;
		try {
			File file = new File(filePath);
			FileReader fReader = new FileReader(file);
			CSVReader csvReader = new CSVReader(fReader);
	        list = csvReader.readAll();

	        csvReader.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return list;
	}
	
	
	
	public static void main(String[] args) {
		readData(Properties.SOURCE_ROOT+"/qosData/Amazon1.csv");
	}
	
	
}
