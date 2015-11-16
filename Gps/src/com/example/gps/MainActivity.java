package com.example.gps;

import java.text.DateFormat;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	private static final String REQUESTING_LOCATION_UPDATES_KEY = "true";
	private static final String LOCATION_KEY = null;
	private static final String LAST_UPDATED_TIME_STRING_KEY = null;
	GoogleApiClient mGoogleApiClient;
	Location mLastLocation;
	TextView mLatitudeText ;
	TextView mLongitudeText ;
	TextView mLastUpdateTimeText;
    boolean mRequestingLocationUpdates=true;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    Object mLastUpdateTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLongitudeText = ((TextView) findViewById(R.id.mLongitudeText));
		mLatitudeText = ((TextView) findViewById(R.id.mLatitudeText));
		mLastUpdateTimeText = ((TextView) findViewById(R.id.mLastUpdateTimeText));
		buildGoogleApiClient();
		connect();
		updateValuesFromBundle(savedInstanceState);
	}
	
	private void connect() {
		mGoogleApiClient.connect();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
	                mGoogleApiClient);
	        if (mLastLocation != null) {
	            mLatitudeText.setText("Latitud: "+ String.valueOf(mLastLocation.getLatitude()));
	            mLongitudeText.setText("Longitud: " + String.valueOf(mLastLocation.getLongitude()));
	        }
			if (mRequestingLocationUpdates) {
				createLocationRequest();
	            startLocationUpdates();
	        }
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	protected void createLocationRequest() {
	    mLocationRequest = new LocationRequest();
	    mLocationRequest.setInterval(10000);
	    mLocationRequest.setFastestInterval(5000);
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}
	
	protected void startLocationUpdates() {
	    LocationServices.FusedLocationApi.requestLocationUpdates(
	            mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		 	mCurrentLocation = location;
	        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
	        updateUI();
	}
	
	private void updateUI() {
        mLatitudeText.setText("Latitud: "+ String.valueOf(mLastLocation.getLatitude()));
        mLongitudeText.setText("Longitud: " + String.valueOf(mLastLocation.getLongitude()));
        mLastUpdateTimeText.setText("Hora"+String.valueOf(mLastUpdateTime));
    }
	
	@Override
	protected void onPause() {
	    super.onPause();
	    stopLocationUpdates();
	}

	protected void stopLocationUpdates() {
	    LocationServices.FusedLocationApi.removeLocationUpdates(
	            mGoogleApiClient, this);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
	        startLocationUpdates();
	    }
	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
	            mRequestingLocationUpdates);
	    savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
	    savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, String.valueOf(mLastUpdateTime));
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	private void updateValuesFromBundle(Bundle savedInstanceState) {
	    if (savedInstanceState != null) {
	        // Update the value of mRequestingLocationUpdates from the Bundle, and
	        // make sure that the Start Updates and Stop Updates buttons are
	        // correctly enabled or disabled.
	        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
	            mRequestingLocationUpdates = savedInstanceState.getBoolean(
	                    REQUESTING_LOCATION_UPDATES_KEY);
	        }

	        // Update the value of mCurrentLocation from the Bundle and update the
	        // UI to show the correct latitude and longitude.
	        if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
	            // Since LOCATION_KEY was found in the Bundle, we can be sure that
	            // mCurrentLocationis not null.
	            mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
	        }

	        // Update the value of mLastUpdateTime from the Bundle and update the UI.
	        if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
	            mLastUpdateTime = savedInstanceState.getString(
	                    LAST_UPDATED_TIME_STRING_KEY);
	        }
	    }
	}
}
