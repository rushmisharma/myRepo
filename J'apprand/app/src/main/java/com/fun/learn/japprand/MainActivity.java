package com.fun.learn.japprand;

import android.R.drawable;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.fun.learn.japprand.cmn.Constants;
import com.fun.learn.japprand.def.Chapter;
import com.fun.learn.japprand.def.PlayList;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

	Intent intent;
	static Chapter c;
    private SlidingUpPanelLayout slidingLayout ;

	// static int cnt = 0;
	static int tRow = 5;
	static int dCnt = 0;
	static int eCnt = 0;
	static int rCnt = 0;

	static boolean firstTimeInit = false;
	static boolean fromFile = false;
	static boolean playDiff = true;
	static boolean playEasy = true;
	static boolean playReg = true;
	static boolean markDirty = false;
	static boolean partialLoad = false;

	static String dirName = "";
	static String fileName = "temp" + Constants.EXT;

	Map<String, String> topic = new HashMap<String, String>();
	List<String> topicList = new ArrayList<String>();

	ColorStateList oldColors;

	ListPopupWindow wgPopup, rgPopup, srPop;

	AlertDialog alertDialog1;

	PowerManager pm = null;
	PowerManager.WakeLock wl = null;

	// -------------- //
	// ** RECEIVER ** //
	// -------------- //
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(Constants.TAG, "Inside the BroadcastReceiver's on receive method...");
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String action = bundle.getString(Constants.ACTION);
				if (Constants.WAIT_MSG.equalsIgnoreCase(action)) {
					Toast.makeText(MainActivity.this, "Loading. Please wait...", Toast.LENGTH_LONG).show();
				} else if (Constants.HIGHLIGHT.equalsIgnoreCase(action)) {
					// Toast.makeText(MainActivity.this, "Count is: " +
					// c.getCurCnt(), Toast.LENGTH_LONG).show();

					updateTableList();
				}
			}
		}
	};

	// ------------- //
	// ** UI CODE ** //
	// ------------- //
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(Constants.TAG, "*************** J'APPRAND ***************");
        if (isMyServiceRunning(JapprandService.class)) {
			Toast.makeText(this, "The service is already running!", Toast.LENGTH_LONG).show();
			c = Chapter.getChapterInstance();
			partialLoad = true;
		} else {
			Toast.makeText(this, "The service is NOT running!", Toast.LENGTH_LONG).show();
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);

		//Display App Header
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.actionbar_layout, null); //hear set your costume layout
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#41c4f4")));

		setContentView(R.layout.activity_main);
		//set layout slide listener
		slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		//some "demo" event
		slidingLayout.setPanelSlideListener(onSlideListener());

		InitBackgoundTask ibt = new InitBackgoundTask(this);
		ibt.execute();
		intent = new Intent(this, JapprandService.class);
		Toast.makeText(this, "Loading, please wait...", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
		Log.i(Constants.TAG, "Inside the activities on resume method...");
		super.onResume();
		registerReceiver(receiver, new IntentFilter(JapprandService.NOTIFICATION));
	}

	@Override
	protected void onPause() {
		Log.i(Constants.TAG, "Inside the activities on pause method...");
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);

	    // To show icons in the actionbar's overflow menu:
	    // http://stackoverflow.com/questions/18374183/how-to-show-icons-in-overflow-menu-in-actionbar
	    //if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
	        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
	            try{
	                Method m = menu.getClass().getDeclaredMethod(
	                        "setOptionalIconsVisible", Boolean.TYPE);
	                m.setAccessible(true);
	                m.invoke(menu, true);
	            }
	            catch(NoSuchMethodException e){
	                Log.e(Constants.TAG, "onMenuOpened", e);
	            }
	            catch(Exception e){
	                throw new RuntimeException(e);
	            }
	        }
	    //}

	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(Constants.TAG, "Inside the activities on option item selected method...");
		ImageButton btn;
		switch (item.getItemId()) {
		case R.id.mRowGap:
			btn = (ImageButton) findViewById(R.id.btnRowGap);
			btn.performClick();
			return true;
		case R.id.mWordGap:
			btn = (ImageButton) findViewById(R.id.btnWordGap);
			btn.performClick();
			return true;
		case R.id.mSwap:
			btn = (ImageButton) findViewById(R.id.btnSwapPracticing);
			btn.performClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
		return new SlidingUpPanelLayout.PanelSlideListener() {
			@Override
			public void onPanelSlide(View view, float v) {
				Log.i(Constants.TAG, "panel is sliding");
			}

			@Override
			public void onPanelCollapsed(View view) {
				Log.i(Constants.TAG, "panel Collapse");
                TextView tv = (TextView) findViewById(R.id.SliderMsg);
                tv.setText("Click to slide up...");
			}

			@Override
			public void onPanelExpanded(View view) {
				Log.i(Constants.TAG, "panel expand");
                TextView tv = (TextView) findViewById(R.id.SliderMsg);
                tv.setText("Click to slide down...");
			}

			@Override
			public void onPanelAnchored(View view) {
				Log.i(Constants.TAG, "panel anchored");
                TextView tv = (TextView) findViewById(R.id.SliderMsg);
                tv.setText("Click to slide down...");
			}

			@Override
			public void onPanelHidden(View view) {
				Log.i(Constants.TAG, "panel is Hidden");
			}
		};
	}


	private OnItemSelectedListener topicSelected = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView parentView, View selectedItemView, int position, long id) {
			// your code here
			Log.i(Constants.TAG, "Selected Input: " + parentView.getItemAtPosition(position).toString());
			dirName = parentView.getItemAtPosition(position).toString();
			String value = topic.get(parentView.getItemAtPosition(position).toString());
			String[] chapters = value.split(Constants.SEPARATOR);
			Log.i(Constants.TAG, "chapters found: " + chapters.length);
			Spinner sf = (Spinner) findViewById(R.id.lstFile);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(sf.getContext(), R.layout.spinner_text,
					chapters);
			dataAdapter.setDropDownViewResource(R.layout.spinner_text);
			sf.setAdapter(dataAdapter);
			if (partialLoad) {
				sf.setSelection(dataAdapter.getPosition(c.getName()));
			}
			sf.setOnItemSelectedListener(fileSelected);
		}

		@Override
		public void onNothingSelected(AdapterView parentView) {
			// your code here
		}
	};

	private OnItemSelectedListener fileSelected = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView parentView, View selectedItemView, int position, long id) {
			// your code here
			Log.i(Constants.TAG, "Selected Input: " + parentView.getItemAtPosition(position).toString() + position);
			btnStopPracticing(selectedItemView);
			TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
			int count = table.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = table.getChildAt(i);
				if (child instanceof TableRow) {
					((ViewGroup) child).removeAllViews();
				}
			}

			fileName = parentView.getItemAtPosition(position).toString();
			createFromFile(parentView);
			partialLoad = false;
		}

		@Override
		public void onNothingSelected(AdapterView parentView) {

		}
	};

	private OnClickListener onDiffMarkClicked = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Log.i(Constants.TAG, "Inside mark difficult method..");

			if (!markDirty) {
				markDirty = true;
				ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
				btn.setImageResource(R.drawable.save_active);
			}

			TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
			int rowIndex = tl.indexOfChild((View) view.getParent());
			Log.i(Constants.TAG, "Diff rowIndex: " + rowIndex);

			TableRow tr = (TableRow) view.getParent();

			ImageButton d = (ImageButton) tr.getChildAt(0);
			ImageButton e = (ImageButton) tr.getChildAt(1);
			if (!c.getPlayList().get(rowIndex).isDifficult()) {
				c.getPlayList().get(rowIndex).setDifficult(true);
				dCnt++;
				d.setImageResource(R.drawable.diff_checked);
				if (c.getPlayList().get(rowIndex).isEasy()) {
					c.getPlayList().get(rowIndex).setEasy(false);
					eCnt--;
					e.setImageResource(R.drawable.skip_unchecked);
				}
			} else {
				c.getPlayList().get(rowIndex).setDifficult(false);
				dCnt--;
				d.setImageResource(R.drawable.diff_unchecked);
			}
			rCnt = c.getPlayList().size() - (eCnt + dCnt);
			return;
		}
	};

	private OnClickListener onEasySkipClicked = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Log.i(Constants.TAG, "Inside skip easy method..");

			if (!markDirty) {
				markDirty = true;
				ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
				btn.setImageResource(R.drawable.save_active);
			}

			TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
			int rowIndex = tl.indexOfChild((View) view.getParent());
			Log.i(Constants.TAG, "Skip rowIndex: " + rowIndex);

			TableRow tr = (TableRow) view.getParent();

			ImageButton d = (ImageButton) tr.getChildAt(0);
			ImageButton e = (ImageButton) tr.getChildAt(1);
			if (!c.getPlayList().get(rowIndex).isEasy()) {
				c.getPlayList().get(rowIndex).setEasy(true);
				eCnt++;
				e.setImageResource(R.drawable.skip_checked);
				if (c.getPlayList().get(rowIndex).isDifficult()) {
					c.getPlayList().get(rowIndex).setDifficult(false);
					dCnt--;
					d.setImageResource(R.drawable.diff_unchecked);
				}
			} else {
				c.getPlayList().get(rowIndex).setEasy(false);
				eCnt--;
				e.setImageResource(R.drawable.skip_unchecked);
			}
			rCnt = c.getPlayList().size() - (eCnt + dCnt);
			return;
		}
	};

	private OnClickListener onRowSelected = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Log.i(Constants.TAG, "Inside on row selected method...");
			view.setBackgroundResource(drawable.list_selector_background);

			TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
			int rowIndex = tl.indexOfChild(view);
			Log.i(Constants.TAG, "Selected rowIndex: " + rowIndex);
			c.setCurCnt(rowIndex--);
			return;
		}
	};

	public void btnPlayDiff(View view) {
		Log.i(Constants.TAG, "Inside play difficult button method...");

		if (!markDirty) {
			markDirty = true;
			ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
			btn.setImageResource(R.drawable.save_active);
		}

		ImageButton pd = (ImageButton) findViewById(R.id.btnPlayDiffOnly);

		if (playDiff) {
			playDiff = false;
			c.setPlayDiff(false);
			pd.setImageResource(R.drawable.diff_unchecked);
		} else {
			int total = dCnt;
			if (playEasy)
				total = total + eCnt;
			if (playReg)
				total = total + (c.getCurCnt() - (eCnt + dCnt));
			if (total > 0) {
				playDiff = true;
				c.setPlayDiff(true);
				pd.setImageResource(R.drawable.diff_checked);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Selection reslts in nothing to play!").setCancelable(false).setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// do things
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
		return;

	}

	public void btnPlayEasy(View view) {
		Log.i(Constants.TAG, "Inside play easy button method...");

		if (!markDirty) {
			markDirty = true;
			ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
			btn.setImageResource(R.drawable.save_active);
		}

		ImageButton pe = (ImageButton) findViewById(R.id.btnPlayEasyOnly);

		if (playEasy) {
			playEasy = false;
			c.setPlayEasy(false);
			pe.setImageResource(R.drawable.skip_unchecked);
		} else {
			int total = eCnt;
			if (playDiff)
				total = total + dCnt;
			if (playReg)
				total = total + (c.getCurCnt() - (eCnt + dCnt));
			if (total > 0) {
				playEasy = true;
				c.setPlayEasy(true);
				pe.setImageResource(R.drawable.skip_checked);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Selection reslts in nothing to play!").setCancelable(false).setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// do things
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
		return;
	}

	public void btnPlayReg(View view) {
		Log.i(Constants.TAG, "Inside play regular button method...");

		if (!markDirty) {
			markDirty = true;
			ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
			btn.setImageResource(R.drawable.save_active);
		}

		ImageButton pr = (ImageButton) findViewById(R.id.btnPlayRegularOnly);

		if (playReg) {
			playReg = false;
			c.setPlayReg(false);
			pr.setImageResource(R.drawable.regular_unchecked);
		} else {
			int total = c.getPlayList().size() - (eCnt + dCnt);
			if (playEasy)
				total = total + eCnt;
			if (playDiff)
				total = total + dCnt;
			if (total > 0) {
				playReg = true;
				c.setPlayReg(true);
				pr.setImageResource(R.drawable.regular_checked);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Selection reslts in nothing to play!").setCancelable(false).setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// do things
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
		return;
	}

	public void btnGetPracticing(View view) {
		Log.i(Constants.TAG, "Inside btnGetPracticing...");

		ImageButton btn = (ImageButton) findViewById(R.id.btnGetPracticing);
		if (!Constants.PLAYING.equalsIgnoreCase(c.getStatus())) {
			c.setStatus(Constants.PLAYING);
			btn.setImageResource(R.drawable.pause);
			startService(intent);
		} else {
			c.setStatus(Constants.PAUSED);
			btn.setImageResource(R.drawable.play);
		}
		Log.i(Constants.TAG, "Status is set to: " + c.getStatus());
		return;
	}

	public void btnStopPracticing(View view) {
		Log.i(Constants.TAG, "Inside btnStopPracticing...");
		ImageButton btn = (ImageButton) findViewById(R.id.btnGetPracticing);
		btn.setImageResource(R.drawable.play);
		if (partialLoad)
			c.setStatus(Constants.STOPPED);
		partialLoad = false;
		stopService(intent);
        TextView tv = (TextView) findViewById(R.id.TextHighLight);
        tv.setText("");

        TextView tvQ = (TextView) findViewById(R.id.TextQuest);
        tvQ.setText("");

        TextView tvA = (TextView) findViewById(R.id.TextAnsw);
        tvA.setText("");
		return;
	}

	public void btnRepeatPracticing(View view) {
		Log.i(Constants.TAG, "Inside repeat button method...");
		ImageButton btn = (ImageButton) findViewById(R.id.btnRepeatPracticing);
		if (c.isRepeatOn()) {
			c.setRepeatOn(false);
			btn.setImageResource(R.drawable.repeat);
		} else {
			c.setRepeatOn(true);
			btn.setImageResource(R.drawable.repeat_set);
		}
		return;
	}

	public void btnShufflePracticing(View view) {
		Log.i(Constants.TAG, "Inside suffle button method...");
		ImageButton btn = (ImageButton) findViewById(R.id.btnShufflePracticing);
		if (c.isShuffleOn()) {
			c.setShuffleOn(false);
			btn.setImageResource(R.drawable.shuffle);
		} else {
			c.setShuffleOn(true);
			btn.setImageResource(R.drawable.shuffle_set);
		}
		return;
	}

	public void btnRwdPracticing(View view) {
		Log.i(Constants.TAG, "Inside rewind button method...");
		do {
			c.setCurCnt(c.getCurCnt() - 2);
			if (c.getCurCnt() < 0) {
				c.setCurCnt(c.getPlayList().size() + c.getCurCnt());
			}
		} while (c.getPlayList().get(c.getCurCnt()).isEasy());
		return;
	}

	public void btnFwdPracticing(View view) {
		Log.i(Constants.TAG, "Inside forward button method...");
		do {
			c.setCurCnt(c.getCurCnt() + 2);
			if (c.getCurCnt() > c.getPlayList().size()) {
				c.setCurCnt(c.getCurCnt() - c.getPlayList().size());
			}
		} while (c.getPlayList().get(c.getCurCnt()).isEasy());
		return;
	}

	public void btnSavePracticing(View view) {
		Log.i(Constants.TAG, "Inside save button method...");

		ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
		btn.setImageResource(R.drawable.save);
		btn.setEnabled(false);
		markDirty = false;
		Log.i(Constants.TAG, "Creating the file..");
		try {
			File f = new File(this.getFilesDir(),
					Constants.MAINDIR + File.separator + dirName + File.separator + fileName);
			FileOutputStream fileOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(c);
			out.close();
			fileOut.close();
			Log.i(Constants.TAG, "Serialized data is saved in " + Constants.MAINDIR + File.separator + dirName
					+ File.separator + fileName);
		} catch (IOException e) {
			Log.e(Constants.TAG, "IO Exception occured.");
			e.printStackTrace();
		}

		btn.setEnabled(true);
		return;
	}

	@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
	public void btnWordGap(View view) {
		Log.i(Constants.TAG, "Inside word gap button method...");

		if (!markDirty) {
			markDirty = true;
			ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
			btn.setImageResource(R.drawable.save_active);
		}

		wgPopup = new ListPopupWindow(MainActivity.this);
		wgPopup.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.list_item, Constants.delay));
		wgPopup.setAnchorView((ImageButton) findViewById(R.id.btnWordGap));
		wgPopup.setWidth(300);
		wgPopup.setHeight(400);

		wgPopup.setModal(true);
		wgPopup.setOnItemClickListener(wgapVaueSelected);
		wgPopup.show();
	}

	private OnItemClickListener wgapVaueSelected = new OnItemClickListener() {

		@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
			c.setWordGap(Integer.parseInt(parentView.getItemAtPosition(position).toString().split(" ")[0]) * 1000);
			Log.i(Constants.TAG, "Selected WordGap Input: " + c.getWordGap());
			wgPopup.dismiss();
		}
	};

	@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
	public void btnRowGap(View view) {
		Log.i(Constants.TAG, "Inside row gap button method...");

		if (!markDirty) {
			markDirty = true;
			ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
			btn.setImageResource(R.drawable.save_active);
		}

		rgPopup = new ListPopupWindow(MainActivity.this);
		rgPopup.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.list_item, Constants.delay));
		rgPopup.setAnchorView((ImageButton) findViewById(R.id.btnRowGap));
		rgPopup.setWidth(300);
		rgPopup.setHeight(400);

		rgPopup.setModal(true);
		rgPopup.setOnItemClickListener(rgapVaueSelected);
		rgPopup.show();

	}

	private OnItemClickListener rgapVaueSelected = new OnItemClickListener() {

		@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
			c.setRowGap(Integer.parseInt(parentView.getItemAtPosition(position).toString().split(" ")[0]) * 1000);
			Log.i(Constants.TAG, "Selected RowGap Input: " + c.getRowGap());
			rgPopup.dismiss();
		}
	};

	@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
	public void btnSpeechRate(View view) {
		Log.i(Constants.TAG, "Inside speechRate button method...");

		if (!markDirty) {
			markDirty = true;
			ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
			btn.setImageResource(R.drawable.save_active);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		srPop = new ListPopupWindow(MainActivity.this);
		srPop.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.list_item, Constants.speeachRateList));
		srPop.setAnchorView((ImageButton) findViewById(R.id.btnSpeechRate));
		srPop.setWidth(300);
		srPop.setHeight(400);

		srPop.setModal(true);

		builder.setTitle("Select Your Choice");
		CharSequence[] val = { "for Question", "for Answer" };
		builder.setSingleChoiceItems(val, -1, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {

				switch (item) {
				case 0:
					Log.i(Constants.TAG, "First Item Clicked");
					srPop.setOnItemClickListener(primarySpeechRateVaueSelected);
					srPop.show();
					break;

				case 1:
					Log.i(Constants.TAG, "Second Item Clicked");
					srPop.setOnItemClickListener(secondarySpeechRateVaueSelected);
					srPop.show();
					break;
				}
				alertDialog1.dismiss();
			}
		});
		alertDialog1 = builder.create();
		alertDialog1.show();

	}

	private OnItemClickListener primarySpeechRateVaueSelected = new OnItemClickListener() {

		@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
			c.setSpeechRatePrimary(Float.parseFloat(parentView.getItemAtPosition(position).toString()));
			Log.i(Constants.TAG, "Selected Speech Rate Input: " + c.getSpeechRatePrimary());
			srPop.dismiss();
		}
	};

	private OnItemClickListener secondarySpeechRateVaueSelected = new OnItemClickListener() {

		@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
			c.setSpeechRateSecondary(Float.parseFloat(parentView.getItemAtPosition(position).toString()));
			Log.i(Constants.TAG, "Selected Speech Rate Input: " + c.getSpeechRateSecondary());
			srPop.dismiss();
		}
	};

	public void btnSwapPracticing(View view) {
		Log.i(Constants.TAG, "Inside swap practicing...");

		if (!markDirty) {
			markDirty = true;
			ImageButton btn = (ImageButton) findViewById(R.id.btnSavePracticing);
			btn.setImageResource(R.drawable.save_active);
		}

		if (c.isPrimaryFirst()) {
			c.setPrimaryFirst(false);
		} else {
			c.setPrimaryFirst(true);
		}
	}

	public void btnDelPracticing(View view) {
	}

	public void btnTestPracticing(View view) {
	}

	public void btnSettings(View view) {
	}

	// ------------------- //
	// ** LOCAL METHODS ** //
	// ------------------- //
	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void createFromFile(View view) {
		Log.i(Constants.TAG, "Inside create from file (" + fileName + ") method...");
		try {
			fromFile = true;
			ImageButton pd;
			if (!partialLoad) {
				c = Chapter.getChapterInstance();
				File f = new File(view.getContext().getFilesDir(),
						Constants.MAINDIR + File.separator + dirName + File.separator + fileName);
				FileInputStream fileIn = new FileInputStream(f);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				Chapter ct = (Chapter) in.readObject();
				c.setPrimaryLang(ct.getPrimaryLang());
				c.setSecondaryLang(ct.getSecondaryLang());
				c.setCurCnt(ct.getCurCnt());
				c.setPrimaryFirst(ct.isPrimaryFirst());
				c.setPlayDiff(ct.isPlayDiff());
				c.setPlayEasy(ct.isPlayEasy());
				c.setPlayReg(ct.isPlayReg());
				c.setShuffleOn(ct.isShuffleOn());
				c.setRowGap(ct.getRowGap());
				c.setWordGap(ct.getWordGap());
				c.setSpeechRatePrimary(ct.getSpeechRatePrimary());
				c.setSpeechRateSecondary(ct.getSpeechRateSecondary());
				c.setStatus(ct.getStatus());
				c.setRepeatOn(ct.isRepeatOn());
				c.setPlayList(ct.getPlayList());
				c.setDir(ct.getDir());
				c.setName(ct.getName());

				in.close();
				fileIn.close();
			}
			for (int i = 0; i < c.getPlayList().size(); i++) {
				PlayList wl = c.getPlayList().get(i);
				Bundle bundle = new Bundle();
				bundle.putString("word", wl.getPrimaryWord() + "  :  " + wl.getSecondaryWord());
				bundle.putBoolean("diff", wl.isDifficult());
				bundle.putBoolean("skip", wl.isEasy());
				bundle.putInt("rowNum", i);
				createNPopulateRow(bundle);
			}

			playEasy = c.isPlayEasy();
			pd = (ImageButton) findViewById(R.id.btnPlayEasyOnly);
			if (playEasy)
				pd.setImageResource(R.drawable.skip_checked);
			else
				pd.setImageResource(R.drawable.skip_unchecked);

			playDiff = c.isPlayDiff();
			pd = (ImageButton) findViewById(R.id.btnPlayDiffOnly);
			if (playDiff)
				pd.setImageResource(R.drawable.diff_checked);
			else
				pd.setImageResource(R.drawable.diff_unchecked);

			playReg = c.isPlayReg();
			pd = (ImageButton) findViewById(R.id.btnPlayRegularOnly);
			if (playReg)
				pd.setImageResource(R.drawable.regular_checked);
			else
				pd.setImageResource(R.drawable.regular_unchecked);

		} catch (FileNotFoundException e) {
			Log.e(Constants.TAG, "File Not Found Exception.");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(Constants.TAG, "IO Exception occured.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.e(Constants.TAG, "Class Not Found Exception occured.");
			e.printStackTrace();
		}
	}

	public void createNPopulateRow(Bundle bundle) {
		// Log.i(Constants.TAG, "Inside create and populate row handler...");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        /*Display display = getWindowManager().getDefaultDisplay();
        int height=display.getHeight();

        LinearLayout llp = (LinearLayout) findViewById(R.id.selLayout01);
        LinearLayout ll = (LinearLayout) findViewById(R.id.tblLinearLayout0);
        LinearLayout.LayoutParams llLp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                height - calculatePixelsFromDP(llp.getContext(), 100));
        ll.setLayoutParams(llLp);*/


		TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
		TableRow tr = new TableRow(tl.getContext());
		LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
		lp.setMargins(1, 1, 1, 1);
		tr.setLayoutParams(lp);
		tr.setGravity(Gravity.LEFT);
		tr.setClickable(true);
		tr.setBackgroundColor(Color.rgb(127, 126, 126));
		//tr.setPadding(0, 0, 1, 1);
		tr.setOnClickListener(onRowSelected);

		ImageButton btnD = new ImageButton(tl.getContext());
		lp = new TableRow.LayoutParams(calculatePixelsFromDP(tl.getContext(), 47),
				calculatePixelsFromDP(tl.getContext(), 31));
		lp.setMargins(1, 1, 1, 1);
		btnD.setLayoutParams(lp);
		btnD.setClickable(true);
		btnD.setBackgroundColor(Color.WHITE);
		//btnD.setPadding(1, 0, 1, 1);
		if (bundle.getBoolean("diff")) {
			dCnt++;
			btnD.setImageResource(R.drawable.diff_checked);
		} else {
			btnD.setImageResource(R.drawable.diff_unchecked);
		}
		btnD.setOnClickListener(onDiffMarkClicked);
		tr.addView(btnD);

		ImageButton btnE = new ImageButton(tl.getContext());
		lp = new TableRow.LayoutParams(calculatePixelsFromDP(tl.getContext(), 47),
				calculatePixelsFromDP(tl.getContext(), 31));
		lp.setMargins(1, 1, 1, 1);
		btnE.setLayoutParams(lp);
		btnE.setClickable(true);
		//btnE.setPadding(2, 0, 1, 1);
		btnE.setBackgroundColor(Color.WHITE);
		if (bundle.getBoolean("skip")) {
			eCnt++;
			btnE.setImageResource(R.drawable.skip_checked);
		} else {
			btnE.setImageResource(R.drawable.skip_unchecked);
		}
		btnE.setOnClickListener(onEasySkipClicked);
		tr.addView(btnE);

		if (!bundle.getBoolean("diff") && !bundle.getBoolean("skip")) {
			rCnt++;
		}

		TextView tv = new TextView(tl.getContext());
        lp = new TableRow.LayoutParams(width - (calculatePixelsFromDP(tl.getContext(), 47)+
                calculatePixelsFromDP(tl.getContext(), 47)),
                calculatePixelsFromDP(tl.getContext(), 31));
		lp.setMargins(1, 1, 1, 1);
		tv.setLayoutParams(lp);
		tv.setText(bundle.getString("word"));
		tv.setTextSize(13);
		tv.setPadding(2, 0, 1, 1);
		tv.setBackgroundColor(Color.WHITE);
		tr.addView(tv);

		oldColors = tv.getTextColors();

		tl.addView(tr, bundle.getInt("rowNum"));
		return;
	}

	private int calculatePixelsFromDP(Context c, float dp) {
		// Log.i(Constants.TAG, "Inside calculate pixels from dp method...");

		DisplayMetrics metrics = c.getResources().getDisplayMetrics();
		float fpixels = metrics.density * dp;
		int pixels = (int) (fpixels + 0.5f);
		return pixels;
	}

	public void updateTableList() {
		Log.i(Constants.TAG, "Inside update table list...");

		if (c.getCurCnt() < c.getPlayList().size()) {
			performUpdate();
		} else if (c.isRepeatOn()) {
			c.setCurCnt(0);
			performUpdate();
		}
		TextView tv = (TextView) findViewById(R.id.TextHighLight);
		tv.setText(c.getPlayList().get(c.getCurCnt()).getPrimaryWord() + " : "
				+ c.getPlayList().get(c.getCurCnt()).getSecondaryWord());

        TextView tvQ = (TextView) findViewById(R.id.TextQuest);
        tvQ.setText(c.getPlayList().get(c.getCurCnt()).getPrimaryWord());

        TextView tvA = (TextView) findViewById(R.id.TextAnsw);
        tvA.setText(c.getPlayList().get(c.getCurCnt()).getSecondaryWord());
	}

	public void performUpdate() {
		if (!partialLoad) {
			Log.i(Constants.TAG, "Performing update...");
			// Highlight the current word
			TableLayout tblLayout = (TableLayout) findViewById(R.id.tableLayout);
			TableRow rCurr = new TableRow(this);
			int index = c.getCurCnt();
			for (int i = 0; i < c.getPlayList().size(); i++) {
				rCurr = (TableRow) tblLayout.getChildAt(i);
				rCurr.setPadding(1, 0, 1, 1);
				rCurr.setBackgroundColor(Color.rgb(127, 126, 126));
				TextView tv = (TextView) rCurr.getChildAt(2);
				tv.setTypeface(null, Typeface.NORMAL);
				tv.setTextColor(oldColors);
			}
			if (c.isShuffleOn()) {
				index = c.getShuffleIndex().get(c.getCurCnt());
			}
			rCurr = (TableRow) tblLayout.getChildAt(index);
			rCurr.setBackgroundColor(Color.rgb(150, 223, 242));
			TextView tv = (TextView) rCurr.getChildAt(2);
			tv.setTypeface(null, Typeface.BOLD);
			tv.setTextColor(Color.rgb(31, 171, 242));

			if (c.isShuffleOn()) {
				int temp = index;
				temp = temp - 2;
				if (temp > c.getPlayList().size()) {
					temp = temp - c.getPlayList().size();
				}
				if (temp < 0) {
					temp = c.getPlayList().size() + temp;
				}
				if (temp == 0) {
					temp = c.getPlayList().size() - 1;
				}
				final ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
				TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
				final View child = tl.getChildAt(temp);
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						Log.i(Constants.TAG, "Inside the scroll handler...");
						sv.smoothScrollTo(0, child.getBottom());
					}
				});
			} else {
				// Get the 5 words to display
				if (c.getCurCnt() % tRow == 0) {
					int temp = c.getCurCnt() - 1;

					if (temp > c.getPlayList().size()) {
						temp = temp - c.getPlayList().size();
					} else if (temp < 0) {
						temp = c.getPlayList().size() + temp;
					} else if (temp == 0) {
						temp = c.getPlayList().size();
					}
					final ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
					TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
					final View child = tl.getChildAt(temp);
					final int pos = c.getCurCnt();
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							Log.i(Constants.TAG, "Inside the scroll handler...");
							if (pos != 0)
								sv.smoothScrollTo(0, child.getBottom());
						}
					});
				}
			}
		}
	}

	// -------------- //
	// ** HANDLERS ** //
	// -------------- //
	Handler updateTopicList = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i(Constants.TAG, "Inside update topic list handler...: " + topicList);
			Bundle bundle = msg.getData();
			Spinner st = (Spinner) findViewById(R.id.lstTopic);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(st.getContext(), R.layout.spinner_text,
					(List<String>) topicList);
			dataAdapter.setDropDownViewResource(R.layout.spinner_text);
			st.setAdapter(dataAdapter);
			if (partialLoad) {
				st.setSelection(dataAdapter.getPosition(c.getDir()));
			}
			st.setOnItemSelectedListener(topicSelected);
		}
	};

	// ---------------- //
	// ** ASYNC TASK ** //
	// ---------------- //

	private class InitBackgoundTask extends AsyncTask<String, String, String> {

		MainActivity aActivity;

		InitBackgoundTask(MainActivity activity) {
			this.aActivity = activity;
		};

		@Override
		protected String doInBackground(String... params) {
			Log.i(Constants.TAG, "Inside init background task..");

			getTopicList();
			getFileList();
			updateTopicList.sendEmptyMessage(0);
			pm = (PowerManager) getSystemService(aActivity.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
			return null;
		}

		// Check if the txt file exists
		public boolean fileExistance(String fname) {
			File file = getBaseContext().getFileStreamPath(fname);
			return file.exists();
		}

		public void getTopicList() {
			Log.i(Constants.TAG, "Inside get topic list method...");
			File f = new File(aActivity.getFilesDir(), Constants.MAINDIR);
			File t = null;
			if (fileExistance(Constants.MAINDIR)) {
				String[] fileList = f.list();
				for (int i = 0; i < fileList.length; i++) {
					Log.i(Constants.TAG, "Got topic dir list: " + fileList[i]);
					t = new File(aActivity.getFilesDir(), Constants.MAINDIR + File.separator + fileList[i]);
					if (t.isDirectory()) {
						topic.put(fileList[i], null);
						topicList.add(fileList[i]);
					}
				}
			} else {
				firstTimeInit = true;
				f.mkdir();

				String path = Constants.MAINDIR + File.separator + "french" + File.separator;
				f = new File(aActivity.getFilesDir(), path);
				f.mkdirs();
				topic.put("french", "french0to100" + Constants.SEPARATOR + "french100to200" + Constants.SEPARATOR
						+ "french200to300" + Constants.SEPARATOR + "FrenchWords0" + Constants.SEPARATOR + "FrenchWords1"
						+ Constants.SEPARATOR + "FrenchWords2" + Constants.SEPARATOR + "FrenchWords3" + Constants.SEPARATOR + "FrenchWords4");
				topicList.add("french");

				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "french0to100.ser",
						f.getAbsolutePath() + File.separator + "french0to100.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "french100to200.ser",
						f.getAbsolutePath() + File.separator + "french100to200.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "french200to300.ser",
						f.getAbsolutePath() + File.separator + "french200to300.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "FrenchWords0.ser",
						f.getAbsolutePath() + File.separator + "FrenchWords0.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "FrenchWords1.ser",
						f.getAbsolutePath() + File.separator + "FrenchWords1.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "FrenchWords2.ser",
						f.getAbsolutePath() + File.separator + "FrenchWords2.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "FrenchWords3.ser",
						f.getAbsolutePath() + File.separator + "FrenchWords3.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "FrenchWords4.ser",
						f.getAbsolutePath() + File.separator + "FrenchWords4.ser");

				path = Constants.MAINDIR + File.separator + "naturalization" + File.separator;
				f = new File(aActivity.getFilesDir(), path);
				f.mkdirs();
				topic.put("naturalization", "interviewQuestion");
				topicList.add("naturalization");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "interviewQuestion.ser",
						f.getAbsolutePath() + File.separator + "interviewQuestion.ser");

				path = Constants.MAINDIR + File.separator + "hindi" + File.separator;
				f = new File(aActivity.getFilesDir(), path);
				f.mkdirs();
				topic.put("hindi",
						"hindi0to100.ser" + Constants.SEPARATOR + "hindi0to20.ser" + Constants.SEPARATOR
								+ "hindi20to40.ser" + Constants.SEPARATOR + "hindi40to60.ser" + Constants.SEPARATOR
								+ "hindi60to80.ser" + Constants.SEPARATOR + "hindi80to100.ser");
				topicList.add("hindi");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "hindi0to100.ser",
						f.getAbsolutePath() + File.separator + "hindi0to100.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "hindi0to20.ser",
						f.getAbsolutePath() + File.separator + "hindi0to20.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "hindi20to40.ser",
						f.getAbsolutePath() + File.separator + "hindi20to40.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "hindi40to60.ser",
						f.getAbsolutePath() + File.separator + "hindi40to60.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "hindi60to80.ser",
						f.getAbsolutePath() + File.separator + "hindi60to80.ser");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "hindi80to100.ser",
						f.getAbsolutePath() + File.separator + "hindi80to100.ser");

				path = Constants.MAINDIR + File.separator + "warriors" + File.separator;
				f = new File(aActivity.getFilesDir(), path);
				f.mkdirs();
				topic.put("warriors", "warriors.ser");
				topicList.add("warriors");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "warriors.ser",
						f.getAbsolutePath() + File.separator + "warriors.ser");

				path = Constants.MAINDIR + File.separator + "sharks" + File.separator;
				f = new File(aActivity.getFilesDir(), path);
				f.mkdirs();
				topic.put("sharks", "sharks.ser");
				topicList.add("sharks");
				copyFromAssetsToStorage(aActivity.getApplicationContext(), path + "sharks.ser",
						f.getAbsolutePath() + File.separator + "sharks.ser");
			}
		}

		private void copyFromAssetsToStorage(Context Context, String SourceFile, String DestinationFile) {
			Log.i(Constants.TAG, "Inside copy from assets to storage method...");
			InputStream IS;
			try {
				IS = Context.getAssets().open(SourceFile);
				OutputStream OS = new FileOutputStream(DestinationFile);
				copyStream(IS, OS);
				OS.flush();
				OS.close();
				IS.close();
			} catch (IOException e) {
				Log.e(Constants.TAG, "An IO Exception occured inside copy from assets to storage method...");
				e.printStackTrace();
			}
		}

		private void copyStream(InputStream Input, OutputStream Output) {
			Log.i(Constants.TAG, "Inside copy stream method...");
			byte[] buffer = new byte[5120];
			int length;
			try {
				length = Input.read(buffer);
				while (length > 0) {
					Output.write(buffer, 0, length);
					length = Input.read(buffer);
				}
			} catch (IOException e) {
				Log.e(Constants.TAG, "An IO Exception occured inside copy stream method...");
				e.printStackTrace();
			}
		}

		public void getFileList() {
			Log.i(Constants.TAG, "Inside get file list method...");
			if (!firstTimeInit) {
				for (Map.Entry<String, String> entry : topic.entrySet()) {
					String t = entry.getKey();
					File f = new File(aActivity.getFilesDir(), Constants.MAINDIR + File.separator + t);
					String fileNames = "";
					String[] files = f.list();
					for (int i = 0; i < files.length; i++) {
						fileNames = fileNames + files[i] + Constants.SEPARATOR;
					}
					if (!fileNames.isEmpty())
						fileNames = fileNames.substring(0, fileNames.length() - 1);
					Log.i(Constants.TAG, "File list is: " + fileNames);
					topic.put(t, fileNames);
				}
			}
		}
	}
}
