package com.masenf.wtaandroid;

import java.io.Serializable;

public class StackItem implements Serializable {
	private static final long serialVersionUID = -7116711663472846064L;
	String ltag;			// the level tag, identifies this level
	int list_pos;			// the FirstSelectedIndex
	public StackItem (String ltag, int list_pos) {
		this.ltag = ltag;
		this.list_pos = list_pos;
	}
}