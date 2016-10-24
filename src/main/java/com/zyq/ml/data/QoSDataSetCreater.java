package com.zyq.ml.data;

import java.io.File;
import java.util.List;

import com.zyq.ml.utils.QosFileUtils;

public class QoSDataSetCreater {
	public static final String QOS_PATH = Properties.SOURCE_ROOT + "/qosData/";

	private static void genDataSet(String fileName,int num) {
		List<String[]> content = QosFileUtils.readData(fileName);
		MatrixDataSetCreater.createDataSet(Properties.GEN_ROOT+fileName.replaceAll("^.*\\\\", "").replaceAll("\\.csv$", "").trim(), num, content.size(), 1);
	}

	public static void genDataSetFromFolder(String folderName){
		  File file=new File(folderName);
		  File[] fileList = file.listFiles();
		
		  for (int i = 0; i < fileList.length; i++) {
			  genDataSet(fileList[i].getPath(),Properties.GEN_NUM);
		  }
	}
	
	
	public static void main(String[] args) {
		genDataSetFromFolder(QOS_PATH);
	}
	
}
