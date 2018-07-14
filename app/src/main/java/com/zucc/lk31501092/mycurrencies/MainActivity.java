package com.zucc.lk31501092.mycurrencies;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button mCalcButton;
    private TextView mConvertedTextView;
    private EditText mAmountEditText;
    private Spinner mForSpinner, mHomSpinner;
    private String[] mCurrencies;

    public static final String FOR = "FOR_CURRENCY";
    public static final String HOM = "HOM_CURRENCY";

    private String mKey;
    public static final String RATES = "rates";
    public static final String URL_BASE = "http://openexchangerates.org/api/latest.json?app_id=";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat( "#,##0.00000" );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mCalcButton = findViewById( R.id.btn_calc );
        mConvertedTextView = findViewById( R.id.txt_converted );
        mAmountEditText = findViewById( R.id.edt_amount );
        mForSpinner = findViewById( R.id.spn_for );
        mHomSpinner = findViewById( R.id.spn_hom );

        ArrayList<String> arrayList = (ArrayList<String>) getIntent().getSerializableExtra( SplashActivity.KEY_ARRAYLIST );
        Collections.sort( arrayList );
        mCurrencies = arrayList.toArray( new String[arrayList.size()] );

        //Spinner数据
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>( this, R.layout.spinner_closed, mCurrencies );
        arrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mForSpinner.setAdapter( arrayAdapter );
        mHomSpinner.setAdapter( arrayAdapter );

        //Spinner监听
        mForSpinner.setOnItemSelectedListener( this );
        mHomSpinner.setOnItemSelectedListener( this );

        //SharedPreference读取
        if (savedInstanceState == null && (PrefsMgr.getString( this, HOM )) == null) {
            mForSpinner.setSelection( findPositionGivenCode( "USD", mCurrencies ) );
            mHomSpinner.setSelection( findPositionGivenCode( "CNY", mCurrencies ) );

            PrefsMgr.setString( this, FOR, "USD" );
            PrefsMgr.setString( this, HOM, "CNY" );
        } else {
            mForSpinner.setSelection( findPositionGivenCode( PrefsMgr.getString( this, FOR ), mCurrencies ) );
            mHomSpinner.setSelection( findPositionGivenCode( PrefsMgr.getString( this, HOM ), mCurrencies ) );
        }

        mCalcButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = mAmountEditText.getText().toString();
                if (!number.trim().equals( "" ))
                    new CurrencyConverterTask().execute( URL_BASE + mKey );
            }
        } );

        mKey = getKey();

        Intent intent = new Intent( this, getRatesService.class );
        getRatesService.enqueueWork( getApplicationContext(), intent );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mnu_invert:
                invertCurrencies();
                break;
            case R.id.mnu_codes:
                launchBrowser( SplashActivity.URL_CODES );
                break;
            case R.id.mnu_record:
                Intent intent = new Intent( this, RecordActivity.class );
                startActivity( intent );
                break;
            case R.id.mnu_change:
                Intent intent1 = new Intent( this, RateChangeActivity.class );
                startActivity( intent1 );
                break;
            case R.id.mnu_exit:
                finish();
                break;
        }
        return true;

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void launchBrowser(String strUri) {
        if (isOnline()) {
            Uri uri = Uri.parse( strUri );
            //call an implicit intent
            Intent intent = new Intent( Intent.ACTION_VIEW, uri );
            startActivity( intent );
        }
    }

    private void invertCurrencies() {
        int nFor = mForSpinner.getSelectedItemPosition();
        int nHom = mHomSpinner.getSelectedItemPosition();
        mForSpinner.setSelection( nHom );
        mHomSpinner.setSelection( nFor );
        mConvertedTextView.setText( "" );

        PrefsMgr.setString( this, FOR, extractCodeFromCurrency( (String) mForSpinner.getSelectedItem() ) );
        PrefsMgr.setString( this, HOM, extractCodeFromCurrency( (String) mHomSpinner.getSelectedItem() ) );
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spn_for:
                PrefsMgr.setString( this, FOR, extractCodeFromCurrency( (String) mForSpinner.getSelectedItem() ) );
                break;
            case R.id.spn_hom:
                PrefsMgr.setString( this, HOM, extractCodeFromCurrency( (String) mHomSpinner.getSelectedItem() ) );
                break;
            default:
                break;
        }
        mConvertedTextView.setText( "" );

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private int findPositionGivenCode(String code, String[] currencies) {
        for (int i = 0; i < currencies.length; i++) {
            if (extractCodeFromCurrency( currencies[i] ).equalsIgnoreCase( code )) {
                return i;
            }
        }
        //default
        return 0;
    }

    private String extractCodeFromCurrency(String currency) {
        return (currency).substring( 0, 3 );
    }

    private String getKey() {
        AssetManager assetManager = this.getResources().getAssets();
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open( "keys.properties" );
            properties.load( inputStream );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty( "open_key" );
    }

    @SuppressLint("StaticFieldLeak")
    private class CurrencyConverterTask extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog( MainActivity.this );
            progressDialog.setTitle( "Calculating Result..." );
            progressDialog.setMessage( "One moment please..." );
            progressDialog.setCancelable( true );
            progressDialog.setButton( DialogInterface.BUTTON_NEGATIVE,
                    "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CurrencyConverterTask.this.cancel( true );
                            progressDialog.dismiss();
                        }
                    } );
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            return new JSONParser().getJSONFromUrl( strings[0] );
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            double dCalculated = 0.0;
            String strForCode = extractCodeFromCurrency( mCurrencies[mForSpinner.getSelectedItemPosition()] );
            String strHomCode = extractCodeFromCurrency( mCurrencies[mHomSpinner.getSelectedItemPosition()] );
            String strAmount = mAmountEditText.getText().toString();
            try {
                if (jsonObject == null) {
                    throw new JSONException( "no data available." );
                }
                JSONObject jsonRates = jsonObject.getJSONObject( RATES );
                if (strHomCode.equalsIgnoreCase( "USD" )) {
                    dCalculated = Double.parseDouble( strAmount ) /
                            jsonRates.getDouble( strForCode );
                } else if (strForCode.equalsIgnoreCase( "USD" )) {
                    dCalculated = Double.parseDouble( strAmount ) *
                            jsonRates.getDouble( strHomCode );
                } else {
                    dCalculated = Double.parseDouble( strAmount ) *
                            jsonRates.getDouble( strHomCode )
                            / jsonRates.getDouble( strForCode );
                }

                Record record = new Record();
                record.setForCode( strForCode );
                record.setForAmount( strAmount );
                record.setHomCode( strHomCode );
                record.setHomAmount( new DecimalFormat( "0.00" ).format( dCalculated ) );
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat( "yyyy/MM/dd HH:mm" );
                Date curDate = new Date( System.currentTimeMillis() );
                record.setTime( formatter.format( curDate ) );
                record.save();

            } catch (JSONException e) {
                Toast.makeText( MainActivity.this, "There's been a JSON exception: " + e.getMessage(), Toast.LENGTH_LONG ).show();
                mConvertedTextView.setText( "" );
                e.printStackTrace();
            }
            mConvertedTextView.setText( DECIMAL_FORMAT.format( dCalculated ) + " " + strHomCode );
            progressDialog.dismiss();
        }
    }

}
