package com.zyq.ml.algorithm.gamma;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import com.zyq.ml.algorithm.arima.ARIMA;
import com.zyq.ml.data.Properties;
import com.zyq.ml.data.QoSDataSetReader;
import com.zyq.ml.entity.QosDataSet;
import com.zyq.ml.entity.QosItem;
import com.zyq.ml.utils.FileUtils;

/**
 * @author Eamon
 *
 */
public class TestGamma {

	private File fileList = null;
	
	private RInvoke rInvoke= null;
	
	private double[] preResult = null; 
	
	private double[] dataOfAll = null; 
	
	public TestGamma() {
		try {
			rInvoke = new RInvoke();
		} catch (RserveException e) {
			System.out.println("INIT R ERROR");
		}
		
		fileList = new File(Properties.SOURCE_ROOT+"qosData");

	}
	
	public void run(int per,int num){
		System.out.println("=====================================");
		for (String s : fileList.list()) {
			
			runSingleSet(per, num, s,1);
			
		}
	}

	private void runSingleSet(int per, int num, String filename,int caseNum) {
		QosDataSet dataset = QoSDataSetReader.readFile(filename.replaceAll("\\.csv", ""), per, num);
		List<QosItem> list = dataset.getTrainSet();
		
		List<Double> trainSet = new ArrayList<>();
		double[] train;
		int k =0;
		preResult = new double[(int) (list.size() * (1-per/100)) + 1];
		dataOfAll = new double[(int) (list.size() * (1-per/100)) + 1];
		
		for (int i = 0; i < list.size(); i++) {
			QosItem e = list.get(i);
			trainSet.add(e.getResp());
		}
		
		
		Collections.sort(trainSet, new SortByResp());

		double Q3 = trainSet.get((int) (trainSet.size()*0.75));
		double Q1 = trainSet.get((int) (trainSet.size()*0.25));
		double IQR = Q3-Q1;
		
		double upLimit = Q3+1.5*IQR;
		double lowerLimit = 0; 
				//Q1-1.5*IQR;
		trainSet.clear();
		String name = "";
		for (int i = 0; i < list.size(); i++) {
			QosItem e = list.get(i);
			if(e.getResp()>upLimit||e.getResp()<lowerLimit){
				continue;
			}
			if (e.isTrain()) {
				trainSet.add(e.getResp());
			}else{
				int trainNum = trainSet.size();
				if(trainNum==0){
					trainNum = 1;
					trainSet.add(e.getResp());
				}
				train = new double[trainSet.size()];
				
				int j = 0;
				for(double d:trainSet){
					train[j++] = d;
				}
				
				if(train.length<300)continue;
				
				ARIMA arima=new ARIMA(train);
				//int []model= arima.getARIMAmodel1();
				
				switch(caseNum){
				case 0:
					preResult[k]= before(train);
					name ="before：";
					break;
				case 1:
					preResult[k]= 0.0;
					//arima.aftDeal(arima.predictValue(model[0],model[1])); 
					name ="arima：";
					break;
				case 2:
					preResult[k]= trainGamma(train);
					name ="gamma：";
					break;
				default:
					break;
				}
				name+="\r\n";
				dataOfAll[k] = e.getResp();
				k++;
				//System.out.println(trainGamma(train));
				
			}
			
		}
		
		try {
			FileUtils.writeFile(Properties.TEMP_ROOT+"reault.txt", name);
			String preData = "";
			String allData = "";
			for(int i=0;i<dataOfAll.length;i++){
				if(dataOfAll[i]==0.0)continue;
				preData+=preResult[i]+"\t";
				allData+=dataOfAll[i]+"\t";
			}
			preData+="\r\n";
			allData+="\r\n";
			FileUtils.writeFile(Properties.TEMP_ROOT+"reault.txt", preData);
			FileUtils.writeFile(Properties.TEMP_ROOT+"reault.txt", allData);
			FileUtils.writeFile(Properties.TEMP_ROOT+"reault.txt","RMSE:"+rInvoke.callRScriptRMSE(preResult, dataOfAll)+"\r\n");
			FileUtils.writeFile(Properties.TEMP_ROOT+"reault.txt","MAE:"+rInvoke.callRScriptMAE(preResult, dataOfAll)+"\r\n\r\n\r\n");
			
			System.out.println("\n"+name);
			System.out.println("RMSE:"+rInvoke.callRScriptRMSE(preResult, dataOfAll));
			System.out.println("MAE:"+rInvoke.callRScriptMAE(preResult, dataOfAll));
			
		} catch (REXPMismatchException | REngineException e) {
			e.printStackTrace();
		}
	}
	
	private double mid(double train[]){
		double total=0.0;
		for(double d:train){
			total+=d;
		}
		
		return total/train.length;
	}
	
	private double before(double train[]){
		
		return train[train.length-1];
	}
	
	
	private double trainGamma(double train[]){
		try {
			rInvoke.assign("dat", train);
			rInvoke.assign("n", train.length+"");
			
			File file = new File(Properties.RFILES_ROOT + "gamma.R");
			String source = "source(\"" + file.getAbsolutePath() + "\")";
			source = source.replaceAll("\\\\", "/");
			
			rInvoke.eval(source);
			
			double[] resultDoubles = rInvoke.optimResult();
			
			return rInvoke.ksTest(resultDoubles, train);
		} catch (REngineException e) {
			System.out.println("R PARA ERROR");
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	class SortByResp implements Comparator<Object> {
		 public int compare(Object o1, Object o2) {
		  Double s1 = (Double) o1;
		  Double s2 = (Double) o2;
		  return s1.compareTo(s2);
		 }
		}
	
	
	public static void main(String[] args) {
		TestGamma testGamma = new TestGamma();
		String SetName = "StockQuotes1";
		for(int j=1;j<=3;j++){
			FileUtils.writeFile(Properties.TEMP_ROOT+"reault.txt", SetName+" "+10*j+"%\r\n");
			for(int i=0;i<3;i++){
				testGamma.runSingleSet(10*j,3,SetName,i);
				
			}
		}
	}
}
