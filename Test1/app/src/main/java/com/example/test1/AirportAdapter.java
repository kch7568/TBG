package com.example.test1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AirportAdapter extends RecyclerView.Adapter<AirportAdapter.AirportViewHolder> {

    private final List<AirportResponse.Airport> airportList;

    // Constructor
    public AirportAdapter(List<AirportResponse.Airport> airportList) {
        this.airportList = airportList;
    }

    // ViewHolder 생성
    @NonNull
    @Override
    public AirportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_airport, parent, false);
        return new AirportViewHolder(view);
    }

    // 데이터 바인딩
    @Override
    public void onBindViewHolder(AirportViewHolder holder, int position) {
        AirportResponse.Airport airport = airportList.get(position);

        // 각 필드에 해당하는 데이터를 바인딩
        holder.tvAirportCodeIATA.setText(airport.getAirportCodeIATA());    // IATA 코드 (예: ADP)
        holder.tvAirportNameKorean.setText(airport.getKoreanAirportName());  // 한국 공항명
        holder.tvCountryKorean.setText(airport.getKoreanCountryName());       // 한국 국가명
    }

    // 아이템 개수 반환
    @Override
    public int getItemCount() {
        return airportList.size();
    }

    // ViewHolder 클래스
    public static class AirportViewHolder extends RecyclerView.ViewHolder {

        TextView tvAirportCodeIATA, tvAirportNameKorean, tvCountryKorean;

        public AirportViewHolder(View itemView) {
            super(itemView);
            tvAirportCodeIATA = itemView.findViewById(R.id.tv_airport_name);
            tvAirportNameKorean = itemView.findViewById(R.id.tv_airport_code);
            tvCountryKorean = itemView.findViewById(R.id.tv_city);

        }
    }
}
