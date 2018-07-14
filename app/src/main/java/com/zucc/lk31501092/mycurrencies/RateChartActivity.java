package com.zucc.lk31501092.mycurrencies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class RateChartActivity extends Activity {
    List<Double> changeRates = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        
    }

    private class CalculateRateTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog( RateChartActivity.this );
            progressDialog.setTitle( "Calculating Result..." );
            progressDialog.setMessage( "One moment please..." );
            progressDialog.setCancelable( false );
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            List<RateRecord> rateRecords;
            String strForCode = strings[0];
            String strHomCode = strings[1];
            String strAmount = "1";

            rateRecords = LitePal.order( "timestamp" ).limit( 10 ).find( RateRecord.class );
            for (int i = 9; i >= 0; i--) {
                try {
                    double rate = 0;
                    JSONObject jsonRates = new JSONObject( rateRecords.get( i ).getRates() );
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
                    changeRates.add( rate );
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }

            }
            return true;
        }
    }
}
