package com.zyq.ml.algorithm.gamma;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import com.zyq.ml.algorithm.arima1.ARIMA;
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
				int []model= arima.getARIMAmodel();
				
				switch(caseNum){
				case 0:
					preResult[k]= mid(train);
					name ="Average：";
					break;
				case 1:
					preResult[k]= before(train);
					name ="Before：";
					break;
				case 2:
					preResult[k]= trainArima(train);
					//arima.aftDeal(arima.predictValueDouble(model[0],model[1]));;
					//arima.aftDeal(arima.predictValue(model[0],model[1])); 
					name ="Arima：";
					break;
				case 3:
					preResult[k]= trainGamma(train);
					name ="Gamma：";
					break;
				case 4:
					preResult[k]= 0.0;
					name ="Arima+Gamma：";
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
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
//			FileUtils.writeFile(Properties.TEMP_ROOT+"result.txt", df.format(new Date())+"\r\n");
			FileUtils.writeFile(Properties.TEMP_ROOT+"result.txt", filename+" "+ per +"%\r\n");
			FileUtils.writeFile(Properties.TEMP_ROOT+"result.txt", name+"\r\n");
			String preData = "";
			String allData = "";
			for(int i=0;i<dataOfAll.length;i++){
				if(dataOfAll[i]==0.0)continue;
				preData+=preResult[i]+",";
				allData+=dataOfAll[i]+",";
			}
			
			preData+="\r\n";
			allData+="\r\n";
//			FileUtils.writeFile(Properties.TEMP_ROOT+"result.txt", preData);
//			FileUtils.writeFile(Properties.TEMP_ROOT+"result.txt", allData);
			FileUtils.writeFile(Properties.TEMP_ROOT+"result.txt","RMSE:"+rInvoke.callRScriptRMSE(preResult, dataOfAll)+"\r\n");
			FileUtils.writeFile(Properties.TEMP_ROOT+"result.txt","MAE:"+rInvoke.callRScriptMAE(preResult, dataOfAll)+"\r\n\r\n\r\n");
			System.out.println("\n"+name);
//			System.out.println("preData:"+preData);
//			System.out.println("oriData:"+allData);
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
			//System.out.println("R PARA ERROR");
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	/*
	 *  forecast arima in R
	 */
	private double trainArima(double train[]) {
		double[] resultDouble=null;
		double tmp=0;
		try {
			rInvoke.eval("library(forecast);");
			rInvoke.assign("arimaData", train);
			rInvoke.eval("arimaDatats<-ts(arimaData);");
			rInvoke.eval("arima1<-auto.arima(arimaDatats);");
			rInvoke.eval("fcast<-forecast(arima1);");
			resultDouble = rInvoke.arimaResult();
			for (int i = 0; i < resultDouble.length; i++) {
				tmp +=resultDouble[i];
			}
		} catch (REngineException e) {
			// TODO: handle exception
			System.out.println("R PARA ERROR");
		}
		return tmp/resultDouble.length;
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
		String [] setNameStrings={"Amazon1","BLiquidity1","CurrencyConverter1","FastWeather1","GetJoke1","Google1","HyperlinkExtractor1","QuoteOfTheDay1","StockQuotes1","XMLDailyFact1"};
		for (int k = 0; k < setNameStrings.length; k++) {
			for(int j=1;j<=3;j++){
				FileUtils.writeFile(Properties.TEMP_ROOT+"reault.txt", setNameStrings[k]+" "+10*j+"%\r\n");
				for(int i=0;i<4;i++){
					testGamma.runSingleSet(10*j,3,setNameStrings[k],i);
					
				}
			}
		}
//		String SetName = "Amazon1";
		
	}
}
