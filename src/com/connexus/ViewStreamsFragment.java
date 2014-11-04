package com.connexus;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

public class ViewStreamsFragment extends Fragment {
	
    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private Map<Integer, String> streamNameMap = new HashMap<Integer, String>();
    
	public ViewStreamsFragment() {
	}
//record streamnames and ids
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_view_streams,
				container, false);
        
		getStreams(rootView, "");
		
		Button searchButton = (Button) rootView.findViewById(R.id.searchButtonFS);
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				SearchResultsFragment f = new SearchResultsFragment();
				Bundle args = new Bundle();
				EditText queryBox = (EditText) rootView.findViewById(R.id.findStreams);
				String query = queryBox.getText().toString();
				args.putString("query", query);
				f.setArguments(args);
				ft.replace(R.id.container, f);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		Button nearbyButton = (Button) rootView.findViewById(R.id.nearbyButton);
		nearbyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, new ViewNearbyStreamsFragment());
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		Button subbedButton = (Button) rootView.findViewById(R.id.subStreamsButton);
		subbedButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getStreams(rootView, Connexus.username);
			}
		});
		
		Button subStreamsButton = (Button) rootView.findViewById(R.id.subStreamsButton);
		if (Connexus.username.equals("")) {
			subStreamsButton.setVisibility(View.INVISIBLE);
		}
		return rootView;
	}
	
	private void getStreams(View rootView, String username) {
		final TextView responseText = (TextView) rootView.findViewById(R.id.textView2);
        final LinearLayout row1 = (LinearLayout) rootView.findViewById(R.id.streamTableRow1);
        row1.removeAllViews();
        final LinearLayout row2 = (LinearLayout) rootView.findViewById(R.id.streamTableRow2);
        row2.removeAllViews();
        final LinearLayout row3 = (LinearLayout) rootView.findViewById(R.id.streamTableRow3);
        row3.removeAllViews();
        final LinearLayout row4 = (LinearLayout) rootView.findViewById(R.id.streamTableRow4);
        row4.removeAllViews();
		httpClient.post(Connexus.REQUEST_URL + "viewAllStreams?username=" + username, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String result = "";
            	try {
					JSONObject jsonResponse = new JSONObject(new String(response));
					JSONArray streamList = jsonResponse.getJSONArray("stream_list");
					for (int i=0; i<streamList.length() && i<16; i++) {
						JSONObject stream = streamList.getJSONObject(i);
						int streamId = stream.getInt("stream_id");
						JSONArray nameURLPair = stream.getJSONArray(Integer.toString(streamId));
						String streamName = nameURLPair.getString(0);
						String coverImage = nameURLPair.getString(1);
						result += streamName + " ";
						if (coverImage.equals("")) {
							coverImage = "http://2.bp.blogspot.com/-2O6IkipP55Y/TtwkvDqXAvI/AAAAAAAADI4/ID6-pqBVRh4/s320/no_cover-large.jpg";
						}
						SmartImageView streamImage = new SmartImageView(getActivity());
						streamImage.setImageUrl(coverImage);
						streamImage.setId(streamId);
						streamNameMap.put(streamId, streamName);
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
						if (i<4) {
							row1.addView(streamImage);
						} else if (i<8) {
							row2.addView(streamImage);
						} else if (i<12) {
							row3.addView(streamImage);
						} else {
							row4.addView(streamImage);
						}
					}
					//responseText.setText(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            	responseText.setText(Integer.toString(statusCode));
            }
        });
	}
}
