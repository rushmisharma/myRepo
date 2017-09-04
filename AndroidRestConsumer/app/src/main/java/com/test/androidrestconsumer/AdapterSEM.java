package com.test.androidrestconsumer;

import java.util.List;

import com.test.androidrestconsumer.search.SalsaEventModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterSEM extends ArrayAdapter<SalsaEventModel> {
	int resource;
	String response;
	Context context;

	// Initialize adapter
	public AdapterSEM(Context context, int resource,
			List<SalsaEventModel> eventList) {
		super(context, resource, eventList);
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout semView;
		// Get the current alert object
		SalsaEventModel sem = getItem(position);

		// Inflate the view
		if (convertView == null) {
			semView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			semView = (LinearLayout) vi.inflate(resource, null);
			// semView = vi.
		} else {
			semView = (LinearLayout) convertView;
		}

		System.out.println("Getting view for: " + sem + " " + sem.getEvent()
				+ " " + sem.getAddress());

		// Get the text boxes from the listitem.xml file
		TextView seType = (TextView) semView.findViewById(R.id.txtSEType);
		TextView seName = (TextView) semView.findViewById(R.id.txtSEName);
		TextView seAddress = (TextView) semView.findViewById(R.id.txtSEAddress);
		// Button btn = (Button)semView.findViewById(R.id.btnDirection);

		if (sem.geteType().equalsIgnoreCase("eorange")) {
			// semView.setBackgroundColor(Color.GREEN);
			// seType.setText(sem.getEvent() + "   Dist:" + sem.getDistance()
			// +", Travel Duration"+ sem.getTravelDuration());
			seType.setClickable(true);
			seType.setMovementMethod(LinkMovementMethod.getInstance());
			String dAddr = sem.getAddress().trim().replaceAll(" ", "+");
			String text = "<a href=\"http://maps.google.com/maps?saddr=Pleasanton,California&daddr="
					+ dAddr
					+ "\">"
					+ sem.getEvent()
					+ "   Dist:"
					+ sem.getDistance()
					+ ", Travel Duration"
					+ sem.getTravelDuration() + "</a>";
			seType.setText(Html.fromHtml(text));
			seType.setBackgroundColor(Color.rgb(247, 228, 153));
			// Assign the appropriate data from our alert object above
			seName.setText(sem.getCost() + " Starts at: " + sem.getTime());
			seAddress.setText("Address: " + sem.getAddress());

			seName.setBackgroundColor(Color.rgb(249, 241, 219));
			seAddress.setBackgroundColor(Color.rgb(249, 241, 219));
			// btn.setBackgroundColor(Color.rgb(249, 241, 219));
		}
		if (sem.geteType().equalsIgnoreCase("egreen")) {
			// semView.setBackgroundColor(Color.GREEN);
			/*seType.setText(sem.getEvent() + "   Dist:" + sem.getDistance()
					+ ", Travel Duration:" + sem.getTravelDuration());*/
			seType.setClickable(true);
			seType.setMovementMethod(LinkMovementMethod.getInstance());
			String dAddr = sem.getAddress().trim().replaceAll(" ", "+");
			String text = "<a href=\"http://maps.google.com/maps?saddr=Pleasanton,California&daddr="
					+ dAddr
					+ "\">"
					+ sem.getEvent()
					+ "   Dist:"
					+ sem.getDistance()
					+ ", Travel Duration"
					+ sem.getTravelDuration() + "</a>";
			seType.setText(Html.fromHtml(text));
			seType.setBackgroundColor(Color.rgb(220, 249, 184));
			// Assign the appropriate data from our alert object above
			seName.setText(sem.getCost() + " Starts at: " + sem.getTime());
			seAddress.setText("Address: " + sem.getAddress());

			seName.setBackgroundColor(Color.rgb(241, 252, 214));
			seAddress.setBackgroundColor(Color.rgb(241, 252, 214));
			// btn.setBackgroundColor(Color.rgb(241, 252, 214));
		} else if (sem.geteType().equalsIgnoreCase("eblue")) {
			/*seType.setText(sem.getEvent() + "   Dist:" + sem.getDistance()
					+ ", Travel Duration:" + sem.getTravelDuration());*/
			seType.setClickable(true);
			seType.setMovementMethod(LinkMovementMethod.getInstance());
			String dAddr = sem.getAddress().trim().replaceAll(" ", "+");
			String text = "<a href=\"http://maps.google.com/maps?saddr=Pleasanton,California&daddr="
					+ dAddr
					+ "\">"
					+ sem.getEvent()
					+ "   Dist:"
					+ sem.getDistance()
					+ ", Travel Duration"
					+ sem.getTravelDuration() + "</a>";
			seType.setText(Html.fromHtml(text));
			seType.setBackgroundColor(Color.rgb(184, 222, 249));
			// Assign the appropriate data from our alert object above
			seName.setText(sem.getCost() + " Starts at: " + sem.getTime());
			seAddress.setText("Address: " + sem.getAddress());

			seName.setBackgroundColor(Color.rgb(219, 248, 249));
			seAddress.setBackgroundColor(Color.rgb(219, 248, 249));
			// btn.setBackgroundColor(Color.rgb(219, 248, 249));
		} else if (sem.geteType().equalsIgnoreCase("ered")) {
			/*seType.setText(sem.getEvent() + "   Dist:" + sem.getDistance()
					+ ", Travel Duration:" + sem.getTravelDuration());*/
			seType.setClickable(true);
			seType.setMovementMethod(LinkMovementMethod.getInstance());
			String dAddr = sem.getAddress().trim().replaceAll(" ", "+");
			String text = "<a href=\"http://maps.google.com/maps?saddr=Pleasanton,California&daddr="
					+ dAddr
					+ "\">"
					+ sem.getEvent()
					+ "   Dist:"
					+ sem.getDistance()
					+ ", Travel Duration"
					+ sem.getTravelDuration() + "</a>";
			seType.setText(Html.fromHtml(text));
			seType.setBackgroundColor(Color.rgb(249, 184, 202));
			// Assign the appropriate data from our alert object above
			seName.setText(sem.getCost() + " Starts at: " + sem.getTime());
			seAddress.setText("Address: " + sem.getAddress());

			seName.setBackgroundColor(Color.rgb(249, 229, 234));
			seAddress.setBackgroundColor(Color.rgb(249, 229, 234));
			// btn.setBackgroundColor(Color.rgb(249, 229, 234));
		}

		// Assign the appropriate data from our alert object above
		/*
		 * seName.setText(sem.getCost()+" Starts at: " + sem.getTime() + ", "+
		 * sem.getDistance() +", travel time"+ sem.getTravelDuration());
		 * seAddress.setText("Address: " +sem.getAddress());
		 */

		return semView;
	}
	
	/*public void showDialog(){
		AlertDialog.Builder alertDgBuilder = new AlertDialog.Builder(Activity.this);
		alertDgBuilder.setTitle("Add Event Nick Name");
		alertDgBuilder.setMessage("Click 'Save' to save the event nick name.").setCancelable(false).setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Clicked Save", Toast.LENGTH_LONG).show();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Clicked Cancel", Toast.LENGTH_LONG).show();
				dialog.cancel();
			}
		});
	}*/

	/*
	 * public void btnGetEventList(View view) { final Intent intent = new
	 * Intent(Intent.ACTION_VIEW, Uri.parse("http:/a/maps.google.com/maps?" +
	 * "saddr="+ "Pleasanton, California" + "&daddr="+dest_address ));
	 * 
	 * intent.setClassName("com.google.android.apps.maps",
	 * "com.google.android.maps.MapsActivity");
	 * 
	 * startActivity(intent); }
	 */
}
