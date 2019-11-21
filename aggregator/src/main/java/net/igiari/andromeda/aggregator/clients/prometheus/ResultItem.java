package net.igiari.andromeda.aggregator.clients.prometheus;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultItem{

	@SerializedName("metric")
	private Metric metric;

	@SerializedName("values")
	private List<List<Double>> values;

	public void setMetric(Metric metric){
		this.metric = metric;
	}

	public Metric getMetric(){
		return metric;
	}

	public void setValues(List<List<Double>> values){
		this.values = values;
	}

	public List<List<Double>> getValues(){
		return values;
	}

	@Override
 	public String toString(){
		return 
			"ResultItem{" + 
			"metric = '" + metric + '\'' + 
			",values = '" + values + '\'' + 
			"}";
		}
}