package com.test.androidrestconsumer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.test.androidrestconsumer.search.Constant;
import com.test.androidrestconsumer.search.DistanceMatrixResponse;
import com.test.androidrestconsumer.search.SalsaEventModel;
import com.test.androidrestconsumer.search.SalsaEventModelFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class MainActivity extends Activity {
	SalsaEventModel sem = new SalsaEventModel();
	Constant cnst = new Constant();
	private List<SalsaEventModel> eventList = new ArrayList<SalsaEventModel>();
	private List<SalsaEventModel> selSorEventList = new ArrayList<SalsaEventModel>();
	private SalsaEventModelFactory semf = new SalsaEventModelFactory();

	private static final String BASE = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial";
	private static final String KEY = "&key=AIzaSyAbgxhRnV3CZ9cfRddXP_V0bl9WY5Q-7j0";

	EditText txtRegion = null;
	Spinner txtEventType = null;
	EditText txtEventDate = null;
	ListView lstEvent = null;

	ListView lstTest;
	AdapterSEM arrayAdapter;
	SalsaEventModel[] sems = null;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		txtEventDate = (EditText) findViewById(R.id.txtEventDate);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		txtEventDate.setText(dateFormat.format(date));

		try {

			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			long GPSLocationTime = 0;
			if (null != locationGPS) {
				GPSLocationTime = locationGPS.getTime();
			}

			long NetLocationTime = 0;

			if (null != locationNet) {
				NetLocationTime = locationNet.getTime();
			}
			txtRegion = (EditText) findViewById(R.id.txtRegion);

			// Default value setting
			Location loc = null;
			if (0 < GPSLocationTime - NetLocationTime)
				loc = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
			else
				loc = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
		/*----------to get City-Name from coordinates ------------- */
			String cityName = null, stateName = null;
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				if (addresses.size() > 0)
					System.out.println(addresses.get(0).getLocality());
				cityName = addresses.get(0).getLocality();
				stateName = addresses.get(0).getAdminArea();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String s = cityName + ", " + stateName;
			txtRegion.setText(s);

			MyLocationListener locationListener = new MyLocationListener();

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,
					(LocationListener) locationListener);
		}
		catch(SecurityException e){
            Toast.makeText(this, "Got Permission Exception", Toast.LENGTH_SHORT);
        }
	}

	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {

			txtRegion.setText("");
			String longitude = "Longitude: " + loc.getLongitude();
			String latitude = "Latitude: " + loc.getLatitude();

			/*----------to get City-Name from coordinates ------------- */
			String cityName = null, stateName = null;
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				if (addresses.size() > 0)
					System.out.println(addresses.get(0).getLocality());
				cityName = addresses.get(0).getLocality();
				stateName = addresses.get(0).getAdminArea();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String s = cityName + ", " + stateName;
			txtRegion.setText(s);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class UpdateTask extends AsyncTask<String, String, String> {
		boolean done = false;


		ProgressDialog testdialog;
		
		UpdateTask(MainActivity activity){
			testdialog = new ProgressDialog(activity);
		}

		public boolean isDone() {
			return done;
		}

		protected String doInBackground(String... urls) {



				System.out.println("Got the android version as:: " + android.os.Build.VERSION.SDK_INT);

				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				String temp = dwnldPageContent("https://www.salsabythebay.com/salsa-events-calendar/",
						Integer.toString(month).concat(Integer.toString(year)));
				System.out.println("Received: " + temp);
				setEvents(temp);

				done = true;

			return null;
		}

		public boolean fileExistance(String fname) {
			File file = getBaseContext().getFileStreamPath(fname);
			return file.exists();
		}

		public void setEvents(String pageContent) {

			System.out.println("Downloaded page content: " + pageContent);
			Document document = Jsoup.parse(pageContent);
			cnst.setEventMap(retrieveEventList(document, "calendar2"));
			System.out.println("Got Date Event Map: " + cnst.getEventMap());

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();
			try {
				date = formatter.parse(txtEventDate.getText().toString());
			} catch (ParseException e) {
				System.out.println("Please check the date.");
				e.printStackTrace();
			}

			Iterator<String> itr ;
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			System.out.println("Got it " + dayOfWeek);
			switch (dayOfWeek) {
			case 2:
				itr = cnst.getEventMap().get("Mon " + date.getDate()).iterator();
				break;
			case 3:
				itr = cnst.getEventMap().get("Tue " + date.getDate()).iterator();
				break;

			case 5:
				itr = cnst.getEventMap().get("Thu " + date.getDate()).iterator();
				break;
			case 6:
				itr = cnst.getEventMap().get("Fri " + date.getDate()).iterator();
				break;
			case 7:
				itr = cnst.getEventMap().get("Sat " + date.getDate()).iterator();
				break;
			case 1:
				itr = cnst.getEventMap().get("Sun " + date.getDate()).iterator();
				break;
			// Default to initialize to not null value
			case 4:
			default:
				itr = cnst.getEventMap().get("Wed " + date.getDate()).iterator();
				break;
			}

			while (itr.hasNext()) {
				String urlNTypeRec = itr.next();
				System.out.println("Received: " + urlNTypeRec);
				String[] urlNType = urlNTypeRec.split(";");
				System.out.println("The URL to use: " + urlNType[0] +
				 " and eType as " +urlNType[1]);

				pageContent = dwnldPageContent(urlNType[0], null);
				Document doc = Jsoup.parse(pageContent);
				eventList.add(processEvent(doc, semf, urlNType[1]));

				// publishProgress("Please wait...");
				// testdialog.show();
			}

			// System.out.println("The Salsa Event Model List : " + eventList);
		}

		/* Downloads the page content of the specified URL */
		public String dwnldPageContent(String urlStr, String date) {
			URL url;
			String result = "";
			// System.out.println("Downloading the event pages" + urlStr);
			try {
				String cnvrtNm = urlStr.replace("/", "_");
				if (date != null && !date.isEmpty()) {
					cnvrtNm = cnvrtNm.concat(date);
				}
				if (fileExistance(cnvrtNm)) {
					System.out.println("File exists in internal storage");
					try {
						InputStream inputStream = openFileInput(cnvrtNm);

						if (inputStream != null) {
							InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
							BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
							String receiveString = "";
							StringBuilder stringBuilder = new StringBuilder();

							while ((receiveString = bufferedReader.readLine()) != null) {
								stringBuilder.append(receiveString);
							}

							inputStream.close();
							result = stringBuilder.toString();
						}
					} catch (FileNotFoundException e) {
						System.out.println("File not found: " + e.toString());
					} catch (IOException e) {
						System.out.println("Can not read file: " + e.toString());
					}
				} else {
					System.out.println("File does NOT exists in internal storage");
					url = new URL(urlStr);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("Accept", "application/xml");
					InputStream is = con.getInputStream();

					Writer writer = new StringWriter();
					Reader reader = null;
					char[] buffer = new char[1024];
					try {
						reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
						int n;
						while ((n = reader.read(buffer)) != -1) {
							writer.write(buffer, 0, n);
						}
					} finally {
						is.close();
					}
					result = writer.toString();
					System.out.println("Saving file in internal storage");
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
							openFileOutput(cnvrtNm, Context.MODE_PRIVATE));
					outputStreamWriter.write(result);
					outputStreamWriter.close();

					/*
					 * System.out.println("Received Data for Link: " +
					 * writer.toString());
					 */
					reader.close();
					writer.close();
					is.close();

				}

			} catch (MalformedURLException e) {
				System.out.println("Please check the URL." + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Connection or Reading Issue." + e.getMessage());
				e.printStackTrace();
			}
			return result;
		}

		/* Retrieves the site list of all Salsa events of the day. */
		public Map<String, List<String>> retrieveEventList(Document doc, String classNm) {
			Map<String, List<String>> dateEvent = new HashMap<String, List<String>>();
			List<String> eventList = new ArrayList<String>();
			Elements els = doc.getElementsByClass(classNm);
			Iterator<Element> itr = els.iterator();

			System.out.println("There is something in the list : " + itr.hasNext());

			Calendar c = Calendar.getInstance();
			int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
			int cnt = 0;
			while (itr.hasNext()) {
				Element el = (Element) itr.next();
				Elements tels = el.getElementsByTag("td");
				Iterator<Element> titr = tels.iterator();
				while (titr.hasNext() && cnt < monthMaxDays) {
					String date = "";
					Element tel = titr.next();
					Elements pels = tel.getElementsByTag("p");
					Iterator<Element> pitr = pels.iterator();
					while (pitr.hasNext()) {
						date = ((Element) pitr.next()).text();
					}
					Elements iels = tel.getElementsByTag("a");
					Iterator<Element> parser = iels.iterator();
					eventList = new ArrayList<String>();
					while (parser.hasNext()) {
						Element opt = (Element) parser.next();
						String link = opt.attr("href");
						String eType = opt.className();
						eventList.add(link + ";" + eType);
					}
					System.out.println("Found href" + eventList);
					System.out.println("Found eType" + eventList);
                    if(date!=null && !date.isEmpty()) {
                        dateEvent.put(date, eventList);
                        System.out.println("Adding to map "+ date + " " + eventList);
                        System.out.println("Map is " + dateEvent);
                        eventList = null;
                        cnt++;
                    }
				}
			}
			return dateEvent;
		}

		/* Prepare the salsa event model */
		public SalsaEventModel processEvent(Document doc, SalsaEventModelFactory semf, String eType) {

			System.out.println("Starting the final event processing...");
			SalsaEventModel sem = semf.createSalsaEventModel();

			Elements tableEl = doc.getElementsByTag("table");
			sem.seteType(eType);
			// Parse the table to get the event details
			Iterator<Element> tabItr = tableEl.iterator();
			boolean found = false;
			while (tabItr.hasNext() && found == false) {
				Element tabEl = (Element) tabItr.next();
				if (tabEl.hasAttr("id")) {
					System.out.println("Found it!" + tabEl.attr("id"));
					found = true;
					Elements trEls = tabEl.getElementsByTag("tr");
					Iterator<Element> trItr = trEls.iterator();
					while (trItr.hasNext()) {
						Element trEl = (Element) trItr.next();
						Elements tdEls = trEl.getElementsByTag("td");
						Iterator<Element> tdItr = tdEls.iterator();
						String raw_data_val = null;
						String raw_data_key = null;
						boolean cond = false;
						while (tdItr.hasNext() && cond == false) {
							Element finalElKey = (Element) tdItr.next();
							if (finalElKey.hasText() && finalElKey.text().contains(":")
									&& finalElKey.text().endsWith(":"))
								raw_data_key = finalElKey.text();
							else if (finalElKey.hasText())
								raw_data_val = finalElKey.text();

							if (raw_data_key != null && raw_data_val != null) {
								cond = true;
								System.out.println(raw_data_key + " " +
								raw_data_val);
								populateModel(raw_data_key, raw_data_val, sem);
							}
						}
					}

				}
			}

			calculateDistance(sem);
			System.out.println(
					"*************************************************************************************************************************************************** ");
			System.out.println("Event: " + sem.getEvent());
			System.out.println("Date: " + sem.getDate());
			System.out.println("Time: " + sem.getTime());
			System.out.println("Cost: " + sem.getCost());
			System.out.println("Address: " + sem.getAddress());
			System.out.println("Event Type: " + sem.geteType());
			System.out.println("Distance: " + sem.getDistance());
			System.out.println("Travel Duration: " + sem.getTravelDuration());

			return sem;
		}

		public void calculateDistance(SalsaEventModel sem) {

			/*
			 * System.out.println("Calculating distance and travel time from: "
			 * + txtRegion.getText().toString());
			 */
			String origin = "&origins=" + txtRegion.getText().toString().replace(" ", "+");
			String destination = "&destinations=" + sem.getAddress().replace(" ", "+");
			String distance = "0", duration = "0";
			try {
				/*
				 * System.out.println("URL is:" + BASE + origin + destination +
				 * KEY);
				 */
				URL googleDMURL = new URL(BASE + origin + destination + KEY);

				HttpURLConnection con = (HttpURLConnection) googleDMURL.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Accept", "application/json");
				InputStream is = con.getInputStream();

				Writer writer = new StringWriter();
				Reader reader = null;
				char[] buffer = new char[1024];
				try {
					reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} finally {
					is.close();
				}
				String result = writer.toString();
				System.out.println("Got the response from google DM API as: " + result);

				DistanceMatrixResponse dmr = new Gson().fromJson(result, DistanceMatrixResponse.class);
				List<com.test.androidrestconsumer.search.DistanceMatrixResponse.Rows> rows = dmr.rows;
				Iterator<com.test.androidrestconsumer.search.DistanceMatrixResponse.Rows> itr = rows.iterator();
				while (itr.hasNext()) {
					@SuppressWarnings("unchecked")
					List<com.test.androidrestconsumer.search.DistanceMatrixResponse.Rows.Elements> elms = ((List<com.test.androidrestconsumer.search.DistanceMatrixResponse.Rows.Elements>) itr
							.next().elements);
					Iterator<com.test.androidrestconsumer.search.DistanceMatrixResponse.Rows.Elements> eItr = elms
							.iterator();
					while (eItr.hasNext()) {
						com.test.androidrestconsumer.search.DistanceMatrixResponse.Rows.Elements elm = (com.test.androidrestconsumer.search.DistanceMatrixResponse.Rows.Elements) eItr
								.next();
						if (elm.status.equalsIgnoreCase("OK")) {
							distance = elm.distance.text;
							duration = elm.duration.text;
							/*
							 * System.out.
							 * println("Values fetched from response: " +
							 * sem.getDistance() + " " +
							 * sem.getTravelDuration());
							 */
						}
					}

				}
			} catch (MalformedURLException e) {
				System.out.println("Distance calculation failed. Please check the address.");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Some failure occured with distance calculation.");
				e.printStackTrace();
			} finally {

				sem.setDistance(distance);
				sem.setTravelDuration(duration);
			}

		}

		public void populateModel(String key, String val, SalsaEventModel sem) {

			if (key.equalsIgnoreCase("Event:"))
				sem.setEvent(val);
			else if (key.equalsIgnoreCase("Date:"))
				sem.setDate(val);
			else if (key.equalsIgnoreCase("Time:"))
				sem.setTime(val);
			else if (key.equalsIgnoreCase("Cost:"))
				sem.setCost(val);
			else if (key.equalsIgnoreCase("Address:")) {
				val = val.replace("Get Directions via Google Maps", "");
				sem.setAddress(val);
			}

		}

	}

	public void btnGetEventList(View view) {
		// eventList = new ArrayList<SalsaEventModel>();
		txtEventType = (Spinner) findViewById(R.id.txtEventType);
		
		UpdateTask ut = new UpdateTask(this);
		ut.execute();
		while (!ut.isDone()) {
			try {
				//System.out.println("Sleeping n waiting..");
                //Toast.makeText(this,"Please wait updating...",Toast.LENGTH_SHORT);
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (false)
			System.out.println("Replaced Dismiss");
		else
			System.out.println("Dismissed");
		System.out.println("Woke up.." + eventList);
		String eventType = txtEventType.getSelectedItem().toString();

		prepareSelectedSortedEvents(eventType);
		lstTest = (ListView) findViewById(R.id.listview);

		arrayAdapter = new AdapterSEM(MainActivity.this, R.layout.listitems, selSorEventList);

		lstTest.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();

	}

	public void prepareSelectedSortedEvents(String eventType) {
		Collections.sort(eventList);
		if (!eventType.equalsIgnoreCase("Show All")) {
			Iterator itr = eventList.iterator();
			if (eventType.equalsIgnoreCase("Salsa Lessons")) {
				eventType = "egreen";
			} else if (eventType.equalsIgnoreCase("Just Dancing")) {
				eventType = "ered";
			} else if (eventType.equalsIgnoreCase("Featured Events")) {
				eventType = "eorange";
			} else if (eventType.equalsIgnoreCase("Lessons and Dancing")) {
				eventType = "eblue";
			}

			while (itr.hasNext()) {
				SalsaEventModel s = (SalsaEventModel) itr.next();
				if (eventType.equalsIgnoreCase(s.geteType())) {
					selSorEventList.add(s);
				}
			}

		} else {
			selSorEventList = eventList;
		}
	}

}
