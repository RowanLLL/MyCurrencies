package com.zucc.lk31501092.mycurrencies;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.zucc.lk31501092.mycurrencies.adapter.RecordAdapter;
import com.zucc.lk31501092.mycurrencies.model.Record;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends Activity {
    private List<Record> datas = new ArrayList<>();
    EditText edtSearch;
    ImageView ivDeleteText;
    private RecordAdapter adapter;
    ArrayList<String> forCode = new ArrayList<>();
    ArrayList<String> forAmount = new ArrayList<>();
    ArrayList<String> homCode = new ArrayList<>();
    ArrayList<String> homAmount = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    Handler myhandler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_record );
        initDatas();
        set_edtSearch_TextChanged();
        set_ivDeleteText_OnClick();

        adapter = new RecordAdapter( this, R.layout.record_list, datas );
        adapter.notifyDataSetChanged();

        ListView listView = findViewById( R.id.listview );
        listView.setAdapter( adapter );
        listView.setTextFilterEnabled( true );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d( "click", i+"" );
            }
        } );
    }

    private void initDatas() {
        datas = LitePal.findAll( Record.class );
        for (Record d : datas) {
            if (d != null) {
                time.add( d.getTime() );
                forCode.add( d.getForCode() );
                forAmount.add( d.getForAmount() );
                homCode.add( d.getHomCode() );
                homAmount.add( d.getHomAmount() );
            }
        }
    }

    private void getmDataSub(List<Record> datas, String data) {
        int length = homCode.size();
        for (int i = 0; i < length; ++i) {
            if (time.get( i ).contains( data.toUpperCase() ) || forCode.get( i ).contains( data.toUpperCase() ) || forAmount.get( i ).contains( data.toUpperCase() ) || homCode.get( i ).contains( data.toUpperCase() ) || homAmount.get( i ).contains( data.toUpperCase() )) {
                Record item = new Record();
                item.setTime( time.get( i ) );
                item.setForCode( forCode.get( i ) );
                item.setForAmount( forAmount.get( i ) );
                item.setHomCode( homCode.get( i ) );
                item.setHomAmount( homAmount.get( i ) );
                datas.add( item );
            }
        }
    }

    private void set_edtSearch_TextChanged() {
        edtSearch = findViewById( R.id.etSearch );

        edtSearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    ivDeleteText.setVisibility( View.GONE );
                } else {
                    ivDeleteText.setVisibility( View.VISIBLE );
                }

                myhandler.post( new Runnable() {
                    @Override
                    public void run() {
                        String keyWoeds = edtSearch.getText().toString();
                        datas.clear();
                        getmDataSub( datas, keyWoeds );
                        adapter.notifyDataSetChanged();
                    }
                } );
            }
        } );
    }

    private void set_ivDeleteText_OnClick() {
        ivDeleteText = findViewById( R.id.ivDeleteText );
        ivDeleteText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtSearch.setText( "" );
            }
        } );
    }
}
