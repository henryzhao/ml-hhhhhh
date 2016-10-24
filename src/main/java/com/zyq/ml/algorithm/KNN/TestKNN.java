package com.zyq.ml.algorithm.KNN;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;

import com.zyq.ml.data.Properties;
import com.zyq.ml.data.QoSDataSetReader;
import com.zyq.ml.entity.QosDataSet;

import org.rosuda.REngine.*;


/**
 * KNN算法测试类
 * 
 * @author henryzhao
 * @qq 836501108
 * @mail yuqizhao@whu.edu.cn
 * @blog henryzhao.github.io
 * @date 2016.03.30
 */
public class TestKNN {
	public static TestKNN t = new TestKNN();
	
	public static int topk=200;//设置选择前多少个相似用户
	public static int numOfUsers=339;//总的用户个数
	public static int serviceNum=55;//针对第几个服务
	public static int userNum=55;//针对第几个用户
	public static int density=10;//矩阵密度
	
	public static double[] CFtrain = new double[topk];
	public static double[] preResult = new double[numOfUsers];
	public static double[] formatPreResult=new double[numOfUsers];
	public static double[] dataOfAll = new double[numOfUsers];
	public static double[] newCFtrain;
	public static String testfile = new File("").getAbsolutePath() + File.separator
			+ "rtmatrix.txt";// 所有的数据文件
	public static List<List<Double>> datas = new ArrayList<List<Double>>();
	

	/**
	 * 从数据文件中读取数据
	 * 
	 * @param datas
	 *            存储数据的集合对象
	 * @param path
	 *            数据文件的路径
	 */
	public void read(List<List<Double>> datas, String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					new File(path)));
			String data = br.readLine();
			List<Double> l = null;
			while (data != null) {
				String t[] = data.split("\t");
				l = new ArrayList<Double>();
				for (int i = 0; i < t.length; i++) {
					l.add(Double.parseDouble(t[i]));
				}
				datas.add(l);
				data = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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


	public static void gamma(List<List<Double>> datas) throws Exception {
		
		for (int w = 0; w < numOfUsers; w++) {
			try {
				List<List<Double>> testDatas = new ArrayList<List<Double>>();
				double[][] resultDouble = new double[numOfUsers][2];// resultDouble是用来存放距离计算结果的339*2的矩阵，第一列是距离值，第二列是对应的用户标示值
				double[][] pccValue=new double[numOfUsers][2];
				
				t.read(testDatas, testfile);
				
				KNN knn = new KNN();
				for (int i = 0; i < testDatas.size(); i++) {
					resultDouble[i][0] = knn.calDistance(testDatas.get(w),
							testDatas.get(i));// resultDouble[0]是两列数据的相似度，resultDouble[1]是序号
					pccValue[i][0] = Similarity.pcc(testDatas.get(w), testDatas.get(i));
					pccValue[i][1]=resultDouble[i][1] = (double) i;
					 
//					 System.out.println(pccValue[i][0]+","+pccValue[i][1]);
				}
				
				

				// for (int i = 0; i < resultDouble.length; i++) {
				// System.out.println(resultDouble[i][0]+" "+resultDouble[i][1]+" "+
				// i);//把计算结果打印出来
				// }
				resultDouble = NumberSort.bubbleSortD(resultDouble);// 对数据进行排序，按照从小到大的顺序，就得到距离从小到大了
				pccValue = NumberSort.bubbleSortD(pccValue);
				// for (int i = 0; i < resultDouble.length; i++) {
				// System.out.println(resultDouble[i][0]+" "+resultDouble[i][1]
				// );//得到新的数据
				// }
				// 距离最小的前100个数据的编号
				int[] topNum = new int[topk];
				for (int i = 1; i < topk; i++) {
					topNum[i] = (int) resultDouble[i][1];
					// System.out.println(topNum[i]);
				}
				// 距离最小的前n个数据对应的数据值

				for (int i = 0; i < topNum.length; i++) {
					if (testDatas.get(topNum[i]).get(serviceNum) != -1) {
						CFtrain[i] = testDatas.get(topNum[i]).get(serviceNum);
						// System.out.print(CFtrain[i]+",");
					} else {

					}
				}

				// System.out.println();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 计算除去0以后新数组元素的个数
			int j = 0;
			for (int i = 0; i < CFtrain.length; i++) {
				if (CFtrain[i] != 0) {
					j++;
				}
			}
			// 去除在数据中产生的0
			newCFtrain = removeZero(CFtrain, j);
			// 调用R语言进行计算
			try {
				preResult[w]=RScript.callRScript(newCFtrain, w);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < 339; i++) {
			dataOfAll[i]=datas.get(0).get(i);
		}
		
//将结果输出到文件里面
		File file=new File(".//t"+topk+".txt");
		FileOperation.writeTxtFile(preResult,file); 
		for (int i = 0; i < preResult.length; i++) {
			double temp=preResult[i];
			formatPreResult[i]=(double)Math.round(temp*1000)/1000;
		}
		//File file1=new File(".//t1.txt");
		//FileOperation.writeTxtFile(formatPreResult,file1);
		
		double[] preResult = new double[numOfUsers];
		double[] dataOfAll = new double[numOfUsers];
		double[][] result = new double[numOfUsers][2];
		String datafile = new File("").getAbsolutePath() + File.separator
				+ "t"+topk+".txt";// 得到
		String testfile = new File("").getAbsolutePath() + File.separator
				+ "rtmatrix.txt";// 所有的数据文件
		//List<List<Double>> datas = new ArrayList<List<Double>>();
		List<List<Double>> testdatas = new ArrayList<List<Double>>();
		t.read(datas, datafile);
		t.read(testdatas, testfile);
		for (int i = 0; i < datas.get(0).size(); i++) {
			preResult[i]=datas.get(0).get(i);
		}
		
			for (int i = 0; i < testdatas.size(); i++) {
				dataOfAll[i]=testdatas.get(i).get(serviceNum);
			}
			Algorithm.clean(dataOfAll);
			result[serviceNum][0]=RScript.callRScriptRMSE(preResult, dataOfAll,serviceNum);
			result[serviceNum][1]=RScript.callRScriptMAE(preResult, dataOfAll,serviceNum);
		System.out.println();

		
//计算RMSE
		//		callRScriptRMSE(preResult,dataOfAll);
	}
	/**
	 * 程序执行入口
	 * 
	 * @param args
	 * @throws Exception 
	 */

	
	
	
	public static void main(String[] args) throws Exception {
		
		//t.read(datas,testfile);
		//datas=Algorithm.randomDelete(datas, density);//可以改变矩阵密度
		File file=new File(Properties.SOURCE_ROOT+"qosData");
		File[] fileList = file.listFiles();
		for(File f:fileList){
			String name = f.getName().replaceAll("\\.csv", "");
			System.out.println(name);
			QosDataSet dataset = QoSDataSetReader.readFile(name, 30, 3);
			List<Double> data = new ArrayList<>();
			for(int i=0;i<dataset.getTrainSet().size();i++){
				data.add(dataset.getTrainSet().get(i).getResp());
			}
			datas.add(data);
		}
		

//		numOfUsers=datas.size();
//		//UMean平均值方法：基于用户的平均值方法，用其他用户的时间的平均值来作为预测值
//		Algorithm.UMean(datas,serviceNum);
//		//IMean平均值方法：基于服务的平均值方法，用该用户调用其他服务所用的响应时间的平均值作为预测值
//		Algorithm.IMean(datas,userNum);
//		//UPCC算法：基于相似用户的算法
//		Algorithm.UPCC(datas,serviceNum,userNum);
//		//IPCC算法：基于相似服务的算法
//		//Algorithm.IPCC(datas,serviceNum,userNum);
//		//gamma方法
//
//
//			dataset.getTrainSet().get(i).getResp();

		
		
		gamma(datas);
		
	}

	
}
