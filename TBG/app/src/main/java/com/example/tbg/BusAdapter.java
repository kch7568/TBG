package com.example.tbg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {
    private final List<BusExpress> originalBusList;  // 전체 목록
    private final List<BusExpress> filteredBusList;  // 필터링된 목록
    private OnItemClickListener onItemClickListener; // 클릭 리스너 인터페이스

    // 생성자
    public BusAdapter(List<BusExpress> busList) {
        this.originalBusList = busList;
        this.filteredBusList = new ArrayList<>(busList);  // 초기 상태로 전체 목록 추가
    }

    // 인터페이스 정의: 클릭 이벤트를 처리
    public interface OnItemClickListener {
        void onItemClick(String busName);
    }

    // 외부에서 클릭 리스너를 설정
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // ViewHolder 생성
    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아이템 레이아웃을 Inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subway, parent, false);
        return new BusViewHolder(view);
    }

    // ViewHolder 바인딩
    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        // 현재 위치의 데이터 가져오기
        BusExpress busExpress = filteredBusList.get(position);

        // 데이터 설정
        holder.stationName.setText(busExpress.getName());
        holder.stationIcon.setImageResource(busExpress.getImageResId());

        // 첫 번째 아이템에서는 Line View 숨김
        if (position == 0) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }

        // 클릭 이벤트 설정
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(busExpress.getName());
            }
        });
    }

    // RecyclerView 아이템 개수 반환
    @Override
    public int getItemCount() {
        return filteredBusList.size();
    }

    // 필터링 메소드
    public void filter(String query) {
        filteredBusList.clear(); // 기존 필터링 데이터를 초기화

        if (query.isEmpty()) {
            // 검색어가 없으면 전체 목록 반환
            filteredBusList.addAll(originalBusList);
        } else {
            // 검색어가 포함된 데이터만 필터링
            for (BusExpress express : originalBusList) {
                if (express.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredBusList.add(express);
                }
            }
        }
        notifyDataSetChanged(); // RecyclerView에 데이터 변경 알림
    }

    // ViewHolder 클래스
    public static class BusViewHolder extends RecyclerView.ViewHolder {
        public final View line; // 아이템 사이의 라인
        public final ImageView stationIcon; // 역 아이콘
        public final TextView stationName; // 역 이름

        // ViewHolder 생성자
        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            stationIcon = itemView.findViewById(R.id.imageViewIcon);  // 아이콘 이미지
            stationName = itemView.findViewById(R.id.textViewStation);  // 역 이름 텍스트
            line = itemView.findViewById(R.id.Line);  // 아이템 간 구분선
        }
    }
}
