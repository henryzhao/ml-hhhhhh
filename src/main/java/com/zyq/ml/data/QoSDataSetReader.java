package com.zyq.ml.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.zyq.ml.entity.QosDataSet;
import com.zyq.ml.entity.QosItem;
import com.zyq.ml.utils.MatrixFileUtils;
import com.zyq.ml.utils.QosFileUtils;

public class QoSDataSetReader {

	public static QosDataSet readFile(String sourceName, int per, int num) {
		QosDataSet dataset = new QosDataSet();
		try {

			int[][] ctrFile = MatrixFileUtils
					.readIntMatrix(Properties.GEN_ROOT + sourceName + "/per" + per + "/" + num + ".txt");
			List<String[]> content = QosFileUtils.readData(Properties.SOURCE_ROOT + "/qosData/" + sourceName + ".csv");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for (int i = 0; i < ctrFile.length; i++) {
				String source[] = content.get(i);

				Date date = sdf.parse(source[0]);
				boolean isError = false;
				if (!source[4].equals("YES"))
					isError = true;
				QosItem e;
				if (ctrFile[i][0] == 0) {
					e = new QosItem(date.getTime(), Integer.parseInt(source[1]), Integer.parseInt(source[2])/1000.0,
							Double.parseDouble(source[3]), isError,false);
					dataset.getVerifySet().add(e);
				}
				else{
					e = new QosItem(date.getTime(), Integer.parseInt(source[1]), Integer.parseInt(source[2])/1000.0,
							Double.parseDouble(source[3]), isError,true);
					
				}
				dataset.getTrainSet().add(e);
			}
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return dataset;
	}

	public static void main(String[] args) {
		QosDataSet dataset = readFile("Amazon1", 30, 3);
		List<QosItem> list = dataset.getTrainSet();
		list.get(0);
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).getTime()+" "+list.get(i).getResp());
		}
		//System.out.println(dataset.getTrainSet());
		
		//System.out.println(dataset.getVerifySet());
	}

}
