package com.zyq.ml.algorithm.gamma;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import com.zyq.ml.algorithm.KNN.Algorithm;

/**
 * @author Eamon
 *
 */
public class RInvoke {
	
	private RConnection rc;
	
	private REXPGenericVector rexp = null;
	
	private REXPDouble object = null;
	
	public RInvoke() throws RserveException {
		rc = new RConnection();
	}
	
	public void assign(String name,String val) throws RserveException{
		rc.assign(name, val);
	}
	
	public void assign(String name,double[] val) throws REngineException{
		rc.assign(name, val);
	}
	
	public void eval(String source) throws RserveException{
		rc.eval(source);
	}
	
	public double[] optimResult() throws RserveException{
		rexp = (REXPGenericVector) rc.eval("optim(c(2,1),logL,dat=dat)");
		object = (REXPDouble) rexp.asList().firstElement();
		double[] resultDoubles = object.asDoubles();
		return resultDoubles;
	}
	
	public double ksTest(double[] testSet,double[] trainSet) throws REXPMismatchException, REngineException{
		rc.assign("set", testSet);
		// 验证是否是gamma分布
		rexp = (REXPGenericVector) rc.eval("ks.test(rgamma(1000,set[1],set[2]),'pgamma',set[1],set[2])");
		object = (REXPDouble) rexp.asList().elementAt(1);
		double Test = object.asDouble();

		if (isGamma(Test)) {
			object = (REXPDouble) rc.eval("mean(rgamma(10,set[1],set[2]))");
			return object.asDouble();
		} else {
			return Algorithm.mean(trainSet);
		}
	}
	
	/**
	 * 判断是否是gamma函数
	 * 
	 * @param a
	 * @return
	 */
	public boolean isGamma(double a) {
		if (a < 0.05) {
			return false;
		}
		return true;
	}
	
	/**
	 * 调用R语言实现rmse
	 * 
	 * @param @throws
	 */
	public double callRScriptRMSE(double[] preResult, double[] dataOfAll)
			throws REXPMismatchException, REngineException {
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
	public double callRScriptMAE(double[] preResult, double[] dataOfAll)
			throws REXPMismatchException, REngineException {
		rc.assign("x", preResult);
		rc.assign("rx", dataOfAll);
		rc.eval("library(Metrics)");
		REXP rexp = rc.eval("mae(x,rx)");
		return rexp.asDouble();
	}
}
