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

public class UploadFragment extends Fragment {
	
    private AsyncHttpClient httpClient = new AsyncHttpClient();

	public UploadFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_upload,
				container, false);
		
		Button cameraButton = (Button) rootView.findViewById(R.id.useCameraButtonU);
		cameraButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, new CameraFragment());
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		return rootView;
	}
}
