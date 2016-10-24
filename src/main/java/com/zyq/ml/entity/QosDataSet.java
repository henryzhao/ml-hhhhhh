package com.zyq.ml.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QosDataSet {
	private String name;
	
	private List<QosItem> trainSet = new ArrayList<>();
	
	private List<QosItem> verifySet = new ArrayList<>();
	
	
}
