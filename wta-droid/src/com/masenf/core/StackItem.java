package com.masenf.core;

import java.io.Serializable;

public class StackItem implements Serializable {
	private static final long serialVersionUID = -7116711663472846064L;
	String ltag;			// the level tag, identifies this level
	int list_pos;			// the FirstSelectedIndex
	public StackItem (String ltag, int list_pos) {
		this.ltag = ltag;
		this.list_pos = list_pos;
	}
	public String getLtag() {
		return ltag;
	}
	public void setLtag(String ltag) {
		this.ltag = ltag;
	}
	public int getListPos() {
		return list_pos;
	}
	public void setListPos(int list_pos) {
		this.list_pos = list_pos;
	}
}