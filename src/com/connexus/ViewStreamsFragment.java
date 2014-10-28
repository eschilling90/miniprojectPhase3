package com.connexus;

import org.apache.http.Header;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ViewStreamsFragment extends Fragment {
	
    private AsyncHttpClient httpClient = new AsyncHttpClient();

	public ViewStreamsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_view_streams,
				container, false);
        final TextView responseText = (TextView) rootView.findViewById(R.id.textView2);
		httpClient.post(Connexus.REQUEST_URL + "viewAllStreams", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                responseText.setText(Integer.toString(statusCode));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            	responseText.setText(Integer.toString(statusCode));
            }
        });
		Button searchButton = (Button) rootView.findViewById(R.id.searchButtonFS);
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, new SearchResultsFragment());
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
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, new ViewAStreamFragment());
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		return rootView;
	}
}
