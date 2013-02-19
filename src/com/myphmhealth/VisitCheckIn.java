package com.myphmhealth;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class VisitCheckIn extends Activity {

	private String[] data = {"Tx Exerc","NM Re-Ed","Aquatic Therapy", "Massage", "Unlisted"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.visitcheckin);
        Calendar cal = Calendar.getInstance();
        Integer month = cal.get(Calendar.MONTH);
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        Integer year = cal.get(Calendar.YEAR);
        String date = Integer.toString(month)
        					+":"+Integer.toString(day)
        					+":"+Integer.toString(year);
        TextView visitDate = (TextView) findViewById(R.id.textView1);
        visitDate.setText(date);
        
        //TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        //String uid = tManager.getDeviceId();
        //Log.d("PHmHealth", uid);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	
	
}
