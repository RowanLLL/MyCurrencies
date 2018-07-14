package com.zucc.lk31501092.mycurrencies;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class RateChartActivity extends Activity {
    List<String> changeRates = new ArrayList<>();
    List<Long> times = new ArrayList<>();
    List<AxisValue> axisValues = new ArrayList<>();
    LineChartView chart;
    TextView forCode_TextView;
    TextView homCode_TextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chart );
        chart = findViewById( R.id.chart );
        forCode_TextView = findViewById( R.id.chart_forCode );
        homCode_TextView = findViewById( R.id.chart_homCode );

        Intent intent = getIntent();
        String forCode = intent.getStringExtra( "forCode" );
        String homCode = intent.getStringExtra( "homCode" );

        forCode_TextView.setText( forCode );
        homCode_TextView.setText( homCode );

        new CalculateRateTask().execute( forCode, homCode );
    }

    @SuppressLint("StaticFieldLeak")
    private class CalculateRateTask extends AsyncTask<String, Void, Boolean> {
        Boolean isSame = true;

        @Override
        protected Boolean doInBackground(String... strings) {
            List<RateRecord> rateRecords;
            String strForCode = strings[0];
            String strHomCode = strings[1];
            String strAmount = "1";

            rateRecords = LitePal.order( "timestamp DESC" ).limit( 5 ).find( RateRecord.class );
            for (int i = rateRecords.size() - 1; i >= 0; i--)
                try {
                    double rate = 0;

                    times.add( rateRecords.get( i ).getTimestamp() );

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
                    changeRates.add( new DecimalFormat( "0.0000" ).format( rate ) );
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            try {
                if (!aBoolean) {
                    throw new Exception( "no data available." );
                }
                ArrayList<PointValue> values = new ArrayList<>();//折线上的点
                float beforeValue = (float) Double.parseDouble( changeRates.get( 0 ) );
                for (int i = 0; i < changeRates.size(); i++) {
//                    Log.d( "你好", changeRates.get( i ) );
                    values.add( new PointValue( i, (float) Double.parseDouble( changeRates.get( i ) ) ) );

                    if (isSame) {
                        if (beforeValue != (float) Double.parseDouble( changeRates.get( i ) ))
                            isSame = false;
                    }

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat( "MM/dd HH时" );
                    formatter.setTimeZone( TimeZone.getTimeZone( "GMT+8:00" ) );
                    String time = formatter.format( new Date( times.get( i ) * 1000 ) );
                    AxisValue axisValue = new AxisValue( i );
                    axisValue.setLabel( time );
                    axisValues.add( axisValue );
                }
//                values.add( new PointValue( 5, (float) 0.1498 ) );


                ArrayList<Line> lines = new ArrayList<>();
                Line line = new Line( values ).setColor( Color.BLACK );//声明线并设置颜色
                line.setCubic( false );//设置是平滑的还是直的
                line.setHasLabels( false );
                lines.add( line );

                LineChartData data = new LineChartData();
                Axis axisX = new Axis( axisValues );//x轴
                Axis axisY = new Axis();//y轴
                axisX.setHasTiltedLabels( true );
                axisY.setHasLines( true );
                axisX.setTextColor( Color.BLACK );
                axisY.setTextColor( Color.BLACK );
                data.setAxisXBottom( axisX );
                data.setAxisYLeft( axisY );
                data.setLines( lines );
                data.setValueLabelBackgroundEnabled( true );
                chart.setLineChartData( data );
                chart.setValueSelectionEnabled( false );

                if (isSame) {
                    final Viewport v = new Viewport( chart.getMaximumViewport() );
                    v.top = (float) (beforeValue + 0.1000); //max value
                    v.bottom = (float) (beforeValue - 0.1000);  //min value
                    chart.setMaximumViewport( v );
                    chart.setCurrentViewport( v );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
