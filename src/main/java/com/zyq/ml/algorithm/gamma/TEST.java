package com.zyq.ml.algorithm.gamma;

import java.io.File;
import java.util.List;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import com.zyq.ml.data.QoSDataSetReader;
import com.zyq.ml.entity.QosDataSet;
import com.zyq.ml.entity.QosItem;

/**
 * @author Eamon
 *
 */
public class TEST {
	public static void main(String[] args) throws RserveException {
		RConnection rc = new RConnection();
		File fileList = new File("src/main/resource/sourceData/qosData/");
		for (String s : fileList.list()) {

			QosDataSet dataset = QoSDataSetReader.readFile(s.replaceAll("\\.csv", ""), 30, 3);
			List<QosItem> list = dataset.getTrainSet();
			double test[] = new double[(int) (list.size() * 0.7) + 1];

			int j = 0;
			for (int i = 0; i < list.size(); i++) {
				QosItem e = list.get(i);
				if (e.isTrain()) {
					test[j++] = e.getResp();
				}
			}
			try {
				RScript.callRScript(rc,test, test.length);
			} catch (REXPMismatchException e) {
				e.printStackTrace();
			} catch (REngineException e) {
				e.printStackTrace();
			}

		}

	}

}
