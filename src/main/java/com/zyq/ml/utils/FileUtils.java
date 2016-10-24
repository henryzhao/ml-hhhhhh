package com.zyq.ml.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public abstract class FileUtils {
	
	public static void writeFile(String fileLocation, String content) {
        try {
            File file = new File(fileLocation);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file, true)));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public abstract void process(String s);
	
	public void readFileFromFolder(String folderName){
		File files[] = (new File(folderName)).listFiles();
		
	}
	
	
	public void readData(String fileName){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			String line="";
			while ((line = br.readLine()) != null) {
				process(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static int[][] readIntMatrix(String fileName, int userNumber, int itemNumber) {
		int[][] result = new int[userNumber][itemNumber];
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			
			String line;
			int index = 0;
			while ((line = br.readLine()) != null) {
				if(index >= userNumber) break;
				String[] temp = line.split("\t");
				for (int j = 0; j < temp.length; j++) {
					result[index][j] = Integer.parseInt(temp[j]);
					//System.out.println(result[index][j]);
				}
				index++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static void main(String[] args) {

	}

}



