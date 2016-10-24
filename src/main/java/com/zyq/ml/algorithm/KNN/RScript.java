package com.zyq.ml.algorithm.KNN;



import java.util.List;

import org.omg.CORBA.TRANSACTION_MODE;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;

public class RScript {

	/**
	 * 调用R语言实现rmse
	 * 
	 * @param
	 * @throws
	 */
	public static double callRScriptRMSE(double[] preResult, double[] dataOfAll,int j)
			throws REXPMismatchException, REngineException {
		// TODO Auto-generated method stub
		RConnection rc = new RConnection();
		rc.assign("y", preResult);
		rc.assign("ry", dataOfAll);
		REXP rexp = rc.eval("sqrt(sum((y-ry)^ 2))/length(ry)");
		System.out.println("第"+j+"个RMSE:" + rexp.asDouble());
		return rexp.asDouble();
	}

	/**
	 * 调用R语言实现MAE
	 * 
	 * @param
	 * @throws
	 */
	public static double callRScriptMAE(double[] preResult, double[] dataOfAll,int j)
			throws REXPMismatchException, REngineException {
		// TODO Auto-generated method stub
		RConnection rc = new RConnection();
		rc.assign("x", preResult);
		rc.assign("rx", dataOfAll);
		rc.eval("library(Metrics)");
		REXP rexp = rc.eval("mae(x,rx)");
		System.out.println("第"+j+"个MAE:" + rexp.asDouble());
		return rexp.asDouble();
	}
	/**
	 * R语言实现PCC
	 * @param a
	 * @param b
	 * @return
	 */
	public static double callRScriptPCC(List<Double> a,List<Double> b) throws REXPMismatchException, REngineException {
		double []preData=Algorithm.trans(a);
		double []realValue=Algorithm.trans(b);
//		double [][]matrix=new double[a.size()][2];
//		for (int i = 0; i < matrix.length; i++) {
//			matrix[i][0]=preData[i];
//			matrix[i][1]=realValue[i];
//		}
//		String string=new String(Double.toHexString(matrix));
		RConnection rConnection=new RConnection();
		rConnection.eval("library(PresenceAbsence)");
		rConnection.assign("x", preData);
		rConnection.assign("y", realValue);
		REXP rexp1=rConnection.eval("pcc(cmx(x))");
		//REXPGenericVector rexp=(REXPGenericVector)rConnection.eval("pcc(cmx(x))");
		System.out.println(rexp1.asList().firstElement());
		return 0;
	}
	/**
	 * 判断是否是gamma函数
	 * @param a
	 * @return
	 */
	public static boolean isGamma(double a) {
		if (a<0.05) {
			return false;
		}
		return true;
	}

	/**
	 * 运用R语言来，先对数据进行gamma参数训练，再利用gamma函数进行预测
	 * 
	 * @param datas
	 *            存储数据的集合对象
	 * @param path
	 *            数据文件的路径
	 */
	public static double callRScript(double[] dataTrain, int num)
			throws REXPMismatchException, REngineException {
		RConnection rc = new RConnection();
		// source函数需要给出R脚本路径, 注意传入转义的引号
		rc.eval("source(\"/Users/zhaoyuqi/Documents/workspace/KNN/src/KNN/gamma.R\")");
		// double [] dataX =
		// {5.982,6.185,6.348,6.474,6.022,5.752,9.743,5.969,12.332,5.943,5.961,5.875,6.272,6.089,6.28,6.263,6.404,1.369,7.127,2.782,0.578,0.654,4.605,2.188,2.506,1.058,1.486,1.282,0.966,0.823,0.707,0.854,1.613,1.092,0.746,2.349,1.001,1.306,1.798,1.156,1.77,0.955,1.058,0.984,1.706,0.832,1.035,0.944,1.491,2.756,2.641,0.612,1.099,1.432,2.226,1.6,2.876,1.287,1.25,1.198,4.221,3.425,1.084,0.794,4.068,1.148,2.363,0.699,1.653,1.027,1.434,3.236,1.41,1.033,4.719,0.978,0.878,1.618,1.739,3.95,2.076,0.996,1.045,1.937,1.766,1.138,0.92,0.664,1.285,0.952,1.449,0.904,1.657,1.67,1.289,8.743,1.299,1.874};
		double[] dataX1 = dataTrain;

		rc.assign("dat", dataX1);
		REXPGenericVector rexp = (REXPGenericVector) rc
				.eval("optim(c(2,1),logL,dat=dat)");
		REXPDouble object = (REXPDouble) rexp.asList().firstElement();
		double[] resultDoubles = object.asDoubles();
		System.out.println("第" + (num + 1) + "个用户参数训练结果为：alpha="
				+ resultDoubles[0] + " beta=" + resultDoubles[1]);
		rc.assign("a", resultDoubles);
		//验证是否是gamma分布
		REXPGenericVector rexpTest=(REXPGenericVector)rc.eval("ks.test(rgamma(1000,a[1],a[2]),'pgamma',a[1],a[2])");
        REXPDouble objectTest = (REXPDouble)rexpTest.asList().elementAt(1);
        double Test=objectTest.asDouble();
        
        
        if (isGamma(Test)) {
			
		
		REXPDouble rexp1 = (REXPDouble) rc.eval("mean(rgamma(10,a[1],a[2]))");
		System.out.println("预测结果为：" + rexp1.asDouble());
		return rexp1.asDouble();
        }else {
			System.out.println("参数不满足gamma分布！");
			return Algorithm.mean(dataTrain);
		}

	}
	

}
