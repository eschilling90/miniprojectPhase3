package com.connexus;

import org.apache.http.Header;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SearchResultsFragment extends Fragment {
	
    private AsyncHttpClient httpClient = new AsyncHttpClient();

	public SearchResultsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_search_results,
				container, false);
		return rootView;
	}
}
