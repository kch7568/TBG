package com.cookandroid.tbg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirportResultAdapter extends RecyclerView.Adapter<AirportResultAdapter.FlightViewHolder> {

    private List<Flight> flightList;
    private Context context;

    // 항공사 코드와 로고 매핑
    private static final Map<String, Integer> airlineLogoMap = new HashMap<>();
    static {
        airlineLogoMap.put("KE", R.drawable.korean_air_logo);          // 대한항공
        airlineLogoMap.put("OZ", R.drawable.asiana_air_logo);         // 아시아나항공
        airlineLogoMap.put("TW", R.drawable.tway_air_logo);           // 티웨이항공
        airlineLogoMap.put("7C", R.drawable.jeju_air_logo);           // 제주항공
        airlineLogoMap.put("AA", R.drawable.american_air_logo);       // 아메리칸 항공
        airlineLogoMap.put("UA", R.drawable.united_air_logo);        // 유나이티드 항공
        airlineLogoMap.put("DL", R.drawable.delta_air_logo);         // 델타 항공
        airlineLogoMap.put("AF", R.drawable.air_france_logo);        // 에어프랑스
        airlineLogoMap.put("LH", R.drawable.lufthansa_air_logo);     // 독일항공
        airlineLogoMap.put("BA", R.drawable.british_air_logo);       // 영국항공
        airlineLogoMap.put("CX", R.drawable.cathay_pacific_logo);    // 캐세이퍼시픽 항공
        airlineLogoMap.put("QF", R.drawable.qantas_air_logo);        // 퀸즐랜드 항공
        airlineLogoMap.put("SQ", R.drawable.singapore_air_logo);     // 싱가포르 항공
        airlineLogoMap.put("NZ", R.drawable.nz_air_logo);            // 뉴질랜드 항공
        airlineLogoMap.put("JL", R.drawable.japan_air_logo);         // 일본항공
        airlineLogoMap.put("NH", R.drawable.ana_air_logo);           // 전일본공수
        airlineLogoMap.put("N6", R.drawable.nz_air_logo);           // 뉴질랜드 국적 항공

        airlineLogoMap.put("MU", R.drawable.eastern_china_air_logo); // 중국동방항공
        airlineLogoMap.put("CI", R.drawable.china_air_logo);         // 중화항공
        airlineLogoMap.put("TK", R.drawable.turkish_air_logo);       // 터키항공
        airlineLogoMap.put("CA", R.drawable.air_china_logo);        // 중국국제항공
        airlineLogoMap.put("QR", R.drawable.qatar_air_logo);        // 카타르항공
        airlineLogoMap.put("EK", R.drawable.emirates_air_logo);     // 에미레이트 항공
        airlineLogoMap.put("FM", R.drawable.shanghai_air_logo);     // 상하이 항공
        airlineLogoMap.put("MF", R.drawable.xiamen_air_logo);     // 상하이 항공


    }

    public AirportResultAdapter(Context context, List<Flight> flightList) {
        this.context = context;
        this.flightList = flightList;
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.api_item, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight flight = flightList.get(position);

        holder.departureDate.setText(flight.getDepartureDate());
        holder.departureTime.setText(flight.getDepartureTime());
        holder.departureLogo.setImageResource(getAirlineLogo(flight.getDepartureAirlineCode()));
        holder.departureAirlineName.setText(flight.getDepartureAirlineName());
        holder.departureLocation.setText(flight.getTitle().split(" - ")[0]); // 출발지 추가
        holder.flightPrice.setText("₩" + flight.getPrice());
        holder.departureAirport.setText(flight.getDepartureAirport()); // 출발 공항 설정


        if (flight.isRoundTrip()) {
            holder.arrivalSection.setVisibility(View.VISIBLE);
            holder.arrivalDate.setText(flight.getReturnDate());
            holder.arrivalTime.setText(flight.getReturnTime());
            holder.arrivalLogo.setImageResource(getAirlineLogo(flight.getReturnAirlineCode()));
            holder.arrivalAirlineName.setText(flight.getReturnAirlineName());
            holder.arrivalLocation.setText(flight.getTitle().split(" - ")[1]); // 도착지 추가
            holder.arrivalAirport.setText(flight.getArrivalAirport()); // 도착 공항 설정
        } else {
            holder.arrivalSection.setVisibility(View.GONE);
        }




        // 항목 클릭 시 Skyscanner로 이동
        holder.itemView.setOnClickListener(v -> {
            // Skyscanner URL 생성
            String url = "https://skyscanner.co.kr/";

            // URL을 웹 브라우저로 열기 위한 Intent
            if (url != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return flightList.size();
    }

    private int getAirlineLogo(String airlineCode) {
        return airlineLogoMap.getOrDefault(airlineCode, R.drawable.tbg_icon);
    }

    static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView departureDate, departureTime, arrivalDate, arrivalTime, flightPrice;
        ImageView departureLogo, arrivalLogo;
        TextView departureAirlineName, arrivalAirlineName;
        TextView departureLocation, arrivalLocation;
        TextView departureAirport, arrivalAirport; // 공항 정보 추가
        View arrivalSection; // 도착 정보를 담은 섹션

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);

            departureDate = itemView.findViewById(R.id.departureDate);
            departureTime = itemView.findViewById(R.id.departureTime);
            arrivalDate = itemView.findViewById(R.id.arrivalDate);
            arrivalTime = itemView.findViewById(R.id.arrivalTime);
            flightPrice = itemView.findViewById(R.id.flightPrice);
            departureLogo = itemView.findViewById(R.id.departureLogo);
            arrivalLogo = itemView.findViewById(R.id.arrivalLogo);
            departureAirlineName = itemView.findViewById(R.id.departureAirlineName);
            arrivalAirlineName = itemView.findViewById(R.id.arrivalAirlineName);
            departureLocation = itemView.findViewById(R.id.departureLocation);
            arrivalLocation = itemView.findViewById(R.id.arrivalLocation);
            departureAirport = itemView.findViewById(R.id.departureAirport); // 출발 공항
            arrivalAirport = itemView.findViewById(R.id.arrivalAirport); // 도착 공항
            arrivalSection = itemView.findViewById(R.id.arrivalSection);
        }
    }



}
