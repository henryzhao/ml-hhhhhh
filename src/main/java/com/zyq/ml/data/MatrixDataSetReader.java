package com.zyq.ml.data;

import java.util.HashMap;


import com.zyq.ml.utils.MatrixFileUtils;

/**
 * @author Eamon
 *
 */
public class MatrixDataSetReader {
	
	
	public static double[][] readFile(String sourceName,int per,int num){
		double[][] sourceFile = MatrixFileUtils.readMatrix(Properties.SOURCE_ROOT + sourceName+".txt");
		int[][] ctrFile = MatrixFileUtils.readIntMatrix(Properties.GEN_ROOT+sourceName+"/per"+per+"/"+num+".txt");

		System.out.println(sourceFile.length+" "+sourceFile[1].length);
		System.out.println(ctrFile.length+" "+ctrFile[1].length);
		for(int i=0;i<ctrFile.length;i++){
			for(int j=0;j<ctrFile[0].length;j++){
				if(ctrFile[i][j]==0)sourceFile[i][j]=0;
			}
		}
		return sourceFile;
	}
	
	public static HashMap<Integer,double[][]> readFile(String sourceName,int per){
		HashMap<Integer,double[][]> finalMap = new HashMap<>();
		for(int i=0;i<Properties.GEN_NUM;i++){
			finalMap.put(i, readFile(sourceName,per,i));
		}

		return finalMap;
	}
	
	public static void main(String[] args) {
		MatrixFileUtils.writeMatrix(MatrixDataSetReader.readFile("rtmatrix", 50, 5),Properties.TEMP_ROOT+"TEST.txt");
	}
	

}
