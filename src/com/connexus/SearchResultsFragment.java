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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

public class SearchResultsFragment extends Fragment {
	
    private AsyncHttpClient httpClient = new AsyncHttpClient();
    
    private Map<Integer, String> streamNameMap = new HashMap<Integer, String>();
    private String query = "";
    private int startPage = 0;
    private int endPage = 8;
    
	public SearchResultsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_search_results,
				container, false);
		this.query = getArguments().getString("query");
		
		loadStreams(rootView);
		
		Button moreStreams = (Button) rootView.findViewById(R.id.moreButtonSR);
		moreStreams.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startPage += 8;
				endPage += 8;
				loadStreams(rootView);
			}
		});
		
		Button search = (Button) rootView.findViewById(R.id.searchButtonSR);
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText searchBox = (EditText) rootView.findViewById(R.id.searchBoxSR);
				query = searchBox.getText().toString();
				startPage = 0;
				endPage = 8;
				loadStreams(rootView);
			}
		});
		return rootView;
	}
	
	void loadStreams(final View rootView) {
		final TextView responseText = (TextView) rootView.findViewById(R.id.textView2);
        final LinearLayout row1 = (LinearLayout) rootView.findViewById(R.id.streamTableRow1SR);
        row1.removeAllViews();
        final LinearLayout row2 = (LinearLayout) rootView.findViewById(R.id.streamTableRow2SR);
        row2.removeAllViews();
		httpClient.post(Connexus.REQUEST_URL + "search?query_string=" + query, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String result = "";
            	try {
					JSONObject jsonResponse = new JSONObject(new String(response));
					JSONArray streamList = jsonResponse.getJSONArray("stream_list");
					if (startPage > streamList.length()) {
						startPage = 0;
						endPage = 8;
					}
					String returnText = streamList.length() + " results for " + query +
							", click on an image to view stream";
					TextView returnMessage = (TextView) rootView.findViewById(R.id.returnMessageSR);
					returnMessage.setText(returnText);
					for (int i=startPage; i<streamList.length() && i<endPage; i++) {
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
