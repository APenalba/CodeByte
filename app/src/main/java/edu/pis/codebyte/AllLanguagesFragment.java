package edu.pis.codebyte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class AllLanguagesFragment extends Fragment {

    public AllLanguagesFragment() {
        // Required empty public constructor
    }


    public static AllLanguagesFragment newInstance(String param1, String param2) {
        AllLanguagesFragment fragment = new AllLanguagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_languages, container, false);

    }
}