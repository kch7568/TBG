package com.example.tbg;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SubwayAdapter extends RecyclerView.Adapter<SubwayAdapter.SubwayViewHolder> {

    private List<SubwayStation> subwayList;
    private List<SubwayStation> filteredList;

    public SubwayAdapter(List<SubwayStation> subwayList) {
        this.subwayList = subwayList;
        this.filteredList = new ArrayList<>(subwayList);  // 초기 필터 리스트는 전체 목록
    }

    @Override
    public SubwayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 각 항목 레이아웃을 Inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subway, parent, false);
        return new SubwayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubwayViewHolder holder, int position) {
        SubwayStation station = filteredList.get(position);
        holder.stationName.setText(station.getName());

        // 각 지하철역에 맞는 이미지 설정
        holder.stationIcon.setImageResource(station.getImageResId());  // 각 지하철역에 맞는 이미지를 설정
    }

    @Override
    public int getItemCount() {
        return filteredList.size();  // 필터된 목록의 크기를 반환
    }

    // 필터링 메소드
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(subwayList);  // 검색어가 없으면 전체 목록
        } else {
            for (SubwayStation station : subwayList) {
                if (station.getName().toLowerCase().contains(query.toLowerCase())) {  // 대소문자 구분 없이 검색
                    filteredList.add(station);
                }
            }
        }
        notifyDataSetChanged();  // 데이터가 변경되었음을 알림
    }

    // ViewHolder 클래스
    public static class SubwayViewHolder extends RecyclerView.ViewHolder {
        ImageView stationIcon;
        TextView stationName;

        public SubwayViewHolder(View itemView) {
            super(itemView);
            stationIcon = itemView.findViewById(R.id.imageViewIcon);  // 이미지 뷰
            stationName = itemView.findViewById(R.id.textViewStation);  // 텍스트 뷰
        }
    }
}