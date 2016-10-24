package com.zyq.ml.data;

import java.io.File;

import com.zyq.ml.utils.MatrixFileUtils;

/**
 * @author Eamon
 *
 */
public class MatrixDataSetCreater {

	public static void genDataSet(String fileName){
		int[] x = MatrixFileUtils.getMatrixSize(Properties.SOURCE_ROOT + fileName);
		String folderName =Properties.GEN_ROOT + fileName.replaceAll("\\..*$", "");
		createDataSet(folderName,Properties.GEN_NUM,x[0],x[1]);
	}
	
	
	public static void createDataSet(String folderName,int num,int vNum,int hNum){
		for(int per:Properties.ratioCtr){
			double ratio = per/100.0;
			String newFolderName=folderName+"/per"+per;
			File file =new File(newFolderName);  
			if  (!file .exists()  && !file .isDirectory())      
			{       
			    file .mkdir();  
			}
			for(int i=0;i<num;i++){
				String fileName=newFolderName+"/"+i+".txt";
				createDataSet(fileName,ratio,vNum,hNum);
				System.out.println("FINISHED FILE"+i);
			}
			System.out.println("FINISHED PER"+per);
		}
		
		
	}
	
	
	public static void createDataSet(String filePath,double ratio,int vNum,int hNum){
		int[][] dataSet = new int[vNum][hNum];
		long missNum = (long)(vNum*hNum*ratio);
		
		int k=0;
		for(int i=0;i<vNum;i++){
			for(int j=0;j<hNum;j++){
				if(k<missNum)dataSet[i][j]=0;
				else dataSet[i][j]=1;
				k++;
			}
		}	
		
		for(int i=0;i<vNum;i++){
			for(int j=0;j<hNum;j++){
				int a=(int)(Math.random()*vNum);
				int b=(int)(Math.random()*hNum);
				k = dataSet[a][b];
				dataSet[a][b] = dataSet[i][j];
				dataSet[i][j] = k;
			}
		}
				
		MatrixFileUtils.writeMatrix(dataSet, filePath);
	}
	
	
	public static void main(String[] args) {
		MatrixDataSetCreater.genDataSet("rtmatrix.txt");
		
		
		
	}
}
