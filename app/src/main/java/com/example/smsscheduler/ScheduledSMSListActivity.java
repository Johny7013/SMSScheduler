package com.example.smsscheduler;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScheduledSMSListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduledsms_list);

        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillList();
    }

    void fillList() {
        ListView listView = (ListView) findViewById(R.id.listView);

        ArrayList<ScheduledSMS> theList = new ArrayList<>();
        Cursor data = dbHelper.getScheduledSMS();

        if (data.getCount() == 0) {
            Toast.makeText(ScheduledSMSListActivity.this, "No SMS Scheduled", Toast.LENGTH_LONG).show();
        }
        else {
            while (data.moveToNext()) {
                theList.add(new ScheduledSMS(data.getString(0), data.getString(1),
                        data.getLong(2), data.getString(3), ScheduledSMSState.values()[data.getInt(4)]));
            }

            ScheduledSMSAdapter listAdapter = new ScheduledSMSAdapter(this, R.layout.scheduledsms_list_item, theList);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ScheduledSMS sms = (ScheduledSMS) view.getTag();

                    Intent overviewScreenIntent = new Intent(ScheduledSMSListActivity.this, ScheduledSMSOverviewActivity.class);

                    overviewScreenIntent.putExtra("id", sms.getId());
                    overviewScreenIntent.putExtra("phoneNumber", sms.getPhoneNumber());
                    overviewScreenIntent.putExtra("date", sms.scheduledDateToString());
                    overviewScreenIntent.putExtra("state", sms.getState().toString());
                    overviewScreenIntent.putExtra("message", sms.getMessage());

                    startActivity(overviewScreenIntent);
                }
            });
        }
    }
}
