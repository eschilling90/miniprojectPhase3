package com.connexus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;

public class ViewNearbyStreamsFragment extends Fragment {
	
    private AsyncHttpClient httpClient = new AsyncHttpClient();

	public ViewNearbyStreamsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_view_nearby_streams,
				container, false);
		
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
}
