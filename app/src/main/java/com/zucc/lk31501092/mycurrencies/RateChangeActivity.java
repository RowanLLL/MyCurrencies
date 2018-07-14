package com.zucc.lk31501092.mycurrencies;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class RateChangeActivity extends Activity {
    private List<Rate> rates = new ArrayList<>();
    EditText rateSearch;
    ImageView rateDeleteText;
    private RateChangeAdapter adapter;
    ArrayList<String> forCode = new ArrayList<>();
    ArrayList<String> homCode = new ArrayList<>();
    ArrayList<String> Amount = new ArrayList<>();
    Handler myhandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_rates );

//        Rate rate = new Rate();
//        rate.setForCode( "CNY" );
//        rate.setHomCode( "USD" );
//        rate.setAmount( "6.9" );
//        rate.save();
//        rate.updateAll( "forCode = ?", "CNY" );

        initDatas();
        set_edtSearch_TextChanged();
        set_ivDeleteText_OnClick();

        adapter = new RateChangeAdapter( this, R.layout.rates_list, rates );
        adapter.notifyDataSetChanged();

        ListView listView = findViewById( R.id.listview_rates );
        listView.setAdapter( adapter );
        listView.setTextFilterEnabled( true );
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
}
