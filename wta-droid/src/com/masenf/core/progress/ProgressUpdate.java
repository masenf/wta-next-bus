package com.masenf.core.progress;

public class ProgressUpdate {
	// encapsulates the things that you'd want to update
	public Integer max 		= null;
	public Integer sofar	= null;
	public String  error 	= null;
	public String  label	= null;
	
	public ProgressUpdate(Integer sofar, Integer max, String error) {
		this.sofar = sofar;
		this.max = max;
		this.error = error;
	}
	public ProgressUpdate(Integer sofar, Integer max) {
		this(sofar, max, null);
	}
	public ProgressUpdate(Integer sofar) {
		this(sofar, null, null);
	}
	public ProgressUpdate(String error) {
		this(null,null,error);
	}
	public ProgressUpdate() {
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
