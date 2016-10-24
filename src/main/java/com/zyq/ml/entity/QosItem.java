package com.zyq.ml.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QosItem {
	private long time;

	private int size;
	
	private double resp;
	
	private double th;
	
	private boolean error;
	
	private boolean isTrain;

}
