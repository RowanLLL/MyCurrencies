package com.zucc.lk31501092.mycurrencies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.zucc.lk31501092.mycurrencies.adapter.RateChangeAdapter;
import com.zucc.lk31501092.mycurrencies.model.Rate;
import com.zucc.lk31501092.mycurrencies.model.RateRecord;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.zucc.lk31501092.mycurrencies.SplashActivity.KEY_ARRAYLISTSIMPLE;

public class RateChangeActivity extends Activity {
    private List<Rate> rates = new ArrayList<>();
    EditText rateSearch;
    ImageView rateDeleteText;
    FloatingActionButton floatingActionButton;
    private RateChangeAdapter adapter;
    ArrayList<String> forCode = new ArrayList<>();
    ArrayList<String> homCode = new ArrayList<>();
    ArrayList<String> Amount = new ArrayList<>();
    Handler myhandler = new Handler();
    private String[] mSimpleCurrencies;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_rates );

//        LitePal.deleteAll( Rate.class );

        initDatas();
        set_edtSearch_TextChanged();
        set_ivDeleteText_OnClick();

        adapter = new RateChangeAdapter( this, R.layout.rates_list, rates );
        adapter.notifyDataSetChanged();

        final ListView listView = findViewById( R.id.listview_rates );
        listView.setAdapter( adapter );
        listView.setTextFilterEnabled( true );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d( "click", i + "" );
                Intent intent = new Intent( listView.getContext(), RateChartActivity.class );
                intent.putExtra( "forCode", rates.get( i ).getForCode() );
                intent.putExtra( "homCode", rates.get( i ).getHomCode() );
                startActivity( intent );
            }
        } );
        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int itemI, long l) {
                Log.d( "Long", "" + itemI );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder( RateChangeActivity.this );
                mBuilder.setTitle( "警告" );
                mBuilder.setMessage( "你确定要删除吗？" );
                mBuilder.setPositiveButton( "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Rate rate = rates.get( itemI );

                        LitePal.deleteAll( Rate.class, "forCode = ? and homCode = ?", rate.getForCode(), rate.getHomCode() );
                        rates.remove( itemI );

                        refreshDatasAfterDelete();

                        adapter.notifyDataSetChanged();

                    }
                } );
                mBuilder.setNegativeButton( "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                } );

                AlertDialog alertDialog = mBuilder.create();
                alertDialog.show();
                return true;

            }
        } );

        mSimpleCurrencies = (String[]) getIntent().getSerializableExtra( KEY_ARRAYLISTSIMPLE );
//        mCurrencies = arrayList.toArray( new String[arrayList.size()] );

        floatingActionButton = findViewById( R.id.float_button );
        floatingActionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder( RateChangeActivity.this );
                View mView = getLayoutInflater().inflate( R.layout.rates_dialog, null );
                mBuilder.setTitle( "Add Record" );
                final Spinner forSpinner = mView.findViewById( R.id.dialog_forCode );
                final Spinner homSpinner = mView.findViewById( R.id.dialog_homCode );
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>( RateChangeActivity.this, android.R.layout.simple_spinner_item, mSimpleCurrencies );
                arrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                forSpinner.setAdapter( arrayAdapter );
                homSpinner.setAdapter( arrayAdapter );

                mBuilder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fcode = forSpinner.getSelectedItem().toString();
                        String hcode = homSpinner.getSelectedItem().toString();

                        Rate rate = new Rate();
                        rate.setForCode( fcode );
                        rate.setHomCode( hcode );
                        rate.setAmount( Calculate( fcode, hcode ) );
                        rate.save();

                        rates.add( rate );
                        forCode.add( fcode );
                        homCode.add( hcode );
                        Amount.add( rate.getAmount() );
                        adapter.notifyDataSetChanged();

                        dialogInterface.dismiss();
                    }
                } );

                mBuilder.setNegativeButton( "Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                } );

                mBuilder.setView( mView );
                AlertDialog alertDialog = mBuilder.create();
                alertDialog.show();
            }
        } );
    }

    private void initDatas() {
        rates = LitePal.findAll( Rate.class );
        for (Rate d : rates) {
            if (d != null) {
                forCode.add( d.getForCode() );
                homCode.add( d.getHomCode() );
                Amount.add( d.getAmount() );
            }
        }
    }

    private void refreshDatasAfterDelete() {
        List<Rate> rateList = new ArrayList<>();
        rateList = LitePal.findAll( Rate.class );
        forCode.clear();
        homCode.clear();
        Amount.clear();
        for (Rate d : rateList) {
            if (d != null) {
                forCode.add( d.getForCode() );
                homCode.add( d.getHomCode() );
                Amount.add( d.getAmount() );
            }
        }
    }

    private void set_edtSearch_TextChanged() {
        rateSearch = findViewById( R.id.rateSearch );

        rateSearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    rateDeleteText.setVisibility( View.GONE );
                } else {
                    rateDeleteText.setVisibility( View.VISIBLE );
                }

                myhandler.post( new Runnable() {
                    @Override
                    public void run() {
                        String keyWoeds = rateSearch.getText().toString();
                        rates.clear();
                        getmDataSub( rates, keyWoeds );
                        adapter.notifyDataSetChanged();
                    }
                } );
            }
        } );
    }

    private void getmDataSub(List<Rate> datas, String data) {
        int length = homCode.size();
        if (length != 0)
            for (int i = 0; i < length; ++i) {
                if (forCode.get( i ).contains( data.toUpperCase() ) || homCode.get( i ).contains( data.toUpperCase() )) {
                    Rate item = new Rate();
                    item.setForCode( forCode.get( i ) );
                    item.setHomCode( homCode.get( i ) );
                    item.setAmount( Amount.get( i ) );
                    datas.add( item );
                }
            }
    }

    private void set_ivDeleteText_OnClick() {
        rateDeleteText = findViewById( R.id.rateDeleteText );
        rateDeleteText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateSearch.setText( "" );
            }
        } );
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
