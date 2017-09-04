package com.fun.learn.japprand.def;

import java.io.Serializable;
import java.util.List;

import com.fun.learn.japprand.cmn.Constants;

public class Chapter implements Serializable {

	static final long serialVersionUID = 1L;

	private static Chapter c = new Chapter();

	private String name;
	private String dir;
	private String primaryLang;
	private String secondaryLang;
	private String status = Constants.STOPPED;

	private List<PlayList> playList;
	private List<Integer> shuffleIndex;

	private int wordGap;
	private int rowGap;
	private int curCnt;

	private float speechRatePrimary;
	private float speechRateSecondary;

	private boolean primaryFirst;
	private boolean playEasy;
	private boolean playDiff;
	private boolean playReg;
	private boolean shuffleOn;
	private boolean repeatOn;

	private Chapter() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public static Chapter getChapterInstance() {
		return c;
	}

	public float getSpeechRatePrimary() {
		return speechRatePrimary;
	}

	public void setSpeechRatePrimary(float speechRatePrimary) {
		this.speechRatePrimary = speechRatePrimary;
	}

	public float getSpeechRateSecondary() {
		return speechRateSecondary;
	}

	public void setSpeechRateSecondary(float speechRateSecondary) {
		this.speechRateSecondary = speechRateSecondary;
	}

	public int getWordGap() {
		return wordGap;
	}

	public void setWordGap(int wordGap) {
		this.wordGap = wordGap;
	}

	public int getRowGap() {
		return rowGap;
	}

	public void setRowGap(int rowGap) {
		this.rowGap = rowGap;
	}

	public int getCurCnt() {
		return curCnt;
	}

	public void setCurCnt(int curCnt) {
		this.curCnt = curCnt;
	}

	public boolean isPlayEasy() {
		return playEasy;
	}

	public void setPlayEasy(boolean playEasy) {
		this.playEasy = playEasy;
	}

	public boolean isPlayDiff() {
		return playDiff;
	}

	public void setPlayDiff(boolean playDiff) {
		this.playDiff = playDiff;
	}

	public boolean isPlayReg() {
		return playReg;
	}

	public void setPlayReg(boolean playReg) {
		this.playReg = playReg;
	}

	public boolean isPrimaryFirst() {
		return primaryFirst;
	}

	public void setPrimaryFirst(boolean primaryFirst) {
		this.primaryFirst = primaryFirst;
	}

	public String getPrimaryLang() {
		return primaryLang;
	}

	public void setPrimaryLang(String primaryLang) {
		this.primaryLang = primaryLang;
	}

	public String getSecondaryLang() {
		return secondaryLang;
	}

	public void setSecondaryLang(String secondaryLang) {
		this.secondaryLang = secondaryLang;
	}

	public List<PlayList> getPlayList() {
		return playList;
	}

	public void setPlayList(List<PlayList> playList) {
		this.playList = playList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isShuffleOn() {
		return shuffleOn;
	}

	public void setShuffleOn(boolean shuffleOn) {
		this.shuffleOn = shuffleOn;
	}

	public List<Integer> getShuffleIndex() {
		return shuffleIndex;
	}

	public void setShuffleIndex(List<Integer> shuffleIndex) {
		this.shuffleIndex = shuffleIndex;
	}

	public boolean isRepeatOn() {
		return repeatOn;
	}

	public void setRepeatOn(boolean repeatOn) {
		this.repeatOn = repeatOn;
	}

}