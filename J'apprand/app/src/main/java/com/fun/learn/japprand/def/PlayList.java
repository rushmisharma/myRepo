package com.fun.learn.japprand.def;

import java.io.Serializable;


public class PlayList implements Serializable {

	static final long serialVersionUID = 1L;

	String primaryWord;
	String secondaryWord;
	boolean difficult;
	boolean easy;

	public PlayList() {

	}

	public String getPrimaryWord() {
		return primaryWord;
	}

	public void setPrimaryWord(String primaryWord) {
		this.primaryWord = primaryWord;
	}

	public String getSecondaryWord() {
		return secondaryWord;
	}

	public void setSecondaryWord(String secondaryWord) {
		this.secondaryWord = secondaryWord;
	}

	public boolean isDifficult() {
		return difficult;
	}

	public void setDifficult(boolean difficult) {
		this.difficult = difficult;
	}

	public boolean isEasy() {
		return easy;
	}

	public void setEasy(boolean easy) {
		this.easy = easy;
	}
}