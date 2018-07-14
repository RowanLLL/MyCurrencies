package com.zucc.lk31501092.mycurrencies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.text.DecimalFormat;
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
            List<RateRecord> list;
            list = LitePal.where( "timestamp = ?", jsonObject.getLong( "timestamp" )+"" ).find( RateRecord.class );
            if (list.size() > 0) {
                Log.d( "isExisted", "True" );
            } else {
                //save in sqlite
                RateRecord rateRecord = new RateRecord();
                rateRecord.setTimestamp( jsonObject.getLong( "timestamp" ) );
                rateRecord.setRates( jsonObject.getJSONObject( "rates" ) + "" );
                rateRecord.save();

                //update rates
                List<Rate> rateList;
                rateList = LitePal.findAll( Rate.class );
                for (Rate rate: rateList) {
                    String amount = Calculate( rate.getForCode(), rate.getHomCode() );
                    rate.setAmount( amount );
                    rate.updateAll( "forCode =? and homCode=?", rate.getForCode(), rate.getHomCode() );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String Calculate(String strForCode, String strHomCode) {
        List<RateRecord> rateRecords;
        String result = "0.0000";
        String strAmount = "1";
        double rate = 0;

        rateRecords = LitePal.order( "timestamp DESC" ).limit( 1 ).find( RateRecord.class );
        try {
            JSONObject jsonRates = new JSONObject( rateRecords.get( 0 ).getRates() );
            if (strHomCode.equalsIgnoreCase( "USD" )) {
                rate = Double.parseDouble( strAmount ) /
                        jsonRates.getDouble( strForCode );
            } else if (strForCode.equalsIgnoreCase( "USD" )) {
                rate = Double.parseDouble( strAmount ) *
                        jsonRates.getDouble( strHomCode );
            } else {
                rate = Double.parseDouble( strAmount ) *
                        jsonRates.getDouble( strHomCode )
                        / jsonRates.getDouble( strForCode );
            }
            result = new DecimalFormat( "0.0000" ).format( rate );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
