package com.cookandroid.tbg;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlightAdapter_t extends RecyclerView.Adapter<FlightAdapter_t.FlightViewHolder> {

    private List<Flight_t> flights;
    private Context context;

    public FlightAdapter_t(Context context, List<Flight_t> flights) {
        this.context = context;
        this.flights = flights;
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight_t flight = flights.get(position);

        holder.tvFlightPrice.setText("가격: " + flight.getPrice());
        holder.tvDepartureInfo.setText("출발: " + flight.getDepartureInfo());
        holder.tvArrivalInfo.setText("도착: " + flight.getArrivalInfo());

        if (flight.getReturnInfo() != null && !flight.getReturnInfo().isEmpty()) {
            holder.tvReturnInfo.setText("복귀: " + flight.getReturnInfo());
            holder.tvReturnInfo.setVisibility(View.VISIBLE);
        } else {
            holder.tvReturnInfo.setVisibility(View.GONE);
        }

        // 아이템 클릭 리스너 추가
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlightDetailActivity_t.class);
            intent.putExtra("price", flight.getPrice());
            intent.putExtra("departureInfo", flight.getDepartureInfo());
            intent.putExtra("arrivalInfo", flight.getArrivalInfo());
            intent.putExtra("returnInfo", flight.getReturnInfo());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlightPrice, tvDepartureInfo, tvArrivalInfo, tvReturnInfo;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFlightPrice = itemView.findViewById(R.id.tv_flight_price);
            tvDepartureInfo = itemView.findViewById(R.id.tv_departure_info);
            tvArrivalInfo = itemView.findViewById(R.id.tv_arrival_info);
            tvReturnInfo = itemView.findViewById(R.id.tv_return_info);
        }
    }
}
