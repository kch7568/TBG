package com.cookandroid.tbg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TrainResultAdapter extends RecyclerView.Adapter<TrainResultAdapter.TrainViewHolder> {

    private final List<Train> trainList;
    private Context context;

    // 열차 ID별 로고 매핑
    private static final Map<String, Integer> trainLogoMap = new HashMap<>();
    static {
        trainLogoMap.put("00", R.drawable.train2_icon); // KTX
        trainLogoMap.put("01", R.drawable.train2_icon); // 새마을호
        trainLogoMap.put("02", R.drawable.train2_icon); // 무궁화호
        trainLogoMap.put("08", R.drawable.train2_icon); // ITX-청춘
        trainLogoMap.put("09", R.drawable.train2_icon); // ITX-새마을
        // 필요 시 추가 열차 종류 정의
    }

    public TrainResultAdapter(List<Train> trainList, Context context) {
        this.trainList = trainList;
        this.context = context;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_item, parent, false);
        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        Train train = trainList.get(position);

        // 출발역과 출발 시간 설정
        holder.departureStation.setText(train.getDepartureStation());
        holder.depatureDate.setText(train.getDepartureDate());
        holder.departureTime.setText(formatTime(train.getDepartureTime()));
        // 도착역과 도착 시간 설정
        holder.arrivalStation.setText(train.getArrivalStation());
        holder.arrivalDate.setText(train.getArrivalDate());
        holder.arrivalTime.setText(formatTime(train.getArrivalTime()));
        // 열차 종류 및 로고 설정
        holder.trainType.setText(train.getTrainType());
        holder.trainLogo.setImageResource(getTrainLogo(train.getVehiclekndid()));

        // 가격 설정
        String formattedPrice = String.format( "₩" + "%,d", Integer.parseInt(train.getPrice()));
        holder.trainPrice.setText(formattedPrice);



        // 항목 클릭 시 Skyscanner로 이동
        holder.itemView.setOnClickListener(v -> {

            // Skyscanner URL 생성
            String url = "https://www.korail.com/intro";

            // URL을 웹 브라우저로 열기 위한 Intent
            if (url != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return trainList.size();
    }

    private int getTrainLogo(String vehiclekndid) {
        return trainLogoMap.getOrDefault(vehiclekndid, R.drawable.train2_icon); // 기본 로고 반환
    }


    // 시간 형식 변환: HH:mm
    private String formatTime(String dateTime) {
        try {
            // 입력 형식: yyyyMMddHHmm (예: "202411290355")
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());

            // 출력 형식: HH:mm (예: "03:55")
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            // 입력 형식으로 파싱
            Date parsedDate = inputFormat.parse(dateTime);

            // 출력 형식으로 변환
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return dateTime; // 변환 실패 시 원래 값을 반환
        }
    }

    private String formatDate(String dateTime) {
        try {
            // 입력 형식: yyyyMMddHHmm (예: "202304010800")
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());

            // 출력 형식: yyyy-MM-dd (예: "2023-04-01")
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // 입력 형식으로 파싱
            Date parsedDate = inputFormat.parse(dateTime);

            // 출력 형식으로 변환
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            Log.e("에러", e.toString());
            e.printStackTrace();
            return dateTime; // 변환 실패 시 원래 값을 반환
        }
    }


    static class TrainViewHolder extends RecyclerView.ViewHolder {
        TextView departureStation, depatureDate, departureTime, arrivalStation, arrivalDate, arrivalTime, trainType, trainPrice;
        ImageView trainLogo;

        public TrainViewHolder(@NonNull View itemView) {
            super(itemView);

            departureStation = itemView.findViewById(R.id.departureStation);
            depatureDate = itemView.findViewById(R.id.departureDate);
            departureTime = itemView.findViewById(R.id.departureTime);
            arrivalStation = itemView.findViewById(R.id.arrivalStation);
            arrivalDate = itemView.findViewById(R.id.arrivalDate);
            arrivalTime = itemView.findViewById(R.id.arrivalTime);
            trainLogo = itemView.findViewById(R.id.trainLogo);
            trainType = itemView.findViewById(R.id.trainType);
            trainPrice = itemView.findViewById(R.id.trainPrice);
        }
    }
}
