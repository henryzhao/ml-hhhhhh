package com.zyq.ml.algorithm.rarima;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class Arima {

	public static void main(String[] args) throws RserveException, REXPMismatchException  {
		arima();
	}
	public static void arima() throws RserveException, REXPMismatchException{
		RConnection rc = new RConnection();
//		rc.eval("library(forecast);");
		rc.eval("datats<-data(WWWusage);");
//		rc.eval("datats<-ts(airdata,start=1949,frequency=12);");
		rc.eval("arima1<-auto.arima(WWWusage);");
		rc.eval("fcast<-forecast(arima1);");
		REXPGenericVector fs = (REXPGenericVector)rc.eval("summary(fcast);");
		REXPDouble objectDouble = (REXPDouble)fs.asList().firstElement();
		
		double[] forecast = objectDouble.asDoubles();
		for (int i = 0; i < forecast.length; i++) {
			System.out.println(forecast[i]);
		rc.close();
		}
	}
	/*
	 * #拟合ARIMA-GARCH模型
library(tseries)
library(fGarch)
library(FinTS)
a=ts(scan("583.txt"))
ts.plot(a)
fit=lm(a~-1+time(a))
r=resid(fit)
summary(fit)
pacf(r^2)
acf(r)
acf(r^2)
AutocorTest(r)  #残差是否存在序列相关
ArchTest(r)     #是否存在ARCH效应
fit1=garchFit(~arma(2,0)+garch(1,1), data=r, algorithm="nlminb+nm", 
            trace=F, include.mean=F)
summary(fit1)

##拟合ARIMA模型
m1=arima(prop, order = c(2,0,0))
summary(m1)
	 */
	public static void arimagarch() throws RserveException,REXPMismatchException {
		RConnection rConnection = new RConnection();
		rConnection.eval("library(tseries);");
		rConnection.eval("library(fGarch);");
		rConnection.eval("library(FinTS);");
		
		
	}
}
