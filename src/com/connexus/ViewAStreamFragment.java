package com.connexus;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

public class ViewAStreamFragment extends Fragment {
	
    private AsyncHttpClient httpClient = new AsyncHttpClient();

    private String streamname = "";
    private int streamId = 0;
    private int startPage = 0;
    private int endPage = 16;
    private int streamSize = 0;
    
    public ViewAStreamFragment() {
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_view_a_stream,
				container, false);
		
		this.streamname = getArguments().getString("streamname");
		this.streamId = getArguments().getInt("streamId");
		
		TextView streamName = (TextView) rootView.findViewById(R.id.streamName);
		streamName.setText(streamname);
		
        getImages(rootView);
		
		Button uploadButton = (Button) rootView.findViewById(R.id.uploadButtonVAS);
		uploadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, new UploadFragment());
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		Button streamButton = (Button) rootView.findViewById(R.id.streamButtonVAS);
		streamButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, new ViewStreamsFragment());
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		Button morePictures = (Button) rootView.findViewById(R.id.moreButtonVAS);
		morePictures.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startPage +=16;
				endPage += 16;
				if (startPage > streamSize) {
					startPage = 0;
					endPage = 16;
				}
				getImages(rootView);
			}
		});
		return rootView;
	}
	
	private void getImages(View rootView) {
        final TextView responseText = (TextView) rootView.findViewById(R.id.textView2);
        final LinearLayout row1 = (LinearLayout) rootView.findViewById(R.id.streamTableRow1VAS);
        row1.removeAllViews();
        final LinearLayout row2 = (LinearLayout) rootView.findViewById(R.id.streamTableRow2VAS);
        row2.removeAllViews();
        final LinearLayout row3 = (LinearLayout) rootView.findViewById(R.id.streamTableRow3VAS);
        row3.removeAllViews();
        final LinearLayout row4 = (LinearLayout) rootView.findViewById(R.id.streamTableRow4VAS);
        row4.removeAllViews();
        String params = "?streamId=" + streamId + "&start_page=" + startPage + "&end_page=" + endPage;
		httpClient.post(Connexus.REQUEST_URL + "viewstream" + params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            	String result = "";
            	//responseText.setText(new String(response));
            	try {
					JSONObject jsonResponse = new JSONObject(new String(response));
					streamSize = jsonResponse.getInt("stream_size");
					JSONArray imageList = jsonResponse.getJSONArray("blob_key_list");
					for (int i=0; i<imageList.length() && i<16; i++) {
						String imageURL = imageList.getString(i);
						SmartImageView streamImage = new SmartImageView(getActivity());
						streamImage.setImageUrl(imageURL);
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
