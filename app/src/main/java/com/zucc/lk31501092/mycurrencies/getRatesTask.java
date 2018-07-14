package com.zucc.lk31501092.mycurrencies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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
            RateRecord rateRecord = new RateRecord();
            rateRecord.setTimestamp( jsonObject.getLong( "timestamp" ) );
            rateRecord.setRates( jsonObject.getJSONObject( "rates" ) + "" );
            rateRecord.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
