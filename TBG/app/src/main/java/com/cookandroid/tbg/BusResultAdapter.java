package com.cookandroid.tbg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BusResultAdapter extends RecyclerView.Adapter<BusResultAdapter.BusViewHolder> {

    private final List<Bus> busList;
    private Context context;

    public BusResultAdapter(Context context,List<Bus> busList) {
        this.busList = busList;
        this.context = context;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus bus = busList.get(position);

        holder.departureStation.setText(bus.getDepartureStation());
        Log.d("ㅇㅇ", "출발역");
        holder.departureDate.setText(bus.getDepartureDate());
        Log.d("ㅇㅇ", "출발일");
        holder.departureTime.setText(formatTime(bus.getDepartureTime()));
        Log.d("ㅇㅇ", "출발시간");
        holder.arrivalStation.setText(bus.getArrivalStation());
        Log.d("ㅇㅇ", "도착역");
        holder.arrivalDate.setText(bus.getArrivalDate());
        Log.d("ㅇㅇ", "도착일");
        holder.arrivalTime.setText(formatTime(bus.getArrivalTime()));
        Log.d("ㅇㅇ", "도착시간");
        String formattedPrice = String.format("₩" + "%,d", Integer.parseInt(bus.getPrice()));
        holder.busPrice.setText(formattedPrice);
        Log.d("뭘넣고있을까여", bus.getSeatClass());
        Log.d("ㅇㅇ", "가격");
        holder.seatClass.setText(bus.getSeatClass());




        // 항목 클릭 시 Skyscanner로 이동
        holder.itemView.setOnClickListener(v -> {

            // Skyscanner URL 생성
            String url = "https://www.kobus.co.kr/oprninf/alcninqr/oprnAlcnPage.do";

            // URL을 웹 브라우저로 열기 위한 Intent
            if (url != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return busList.size();
    }


    private String formatTime(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date parsedDate = inputFormat.parse(dateTime);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    private String formatDate(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(dateTime);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            Log.e("에러", e.toString());
            e.printStackTrace();
            return dateTime;
        }
    }

    static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView departureStation, departureDate, departureTime, arrivalStation, arrivalDate, arrivalTime, busPrice,seatClass;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            departureStation = itemView.findViewById(R.id.bus_departureStation);
            departureDate = itemView.findViewById(R.id.bus_departureDate);
            departureTime = itemView.findViewById(R.id.bus_departureTime);
            arrivalStation = itemView.findViewById(R.id.bus_arrivalStation);
            arrivalDate = itemView.findViewById(R.id.bus_arrivalDate);
            arrivalTime = itemView.findViewById(R.id.bus_arrivalTime);
            busPrice = itemView.findViewById(R.id.busPrice);
            seatClass = itemView.findViewById(R.id.seatClass1);
        }
    }
}