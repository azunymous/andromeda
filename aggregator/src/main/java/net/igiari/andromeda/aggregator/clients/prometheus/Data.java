package net.igiari.andromeda.aggregator.clients.prometheus;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data{

	@SerializedName("result")
	private List<ResultItem> result;

	@SerializedName("resultType")
	private String resultType;

	public void setResult(List<ResultItem> result){
		this.result = result;
	}

	public List<ResultItem> getResult(){
		return result;
	}

	public void setResultType(String resultType){
		this.resultType = resultType;
	}

	public String getResultType(){
		return resultType;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"result = '" + result + '\'' + 
			",resultType = '" + resultType + '\'' + 
			"}";
		}
}