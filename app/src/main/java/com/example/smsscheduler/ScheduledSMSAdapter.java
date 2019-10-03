package com.example.smsscheduler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ScheduledSMSAdapter extends ArrayAdapter<ScheduledSMS> {

    private Context mContext;
    private int mResource;

    public ScheduledSMSAdapter(@NonNull Context context, int resource, @NonNull List<ScheduledSMS> scheduleds) {
        super(context, resource, scheduleds);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ScheduledSMS sms = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvNumber = convertView.findViewById(R.id.number);
        TextView tvState = convertView.findViewById(R.id.state);
        TextView tvMessage = convertView.findViewById(R.id.message);
        TextView tvDate = convertView.findViewById(R.id.date);

        String message = sms.getMessage();

        if (message.length() > 13) {
            message = message.substring(0, 13) + "(...)";
        }

        tvNumber.setText("Number: " + sms.getPhoneNumber());
        tvState.setText("State: " + sms.getState().name());
        tvMessage.setText("Text: " + message);
        tvDate.setText(sms.scheduledDateToString());

        convertView.setTag(sms);

        return convertView;
    }
}
