package com.zyq.ml.algorithm.KNN;



import java.io.DataOutput;
import java.io.File;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

/**
 * 算法工具包 mean均值 UPCC IPCC ARIMA
 * 
 * @author zhaoyuqi
 *
 */
public class Algorithm {
	/**
	 * 求和函数
	 * @param data
	 * @return
	 */
	public static double sum(double[] data) {
		double temp = 0.00;
		for (int i = 0; i < data.length; i++) {
			temp += data[i];
		}
		return temp;
	}

	/**
	 * 平均值算法
	 * 
	 * @param data
	 *            double[]
	 * @return
	 */
	public static double mean(double[] data) {
		double result = 0;
		for (int i = 0; i < data.length; i++) {
			result += data[i];
		}
		return (result / data.length);
	}

	
	/**
	 * 均值
	 * @param data
	 *            List<Double>
	 * @return
	 */
	public static double mean(List<Double> data) {
		double result = 0;
		for (int i = 0; i < data.size(); i++) {
			result += data.get(i);
		}
		return (result / data.size());
	}

	/**
	 * UMean
	 * @param data
	 * @throws REngineException 
	 * @throws REXPMismatchException 
	 */
	public static void UMean(List<List<Double>> data,int serviceNumber) throws REXPMismatchException, REngineException {
		double result=0.00;//声明结果变量
		double []tmp=new double[data.size()];//创建一个新的一维double数组，用于存储某一个服务的所有用户调用值
		double []preValue=new double[data.size()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i]=data.get(i).get(serviceNumber);
		}
		result=mean(tmp);
		for (int i = 0; i < preValue.length; i++) {
			preValue[i]=result;
		}
		System.out.println("UMean");
		RScript.callRScriptMAE(preValue, tmp, serviceNumber);
		RScript.callRScriptRMSE(preValue, tmp, serviceNumber);
		
	}
	/**
	 * 
	 * @param data
	 * @throws REngineException 
	 * @throws REXPMismatchException 
	 */
	public static void IMean(List<List<Double>> data,int userNum) throws REXPMismatchException, REngineException {
		double result=0.00;//声明结果变量
		double []tmp=new double[data.get(0).size()];//创建一个新的一维double数组，用于存储某一个服务的所有用户调用值
		double []preValue=new double[data.get(0).size()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i]=data.get(userNum).get(i);
		}
		result=mean(tmp);
		for (int i = 0; i < preValue.length; i++) {
			preValue[i]=result;
		}
		System.out.println("IMean");
		RScript.callRScriptMAE(preValue, tmp, userNum);
		RScript.callRScriptRMSE(preValue, tmp, userNum);
	}
	/**
	 * UPCC方法
	 * 
	 * @param data
	 * @return
	 * @throws REngineException 
	 * @throws REXPMismatchException 
	 */
	public static void UPCC(List<List<Double>> data,
			int serviceNumber, int userNumber) throws REXPMismatchException, REngineException {
		double []result = new double[data.get(0).size()];// 返回最终结果
		double umean = mean(data.get(userNumber));// 当前用户对所有服务的调用时间的平均值
		double temp = 0.00;// 中间量
		double [][]sim =new double[data.size()][2];
		KNN knn = new KNN();
		for (int i = 0; i < data.size(); i++) {
			sim[i][0] = Similarity.pcc(data.get(userNumber), data.get(i));
//			sim[i][0] = RScript.callRScriptPCC(data.get(userNumber), data.get(i));
			sim[i][1] = (double) i;
			 
//			 System.out.println(sim[i][0]+","+sim[i][1]);
		}
		double[] tmpOfSim = new double[sim.length];// 所有相似度的中间量
		for (int i = 0; i < sim.length; i++) {
			tmpOfSim[i] = sim[i][0];
		}
		double sumSim = sum(tmpOfSim);// 计算相似度之和

		for (int j = 0; j < tmpOfSim.length; j++) {
			for (int i = 0; i < sim.length; i++) {
				temp += tmpOfSim[i] / sumSim
						* (data.get(i).get(serviceNumber) - mean(data.get(i)));
			}
			result[j] = umean + temp;
		}
		System.out.println("UPCC");
		RScript.callRScriptMAE(result, trans(data.get(userNumber)), serviceNumber);
		RScript.callRScriptRMSE(result, trans(data.get(userNumber)), serviceNumber);
		//System.out.println("real value :"+data.get(userNumber).get(serviceNumber)+" ,predict value:"+result);
		
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static double UPCC1(double[] data) {
		return 0;
	}

	/**
	 * IPCC方法
	 * 
	 * @param data
	 * @return
	 */
	public static double IPCC(double[] data) {
		return 0;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static double IPCC1(double[] data) {
		return 0;
	}

	/**
	 * 随机删除entity
	 * 
	 * @param data数据
	 * @param density
	 *            矩阵密度
	 * @return
	 */
	public static double[][] randomDelete(double[][] data, int density) {
		int num = (int) (data.length) * density / 100;

		int[] x = new int[num];
		for (int i = 0; i < x.length; i++) {
			x[i] = (int) (Math.random() * data.length) - 1;
			data[x[i]] = null;
		}
		return data;
	}

	/**
	 * randomDelete 重载
	 * 
	 * @param data
	 *            List<List<Double>>
	 * @param density
	 * @return
	 */
	public static List<List<Double>> randomDelete(List<List<Double>> data,
			int density) {
		int num = (int) (data.size()) * density / 100;
		if (num == 0) {
			return data;
		} else {
			int[] x = new int[num];
			for (int i = 0; i < x.length; i++) {
				x[i] = (int) (Math.random() * data.size()) - 1;
				data.remove(x[i]);
			}
			return data;
		}
	}

	/**
	 * 去除数据中的null值
	 * 
	 * @param data
	 * @return
	 */
	public static double[][] removeNull(double[][] data) {
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				j++;
			}
		}
		double[][] newarr = new double[j][];
		int k = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				newarr[k] = data[i];
				k++;
			}
		}
		return newarr;
	}

	/**
	 * 清洗数据，把数据中的0和-1都去掉
	 * 
	 * @param oldData
	 * @return
	 */
	public static double[] clean(double[] oldData) {
		int n = oldData.length;
		double[] tempData = new double[n];
		for (int i = 0; i < oldData.length; i++) {
			if (oldData[i] != -1) {
				tempData[i] = oldData[i];
			}
		}
		int j = 0;
		for (int i = 0; i < tempData.length; i++) {
			if (tempData[i] != 0) {
				j++;
			}
		}
		double[] returnData = removeZero(tempData, j);
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

	/**
	 * list转double数组
	 * @param list
	 * @return
	 */
	public static double[] trans(List<Double> list) {
		double[] doubles=new double[list.size()];
		for(int i=0;i<list.size();i++)
		{
		    doubles[i]=list.get(i).doubleValue();
		}
		return doubles;
	}
	/**
	 * Algorithm测试主类
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		double[][] data = { { 1, 1, 1, 1, 1 }, { 2, 2, 2, 2, 2 },
				{ 3, 3, 3, 3, 3 }, { 4, 4, 4, 4, 4 }, { 5, 5, 5, 5, 5 },
				{ 6, 6, 6, 6, 6 }, { 1, 1, 1, 1, 1 }, { 2, 2, 2, 2, 2 },
				{ 3, 3, 3, 3, 3 }, { 4, 4, 4, 4, 4 }, { 5, 5, 5, 5, 5 },
				{ 6, 6, 6, 6, 6 } };
		// List<List<Double>>
		// dataList={{1,1,1,1,1},{2,2,2,2,2},{3,3,3,3,3},{4,4,4,4,4},{5,5,5,5,5},{6,6,6,6,6},{1,1,1,1,1},{2,2,2,2,2},{3,3,3,3,3},{4,4,4,4,4},{5,5,5,5,5},{6,6,6,6,6}};
		// double[][] newdata=randomDelete(data,10);
		// double[][] newdata1=removeNull(newdata);
		TestKNN t = new TestKNN();
		String testfile = new File("").getAbsolutePath() + File.separator
				+ "rtmatrix.txt";// 所有的数据文件
		List<List<Double>> datas = new ArrayList<List<Double>>();
		t.read(datas, testfile);
		randomDelete(datas, 10);
	}

}
