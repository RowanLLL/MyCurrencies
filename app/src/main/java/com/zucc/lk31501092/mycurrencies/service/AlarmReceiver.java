package com.zucc.lk31501092.mycurrencies.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, getRatesService.class);
        getRatesService.enqueueWork( context, intent );
    }
}
