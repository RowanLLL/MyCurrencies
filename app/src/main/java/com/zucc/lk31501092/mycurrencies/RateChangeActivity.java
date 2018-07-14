package com.zucc.lk31501092.mycurrencies;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;

public class RateChangeActivity extends Activity {
    private ArrayList<RateRecord> rateRecords = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        rateRecords = (ArrayList<RateRecord>) LitePal.findAll( RateRecord.class );

        try {
            JSONObject ratesJson = new JSONObject( rateRecords.get( 0 ).getRates() );
            Log.d("RateRecords:", ratesJson.getDouble( "CNY" )+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        for (RateRecord rateRecord: rateRecords){
//            Log.d("RateRecords:", rateRecord.getRates()+"");
//        }

    }
}
