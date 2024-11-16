package com.cookandroid.tbg;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeocodingResponse {

    @SerializedName("results")
    public List<Result> results;

    public class Result {
        @SerializedName("formatted_address")
        public String formattedAddress;
    }
}