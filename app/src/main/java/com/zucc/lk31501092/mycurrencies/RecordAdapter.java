package com.zucc.lk31501092.mycurrencies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordAdapter extends ArrayAdapter<Record>{
    private  int resourceid;

    RecordAdapter(@NonNull Context context, int resource, @NonNull List<Record> objects) {
        super( context, resource, objects );
        this.resourceid = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Record data = getItem( position );
        View view;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceid,null);
        }
        else {
            view = convertView;
        }
        TextView forCode = view.findViewById(R.id.forCode);
        TextView forAmount = view.findViewById(R.id.forAmount);
        TextView homCode = view.findViewById(R.id.homCode);
        TextView homAmount = view.findViewById(R.id.homAmount);
        TextView time = view.findViewById(R.id.time);
        assert data != null;
        forCode.setText(data.getForCode());
        forAmount.setText(data.getForAmount());
        homCode.setText(data.getHomCode());
        homAmount.setText(data.getHomAmount());
        time.setText(data.getTime());
        return view;
    }
}
