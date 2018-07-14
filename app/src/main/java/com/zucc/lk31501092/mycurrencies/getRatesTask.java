package com.zucc.lk31501092.mycurrencies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class getRatesTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... strings) {
        Log.d( "TAG", "打印时间: " + new Date().toString() );
        return new JSONParser().getJSONFromUrl( strings[0] );
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                throw new JSONException( "no data available." );
            }
            List<RateRecord> list = new ArrayList<>();
            list = LitePal.where( "timestamp = ?", jsonObject.getLong( "timestamp" )+"" ).find( RateRecord.class );
            if (list.size() > 0) {
                Log.d( "isExisted", "True" );
            } else {
                RateRecord rateRecord = new RateRecord();
                rateRecord.setTimestamp( jsonObject.getLong( "timestamp" ) );
                rateRecord.setRates( jsonObject.getJSONObject( "rates" ) + "" );
                rateRecord.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
