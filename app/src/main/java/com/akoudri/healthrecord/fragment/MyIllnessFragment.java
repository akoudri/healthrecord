package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akoudri.healthrecord.app.R;


public class MyIllnessFragment extends Fragment {

    private View view;

    public static MyIllnessFragment newInstance()
    {
        return new MyIllnessFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_illness, container, false);
        return view;
    }

}