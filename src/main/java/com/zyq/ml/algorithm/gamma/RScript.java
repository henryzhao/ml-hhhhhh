package com.zyq.ml.algorithm.gamma;

import java.io.File;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;

import com.zyq.ml.algorithm.KNN.Algorithm;
import com.zyq.ml.data.Properties;

public class RScript {

	/**
	 * 调用R语言实现rmse
	 * 
	 * @param @throws
	 */
	public static double callRScriptRMSE(double[] preResult, double[] dataOfAll)
			throws REXPMismatchException, REngineException {
		// TODO Auto-generated method stub
		RConnection rc = new RConnection();
		rc.assign("y", preResult);
		rc.assign("ry", dataOfAll);
		REXP rexp = rc.eval("sqrt(sum((y-ry)^ 2))/length(ry)");
		return rexp.asDouble();
	}

	/**
	 * 调用R语言实现MAE
	 * 
	 * @param @throws
	 */
	public static double callRScriptMAE(double[] preResult, double[] dataOfAll)
			throws REXPMismatchException, REngineException {
		// TODO Auto-generated method stub
		RConnection rc = new RConnection();
		rc.assign("x", preResult);
		rc.assign("rx", dataOfAll);
		rc.eval("library(Metrics)");
		REXP rexp = rc.eval("mae(x,rx)");
		return rexp.asDouble();
	}

	/**
	 * R语言实现PCC
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	// public static double callRScriptPCC(List<Double> a,List<Double> b) throws
	// REXPMismatchException, REngineException {
	// double []preData=Algorithm.trans(a);
	// double []realValue=Algorithm.trans(b);
	//// double [][]matrix=new double[a.size()][2];
	//// for (int i = 0; i < matrix.length; i++) {
	//// matrix[i][0]=preData[i];
	//// matrix[i][1]=realValue[i];
	//// }
	//// String string=new String(Double.toHexString(matrix));
	// RConnection rConnection=new RConnection();
	// rConnection.eval("library(PresenceAbsence)");
	// rConnection.assign("x", preData);
	// rConnection.assign("y", realValue);
	// REXP rexp1=rConnection.eval("pcc(cmx(x))");
	// //REXPGenericVector
	// rexp=(REXPGenericVector)rConnection.eval("pcc(cmx(x))");
	// System.out.println(rexp1.asList().firstElement());
	// return 0;
	// }
	/**
	 * 判断是否是gamma函数
	 * 
	 * @param a
	 * @return
	 */
	public static boolean isGamma(double a) {
		if (a < 0.05) {
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
	public static double callRScript(RConnection rc,double[] dataTrain, int num) throws REXPMismatchException, REngineException {
		
		// source函数需要给出R脚本路径, 注意传入转义的引号
		File file = new File(Properties.RFILES_ROOT + "gamma.R");

		String source = "source(\"" + file.getAbsolutePath() + "\")";
		source = source.replaceAll("\\\\", "/");

		rc.assign("dat", dataTrain);
		rc.assign("n", num + "");
		rc.eval(source);
		
		REXPGenericVector rexp = (REXPGenericVector) rc.eval("optim(c(2,1),logL,dat=dat)");
		REXPDouble object = (REXPDouble) rexp.asList().firstElement();
		double[] resultDoubles = object.asDoubles();
		System.out.println((num + 1) + "个用户参数训练结果为：alpha=" + resultDoubles[0] + " beta=" + resultDoubles[1]);

		rc.assign("result", resultDoubles);
		
		// 验证是否是gamma分布
		REXPGenericVector rexpTest = (REXPGenericVector) rc.eval("ks.test(rgamma(1000,result[1],result[2]),'pgamma',result[1],result[2])");
		REXPDouble objectTest = (REXPDouble) rexpTest.asList().elementAt(1);
		double Test = objectTest.asDouble();
		if (isGamma(Test)) {
			REXPDouble rexp1 = (REXPDouble) rc.eval("mean(rgamma(10,result[1],result[2]))");
			System.out.println("预测结果为：" + rexp1.asDouble());
			return rexp1.asDouble();
		} else {
			System.out.println("参数不满足gamma分布！");
			return Algorithm.mean(dataTrain);
		}

	}

}
