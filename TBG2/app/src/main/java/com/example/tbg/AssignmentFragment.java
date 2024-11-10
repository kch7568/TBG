package com.example.tbg;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AssignmentFragment extends Fragment {
    //ui 요소 선언
    private EditText etSearch;
    private ImageButton btnSearch;
    private Button writeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //fragment 레이아웃 inflate
        View view = inflater.inflate(R.layout.fragment_assignment, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        writeButton = view.findViewById(R.id.writeButton);

        //edittext에 키보드 액션 리스너 설정
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        //검색버튼 클릭 리스너 설정
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        //글쓰기 버튼 클릭 리스너 설정
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticeboardWritingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        Log.d("AssignmentFragment", "Search query: " + query); // 로그 추가

        if (TextUtils.isEmpty(query)) {
            Toast.makeText(getContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            // 여기에 실제 검색 로직을 구현
            Toast.makeText(getContext(), "검색어: " + query, Toast.LENGTH_SHORT).show();
        }
    }
}