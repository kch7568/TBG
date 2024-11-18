package com.example.tbg;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class Accountfragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accountfragment, container, false);

        LinearLayout settingLayout = view.findViewById(R.id.setting);
        settingLayout.setOnClickListener(v -> {  // 여기를 settingLayout으로 수정
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        });
    return view;
    }
}
