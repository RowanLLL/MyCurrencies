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

public class RateChangeAdapter extends ArrayAdapter<Rate> {
    private int resourceid;

    public RateChangeAdapter(@NonNull Context context, int resource, @NonNull List<Rate> objects) {
        super( context, resource, objects );
        this.resourceid = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Rate date = getItem( position );
        View view;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceid,null);
        }
        else {
            view = convertView;
        }
        TextView forCode = view.findViewById(R.id.rateListForCode);
        TextView homCode = view.findViewById(R.id.rateListhomCode);
        TextView Amount = view.findViewById(R.id.rateListResult);
        forCode.setText( date.getForCode() );
        homCode.setText( date.getHomCode() );
        Amount.setText( date.getAmount() );
        return view;
    }
}
