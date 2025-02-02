package com.cookandroid.tbg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AirportAdapter extends RecyclerView.Adapter<AirportAdapter.AirportViewHolder> {
    private final List<Airport> originalAirportList; // 전체 목록
    private final List<Airport> filteredAirportList; // 필터링된 목록
    private OnItemClickListener onItemClickListener; // 클릭 리스너 인터페이스

    // 생성자
    public AirportAdapter(List<Airport> airportList) {
        this.originalAirportList = airportList;
        this.filteredAirportList = new ArrayList<>(airportList); // 초기 상태로 전체 목록 추가
    }

    // 인터페이스 정의: 클릭 이벤트 처리
    public interface OnItemClickListener {
        void onItemClick(String airportName);
    }

    // 외부에서 클릭 리스너를 설정
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AirportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subway, parent, false);
        return new AirportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AirportViewHolder holder, int position) {
        Airport airport = filteredAirportList.get(position);

        holder.stationName.setText(airport.getName());
        holder.stationIcon.setImageResource(airport.getImageResId());

        // 첫 번째 아이템에서 Line View 숨김
        if (position == 0) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(airport.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredAirportList.size();
    }

    // 필터링 메소드
    public void filter(String query) {
        filteredAirportList.clear();

        if (query.isEmpty()) {
            filteredAirportList.addAll(originalAirportList);
        } else {
            for (Airport airport : originalAirportList) {
                if (airport.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredAirportList.add(airport);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class AirportViewHolder extends RecyclerView.ViewHolder {
        public final View line;
        public final ImageView stationIcon;
        public final TextView stationName;

        public AirportViewHolder(@NonNull View itemView) {
            super(itemView);
            stationIcon = itemView.findViewById(R.id.imageViewIcon);
            stationName = itemView.findViewById(R.id.textViewStation);
            line = itemView.findViewById(R.id.Line);
        }
    }
}
