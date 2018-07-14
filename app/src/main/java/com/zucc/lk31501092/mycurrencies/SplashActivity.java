package com.zucc.lk31501092.mycurrencies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.zucc.lk31501092.mycurrencies.tools.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SplashActivity extends AppCompatActivity {
    public static final String URL_CODES = "http://openexchangerates.org/api/currencies.json";
    public static final String KEY_ARRAYLIST = "key_arraylist";
    public static final String KEY_ARRAYLISTSIMPLE = "key_arraylist_simple";
    private ArrayList<String> mCurrencies;
    private ArrayList<String> mSimpleCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_splash );
        new FetchCodesTask().execute( URL_CODES );
    }

    private class FetchCodesTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            return new JSONParser().getJSONFromUrl( strings[0] );
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                if (jsonObject == null) {
                    throw new JSONException( "no data available." );
                }
                Iterator iterator = jsonObject.keys();
                String key = "";
                mCurrencies = new ArrayList<>();
                mSimpleCurrencies = new ArrayList<>();
                while (iterator.hasNext()) {
                    key = (String) iterator.next();
                    mCurrencies.add( key + " | " + jsonObject.getString( key ) );
                    mSimpleCurrencies.add( key );
                }

                Intent mainIntent = new Intent( SplashActivity.this, MainActivity.class );
                mainIntent.putExtra( KEY_ARRAYLIST, mCurrencies );
                mainIntent.putExtra( KEY_ARRAYLISTSIMPLE, mSimpleCurrencies );
                startActivity( mainIntent );

                finish();
            } catch (JSONException e) {
                Toast.makeText(
                        SplashActivity.this,
                        "There's been a JSON exception: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                e.printStackTrace();
                finish();
            }
        }
    }
}
