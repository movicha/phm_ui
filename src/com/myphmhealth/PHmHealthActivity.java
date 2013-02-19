package com.myphmhealth;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.myphmhealth.dialog.GPSDisabledDialog;

public class PHmHealthActivity extends Activity {
    /** Called when the activity is first created. */
    
	private HashMap<String, String> password = new HashMap<String, String>();
	private long LocationUpdateFrequency = 1000*60*1;
	private LocationManager locationManager;
	private LocationListener listener;
	private EditText pin;
	private ReverseGeocodingTask getAddresses;
	private Boolean gotLocation = false;
	private ProgressDialog _progressDlg;
	private Boolean authenticated = false;
	private JSONObject myobj;
	private String ret;
	private String aaddress;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initPasswords();
        setContentView(R.layout.main);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*10, 100, listener);
        TextView forgotPassword = (TextView)findViewById(R.id.forgot_password);
        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        pin = (EditText) findViewById(R.id.edit_message);
        if(pin.requestFocus())
        {
        	Log.d("EditText", "pin got focus");
        	InputMethodManager inputM = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        	if(inputM != null)
        	{
        		Log.d("Input Manager", inputM.toString());
        		inputM.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
        	}
        }
        Log.d("EditText", "Setting Action Listener");
        pin.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(EditorInfo.IME_ACTION_DONE == actionId)
				{
					callBackEnd atask = new callBackEnd();
					atask.execute(0);
				}
				return false;
			}
		});

        forgotPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callForgotPasswordScreen();
				
			}
		});
        
		
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
		
        listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			//	 TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				
				showGPSDialog();
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				
				Log.d("Location", location.toString());
				locationManager.removeUpdates(listener);
				if(Geocoder.isPresent())
				{
					getAddresses = new ReverseGeocodingTask(getApplicationContext());
					getAddresses.execute(new Location[] {location});
					
				}
			}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				LocationUpdateFrequency, 
				0, 
				listener);
    	    	
    }
    
    private void showGPSDialog() {
    	DialogFragment newFragment = GPSDisabledDialog.newInstance(
                R.string.gps_disabled);
        newFragment.show(getFragmentManager(), "dialog");
	}

	private void enableLocationSettings() {
    	Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
	}

	public void callForgotPasswordScreen()
    {
    	Toast.makeText(this, "Call forgot password screen", Toast.LENGTH_LONG).show();
    }
    
    private void initPasswords()
    {
    	password.put("hinkel", "Jennifer");
    	password.put("mehta", "Aditya");
    }
    
    @SuppressWarnings("static-access")
	public void logIn(View view)
    {
    	callBackEnd atask = new callBackEnd();
    	_progressDlg = new ProgressDialog(this);
    	_progressDlg = ProgressDialog.show(this, "Logging in", "Logging in....", true);
		atask.execute(0);
    }

    private void authenticate()
    {
    	
    	Log.d("PhmHealth", "Meathod Called");
    	InputMethodManager inputManager = 
    	        (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE); 
    	inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
    	
    	String passwordEntered = pin.getText().toString().toLowerCase();
    	String user = password.get(passwordEntered);    	
    	String message = new String();
    	if(user == null)
    	{
    		message = "Invalid Pin Entered";
    		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    		authenticated = true;
    		//goToNextScreen();
    	}
    	
    	//TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        //String uid = tManager.getDeviceId();
    	
        //DefaultHttpClient httpClient;
        //HttpContext localContext;
        

        //HttpResponse response = null;
        //HttpPost httpPost = null;
        //HttpGet httpGet = null;
        
        //myobj = new JSONObject();
    	//try {
		//	myobj.put("deviceID", uid);
		//	myobj.put("lat", (double)38.867238);
		//	myobj.put("lng", (double)-77.262056);
		//	myobj.put("password", "0okm9ijN");
		//	myobj.put("username", "frontenddev");
		//	
		//	Log.d("PHmHealth", myobj.toString());
		//	
		//	HttpParams params = new BasicHttpParams();
		 //   HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		  //  HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		   // HttpProtocolParams.setUseExpectContinue(params, true);

		    //SchemeRegistry schReg = new SchemeRegistry();
		    //schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		    //schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
	//	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
	//		
	//	    DefaultHttpClient client = new DefaultHttpClient(conMgr, params);
	//	    
	//	    String url = "http://phmhealth.us/Auth.svc/Authenticate";
	//	    httpPost = new HttpPost(url);
	//	    StringEntity tmp = null;        
//
//	        Log.d("PHmHealth", "Setting httpPost headers");
//
//	        httpPost.setHeader("User-Agent", "Android");
//	        httpPost.setHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
//	        httpPost.setHeader("Content-Type", "application/json");
//	        try {
//	            tmp = new StringEntity(myobj.toString(),"UTF-8");
//	        } catch (UnsupportedEncodingException e) {
//	            Log.e("PHmHealth", "HttpUtils : UnsupportedEncodingException : "+e);
//	        }
//	        
//	        httpPost.setEntity(tmp);
//
//	        Log.d("PhmHealth", url + "?" + myobj.toString());
//	        localContext = new BasicHttpContext();
//	        try {
//	            response = client.execute(httpPost,localContext);
//
//	            if (response != null) {
//	                ret = EntityUtils.toString(response.getEntity());
//	            }
//	        } catch (Exception e) {
//	            Log.e("PhmHealth", "HttpUtils: " + e);
//	        }

//	        Log.d("PhmHealth", "Returning value:" + ret);
//	        
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    }
    
	private void goToNextScreen() {
		_progressDlg = ProgressDialog.show(this, "Logging in", "Logging in");
		Intent gotonextActivity = new Intent("com.myphmhealth.VisitCheckIn");
 	   //gotonextActivity.putExtra("lat", loc.getLatitude());
 	   //gotonextActivity.putExtra("long", loc.getLongitude());
 	   startActivity(gotonextActivity);
 	   //Log.d("Distance", "Distance between the two points is "+results[0]+" meters");
 	   _progressDlg.dismiss();
	}

	public void doPositiveClick() {
		enableLocationSettings();
	}

	public void doNegativeClick() {
		Toast.makeText(this, "Cant get Loaction", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	private class callBackEnd extends AsyncTask<Object, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Object... params) {
			authenticate();
			return true;
		}
		
	}
	
	// AsyncTask encapsulating the reverse-geocoding API.  Since the geocoder API is blocked,
	// we do not want to invoke it from the UI thread.
	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Boolean> {
	    Context mContext;

	    Location loc;
	    public ReverseGeocodingTask(Context context) {
	        super();
	        mContext = context;
	    }

	    @Override
	    protected Boolean doInBackground(Location... params) {
	        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

	        loc = params[0];
	        List<Address> addresses = null;
	        try {
	            // Call the synchronous getFromLocation() method by passing in the lat/long values.
	            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
	        } catch (IOException e) {
	            e.printStackTrace();
	            // Update UI field with the exception.
	            //Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
	        }
	        if (addresses != null && addresses.size() > 0) {
	            
	        	for(int i = 0; i<addresses.size(); i++)
	        	{
	        	Address address = addresses.get(i);
	            // Format the first line of address (if available), city, and country name.
	            String addressText = String.format("%s, %s, %s",
	                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                    address.getLocality(),
	                    address.getCountryName());
	            aaddress = addressText;
	        	}
	        	while(!authenticated)
	        	{}
	        	return true;
	        }
	        return false;
	    }
	    
	    
	    protected void onPostExecute(Boolean result) 
	    {
	       if(result)
	       {
	    	   locationManager.removeUpdates(listener);
	    	   float results[] = {0,0,0};
	    	   //Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), (double)38.867238, (double)-77.262056, results);
	    	   gotLocation = true;
	    	   Intent gotonextActivity = new Intent("com.myphmhealth.VisitCheckIn");
	    	   //Intent goToNextActivity = new Intent(
	    	   gotonextActivity.putExtra("lat", loc.getLatitude());
	    	   gotonextActivity.putExtra("long", loc.getLongitude());
	    	   //Log.d("Distance", "Distance between the two points is "+results[0]+" meters");
	    	   gotonextActivity.putExtra("address", aaddress);
	    	   _progressDlg.dismiss();
	    	   startActivity(gotonextActivity);
	       }
	    }
	}
	    
}