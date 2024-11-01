package com.cookandroid.tbg;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView WelcomeMessage;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // WelcomeMessage TextView 초기화
        WelcomeMessage = view.findViewById(R.id.WelcomeMessage);

        ImageView imageView1 = view.findViewById(R.id.imageView1);
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        ImageView imageView3 = view.findViewById(R.id.imageView3);
        ImageView imageView4 = view.findViewById(R.id.imageView4);
        ImageView imageView5 = view.findViewById(R.id.imageView5);

        imageView1.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 1", "여행지 1에 대한 설명입니다.", R.drawable.travledescription1);
        });

        imageView2.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 2", "여행지 2에 대한 설명입니다.", R.drawable.travledescription2);
        });

        imageView3.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 3", "여행지 3에 대한 설명입니다.", R.drawable.travledescription3);
        });

        imageView4.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 4", "여행지 4에 대한 설명입니다.", R.drawable.travledescription4);
        });

        imageView5.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 5", "여행지 5에 대한 설명입니다.", R.drawable.travledescription5);
        });

        // 이미지 클릭 리스너 추가...

        return view;
    }
    public void setWelcomeMessageText(String message) {
            WelcomeMessage.setText(message);
    }

    private void showTravelInfoDialog(String title, String message, int imageResId) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_travel_info, null);

        ImageView dialogImageView = dialogView.findViewById(R.id.dialogImageView);
        dialogImageView.setImageResource(imageResId);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        dialogTitle.setText(title);

        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        dialogMessage.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}
