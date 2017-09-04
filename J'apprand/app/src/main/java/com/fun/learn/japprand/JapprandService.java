package com.fun.learn.japprand;

import java.util.Collections;
import java.util.Locale;

import com.fun.learn.japprand.cmn.Constants;
import com.fun.learn.japprand.def.Chapter;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class JapprandService extends IntentService {

	TextToSpeech ttsPrimary = null;
	TextToSpeech ttsSecondary = null;

	int ttsInstCnt = 0;

	boolean donePrimary = true;
	boolean doneSecondary = true;

	public static final String NOTIFICATION = "com.fun.learn.japprand";

	// int cnt = 0;

	Chapter c;

	Bundle ttsParm;

	public JapprandService() {
		super("JapprandService");
		Log.i(Constants.TAG, "Inside JapprandService Constructor...");

		//ttsPrimary = new TextToSpeech(this, new ttsInitListener());

		//ttsSecondary = new TextToSpeech(this, new ttsInitListener());
	}


	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onHandleIntent(Intent i) {
		Log.i(Constants.TAG, "Inside services' onHandleIntent...");

		c = Chapter.getChapterInstance();

		if (Constants.PLAYING.equalsIgnoreCase(c.getStatus())) {
			publish(Constants.WAIT_MSG);
			init();
			play();
		}
	}

	private void publish(String msg) {
		Log.i(Constants.TAG, "Inside services' publish...");
		Intent intent = new Intent(NOTIFICATION);
		if (Constants.WAIT_MSG.equalsIgnoreCase(msg)) {
			intent.putExtra(Constants.ACTION, msg);
		} else if (Constants.HIGHLIGHT.equalsIgnoreCase(msg)) {
			intent.putExtra(Constants.ACTION, msg);
		}
		sendBroadcast(intent);
	}


	@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	private void init() {
		Log.i(Constants.TAG, "Inside services' init...");
		ttsPrimary = new TextToSpeech(this, new ttsInitListener());

		ttsSecondary = new TextToSpeech(this, new ttsInitListener());

		while (ttsInstCnt != 2) {
			try {
				Log.i(Constants.TAG, "Waiting for tts to get initialized.. ");
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Log.e(Constants.TAG, "Interrupted Exception");
				e.printStackTrace();
			}
		}
		Log.i(Constants.TAG, "Woke up");

		ttsPrimary.setSpeechRate(c.getSpeechRatePrimary());
		ttsSecondary.setSpeechRate(c.getSpeechRateSecondary());

		ttsPrimary.setOnUtteranceProgressListener(new ttsUtteranceListener());
		ttsSecondary.setOnUtteranceProgressListener(new ttsUtteranceListener());

		if (c.getPrimaryLang().equalsIgnoreCase("french")) {
			ttsPrimary.setLanguage(Locale.CANADA_FRENCH);
		} else if (c.getPrimaryLang().equalsIgnoreCase("hindi")) {
			ttsPrimary.setLanguage(new Locale("hi"));
		} else if (c.getPrimaryLang().equalsIgnoreCase("english")) {
			ttsPrimary.setLanguage(Locale.US);
		}

		if (c.getSecondaryLang().equalsIgnoreCase("french")) {
			ttsSecondary.setLanguage(Locale.CANADA_FRENCH);
		} else if (c.getSecondaryLang().equalsIgnoreCase("hindi")) {
			ttsSecondary.setLanguage(new Locale("hi"));
		} else if (c.getSecondaryLang().equalsIgnoreCase("english")) {
			ttsSecondary.setLanguage(Locale.US);
		}
		return;
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void play() {
		Log.i(Constants.TAG, "Inside services' play...");

		ttsPrimary.setSpeechRate(c.getSpeechRatePrimary());
		ttsSecondary.setSpeechRate(c.getSpeechRateSecondary());

		if (c.getCurCnt() >= c.getPlayList().size()) {
			if (c.isRepeatOn()) {
				c.setCurCnt(0);
			} else {
				c.setStatus(Constants.STOPPED);
			}
		}
		if (Constants.STOPPED.equalsIgnoreCase(c.getStatus())) {
			return;
		}
		while (Constants.PAUSED.equalsIgnoreCase(c.getStatus())) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (Constants.PLAYING.equalsIgnoreCase(c.getStatus())) {
			publish(Constants.HIGHLIGHT);
			int index = c.getCurCnt();
			if (c.isShuffleOn()) {
				index = c.getShuffleIndex().get(c.getCurCnt());
				if (c.getCurCnt() == c.getPlayList().size() - 1) {
					Collections.shuffle(c.getShuffleIndex());
				}
			}
			boolean result = false;
			if (c.getPlayList().get(index).isEasy() && c.isPlayEasy()) {
				result = true;
			}
			if (c.getPlayList().get(index).isDifficult() && c.isPlayDiff()) {
				result = true;
			}
			if (!c.getPlayList().get(index).isEasy() && !c.getPlayList().get(index).isDifficult() && c.isPlayReg()) {
				result = true;
			}

			if (result) {
				Log.i(Constants.TAG, "Current input: " + c.getPlayList().get(index).getPrimaryWord() + ", "
						+ c.getPlayList().get(index).getSecondaryWord());
				speaker(index);
			}
		}
	}


	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void speaker(int index) {
		Log.i(Constants.TAG, "Inside services' speaker...");
		if (c.isPrimaryFirst()) {
			Log.i(Constants.TAG, "Speaking primary first...");
			if (c.getStatus().equalsIgnoreCase(Constants.PLAYING) && doneSecondary) {
				donePrimary = false;
				doneSecondary = false;
				ttsParm = new Bundle();
				ttsParm.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, Constants.PRIDONE);
				ttsPrimary.speak(extractSpeakable(c.getPlayList().get(index).getPrimaryWord()),
						TextToSpeech.QUEUE_FLUSH, ttsParm, Constants.PRIDONE);
				try {
					// Log.i(Constants.TAG, "Sleeping");
					Thread.sleep(c.getWordGap());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (ttsPrimary.isSpeaking() || !donePrimary) {
					try {
						// Log.i(Constants.TAG, "Sleeping");
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (c.getStatus().equalsIgnoreCase(Constants.PLAYING) && donePrimary) {
				doneSecondary = false;
				ttsParm = new Bundle();
				ttsParm.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, Constants.SECDONE);
				ttsSecondary.speak(extractSpeakable(c.getPlayList().get(index).getSecondaryWord()),
						TextToSpeech.QUEUE_FLUSH, ttsParm, Constants.SECDONE);

				try {
					// Log.i(Constants.TAG, "Sleeping");
					Thread.sleep(c.getRowGap());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				while (ttsSecondary.isSpeaking() || !doneSecondary) {
					try {
						// Log.i(Constants.TAG, "Sleeping.");
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Log.i(Constants.TAG, "Speaking secondary first...");
			if (c.getStatus().equalsIgnoreCase(Constants.PLAYING) && donePrimary) {
				doneSecondary = false;
				donePrimary = false;
				ttsParm = new Bundle();
				ttsParm.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, Constants.SECDONE);
				ttsSecondary.speak(extractSpeakable(c.getPlayList().get(index).getSecondaryWord()),
						TextToSpeech.QUEUE_FLUSH, ttsParm, Constants.SECDONE);
				try {
					// Log.i(Constants.TAG, "Sleeping...");
					Thread.sleep(c.getWordGap());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (ttsSecondary.isSpeaking() || !doneSecondary) {
					try {
						// Log.i(Constants.TAG, "Sleeping..");
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (c.getStatus().equalsIgnoreCase(Constants.PLAYING) && doneSecondary) {
				donePrimary = false;
				ttsParm = new Bundle();
				ttsParm.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, Constants.PRIDONE);
				ttsPrimary.speak(extractSpeakable(c.getPlayList().get(index).getPrimaryWord()),
						TextToSpeech.QUEUE_FLUSH, ttsParm, Constants.PRIDONE);
				try {
					// Log.i(Constants.TAG, "Sleeping");
					Thread.sleep(c.getRowGap());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (ttsPrimary.isSpeaking() || !donePrimary) {
					try {
						// Log.i(Constants.TAG, "Sleeping");
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (!donePrimary || !doneSecondary) {
			publish(Constants.WAIT_MSG);
		}
		while (!donePrimary || !doneSecondary) {
			try {
				Log.i(Constants.TAG, "Waiting for speaking to finish before the next set...");
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (donePrimary && doneSecondary)
			c.setCurCnt(c.getCurCnt() + 1);
		play();
	}

	private String extractSpeakable(String word) {
		Log.i(Constants.TAG, "Inside the extract speakable method...");
		String speak = "";
		int i = 0;
		while (i < word.length()) {
			if (word.charAt(i) == Constants.splitChar1) {
				i++;
				while (word.charAt(i) != Constants.splitChar2) {
					i++;
				}
			}
			speak += word.charAt(i);
			i++;
		}
		return speak;
	}

	class ttsInitListener implements OnInitListener {

		@Override
		public void onInit(int status) {
			if (status != TextToSpeech.ERROR) {
				ttsInstCnt++;
			}
		}
	}


	@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	class ttsUtteranceListener extends UtteranceProgressListener {

		@Override
		public void onDone(String utteranceId) {
			Log.i(Constants.TAG, "Inside the done method on utterance progress listener.");
			if (Constants.PRIDONE.equalsIgnoreCase(utteranceId)) {
				Log.i(Constants.TAG, "Setting primary to done.");
				donePrimary = true;
			} else if (Constants.SECDONE.equalsIgnoreCase(utteranceId)) {
				Log.i(Constants.TAG, "Setting secondary to done.");
				doneSecondary = true;
			}

		}

		@Override
		public void onError(String utteranceId) {
		}

		@Override
		public void onStart(String utteranceId) {
		}
	}

}
