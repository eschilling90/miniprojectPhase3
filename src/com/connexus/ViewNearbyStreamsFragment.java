package com.connexus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

public class ViewNearbyStreamsFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	
    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private Map<Integer, String> streamNameMap = new HashMap<Integer, String>();
    private List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    
    private LocationClient mLocationClient;
    private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(30*1000) // 5 seconds
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private double lat = 0.0;
    private double lon = 0.0;
    
	public ViewNearbyStreamsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_view_nearby_streams,
				container, false);
		
		loadImages(rootView);
		
		Button streamsButton = (Button) rootView.findViewById(R.id.viewStreamsButtonVNS);
		streamsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, new ViewStreamsFragment());
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		return rootView;
	}

	void loadImages(View rootView) {
		final TextView responseText = (TextView) rootView.findViewById(R.id.textView2);
        final LinearLayout row1 = (LinearLayout) rootView.findViewById(R.id.streamTableRow1VNS);
        row1.removeAllViews();
        final LinearLayout row2 = (LinearLayout) rootView.findViewById(R.id.streamTableRow2VNS);
        row2.removeAllViews();
        final LinearLayout row3 = (LinearLayout) rootView.findViewById(R.id.streamTableRow3VNS);
        row3.removeAllViews();
        final LinearLayout row4 = (LinearLayout) rootView.findViewById(R.id.streamTableRow4VNS);
        row4.removeAllViews();
		httpClient.post(Connexus.REQUEST_URL + "nearbyImages", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String result = "";
            	try {
            		JSONArray jsonResponse = new JSONArray(new String(response));
					for (int i=0; i<jsonResponse.length(); i++) {
						JSONObject image = jsonResponse.getJSONObject(i);
						int streamId = image.getInt("streamId");
						String streamname = image.getString("streamname");
						double lon = image.getDouble("lon");
						double lat = image.getDouble("lat");
						String url = image.getString("url");
						Location phoneLoc = mLocationClient.getLastLocation();
						Location imageLoc = new Location(phoneLoc);
						imageLoc.setLatitude(lat);
						imageLoc.setLongitude(lon);
						float dist = phoneLoc.distanceTo(imageLoc);
						ImageInfo imageInfo = new ImageInfo(streamId, streamname, dist, url);
						imageList.add(imageInfo);
						Collections.sort(imageList, new Comparator<ImageInfo>() {
							@Override
							public int compare(ImageInfo image1, ImageInfo image2) {
								return (int) (image1.distance - image2.distance);
							}
						});
					}
					//responseText.setText(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	for (int j=0; j<imageList.size() && j<16; j++) {
            		SmartImageView streamImage = new SmartImageView(getActivity());
					streamImage.setImageUrl(imageList.get(j).url);
					streamImage.setId(imageList.get(j).id);
					streamNameMap.put(imageList.get(j).id, imageList.get(j).name);
					streamImage.setClickable(true);
					streamImage.setOnClickListener(new OnClickListener () {

						@Override
						public void onClick(View arg0) {
							FragmentTransaction ft = getFragmentManager().beginTransaction();
							ViewAStreamFragment f = new ViewAStreamFragment();
							Bundle args = new Bundle();
							int streamId = arg0.getId();
							String streamname = streamNameMap.get(streamId);
							args.putString("streamname", streamname);
							args.putInt("streamId", streamId);
							f.setArguments(args);
							ft.replace(R.id.container, f);
							ft.addToBackStack(null);
							ft.commit();
						}
						
					});
					streamImage.setAdjustViewBounds(true);
					streamImage.setMaxHeight(100);
					streamImage.setMaxWidth(100);
					if (j<4) {
						row1.addView(streamImage);
					} else if (j<8) {
						row2.addView(streamImage);
					} else if (j<12) {
						row3.addView(streamImage);
					} else {
						row4.addView(streamImage);
					}
            	}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            	responseText.setText(Integer.toString(statusCode));
            }
        });
	}
	
	private class ImageInfo {
		int id = 0;
		String name = "";
		float distance = 0;
		String url = "";
		
		ImageInfo(int i, String n, float d, String u) {
			this.id = i;
			this.name = n;
			this.distance = d;
			this.url = u;
		}
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location == null) {
			return;
		}
		lat = location.getLatitude();
		lon = location.getLongitude();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getActivity(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
		mLocationClient.connect();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}
}
