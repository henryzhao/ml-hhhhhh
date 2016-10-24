package com.zyq.ml.algorithm.KNN;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

public class Test {


	public static void main(String[] args) throws REXPMismatchException, REngineException {
		// TODO Auto-generated method stub
		TestKNN t = new TestKNN();
//		double[] CFtrain = new double[100];
		
		int topk=200;
		int numOfUsers=339;//总的用户个数
		int serviceNum=55;
		
		double[] preResult = new double[numOfUsers];
		double[] dataOfAll = new double[numOfUsers];
		double[][] result = new double[numOfUsers][2];
		String datafile = new File("").getAbsolutePath() + File.separator
				+ "t"+topk+".txt";// 得到
		String testfile = new File("").getAbsolutePath() + File.separator
				+ "rtmatrix.txt";// 所有的数据文件
		List<List<Double>> datas = new ArrayList<List<Double>>();
		List<List<Double>> testdatas = new ArrayList<List<Double>>();
		t.read(datas, datafile);
		t.read(testdatas, testfile);
		for (int i = 0; i < datas.get(0).size(); i++) {
			preResult[i]=datas.get(0).get(i);
		}
		
			for (int i = 0; i < testdatas.size(); i++) {
				dataOfAll[i]=testdatas.get(i).get(serviceNum);
			}
			clean(dataOfAll);
			result[serviceNum][0]=RScript.callRScriptRMSE(preResult, dataOfAll,serviceNum);
			result[serviceNum][1]=RScript.callRScriptMAE(preResult, dataOfAll,serviceNum);
		
		
		
		System.out.println();
	}
	/**
	 * 清洗数据，把数据中的0和-1都去掉
	 * @param oldData
	 * @return
	 */
	public static double [] clean(double [] oldData) {
		int n=oldData.length;
		double[] tempData=new double[n];
		for (int i = 0; i < oldData.length; i++) {
			if (oldData[i]!=-1) {
				tempData[i]=oldData[i];
			}
		}
		int j=0;
		for (int i = 0; i < tempData.length; i++) {
			if (tempData[i] != 0) {
				j++;
			}
		}
		double[] returnData=removeZero(tempData,j);
		return returnData;
	}
	/**
	 * 去除数组中的0
	 * 
	 * @param 原始数组
	 *            ，新数组元素个数
	 * @param 去0后的新数组
	 */
	public static double[] removeZero(double[] a, int j) {

		double[] newarr = new double[j];
		int k = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != 0) {
				newarr[k] = a[i];
				k++;
			}
		}
		return newarr;
	}

}
