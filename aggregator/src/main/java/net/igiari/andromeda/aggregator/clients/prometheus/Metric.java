package net.igiari.andromeda.aggregator.clients.prometheus;

import com.google.gson.annotations.SerializedName;

public class Metric{

	@SerializedName("instance")
	private String instance;

	@SerializedName("__name__")
	private String name;

	@SerializedName("dependencyName")
	private String dependencyName;

	public void setInstance(String instance){
		this.instance = instance;
	}

	public String getInstance(){
		return instance;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDependencyName(String dependencyName){
		this.dependencyName = dependencyName;
	}

	public String getDependencyName(){
		return dependencyName;
	}

	@Override
 	public String toString(){
		return 
			"Metric{" + 
			"instance = '" + instance + '\'' + 
			",__name__ = '" + name + '\'' + 
			",dependencyName = '" + dependencyName + '\'' +
			"}";
		}
}