package com.example.smsscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.WorkManager;

import java.util.UUID;

public class ScheduledSMSOverviewActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    String id;
    ScheduledSMSState state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduledsms_overview);

        final TextView tvPhoneNumber = (TextView) findViewById(R.id.tv_phone_number);
        final TextView tvDate = (TextView) findViewById(R.id.tv_date);
        final TextView tvState = (TextView) findViewById(R.id.tv_status);
        final TextView tvMessage = (TextView) findViewById(R.id.tv_message);
        final Button btnCancel = (Button) findViewById(R.id.btn_cancel);

        dbHelper = new DatabaseHelper(this);

        final Intent receivedIntent = getIntent();

        id = receivedIntent.getStringExtra("id");
        state = ScheduledSMSState.valueOf(receivedIntent.getStringExtra("state"));

        tvPhoneNumber.setText(receivedIntent.getStringExtra("phoneNumber"));
        tvDate.setText(receivedIntent.getStringExtra("date"));
        tvState.setText(receivedIntent.getStringExtra("state"));
        tvMessage.setText(receivedIntent.getStringExtra("message"));
        tvMessage.setMovementMethod(new ScrollingMovementMethod());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (state != ScheduledSMSState.SCHEDULED) {
                    Toast.makeText(ScheduledSMSOverviewActivity.this,
                            "Cannot cancel " + state.toString() + " message", Toast.LENGTH_LONG).show();
                }
                else {
                    boolean updatedSuccessfully = dbHelper.updateState(id, ScheduledSMSState.CANCELED);

                    if(updatedSuccessfully) {
                        WorkManager.getInstance().cancelWorkById(UUID.fromString(id));
                        state = ScheduledSMSState.CANCELED;
                        tvState.setText(state.toString());
                        Toast.makeText(ScheduledSMSOverviewActivity.this,
                                "Scheduled SMS sending canceled successfully", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(ScheduledSMSOverviewActivity.this,
                                "Error during canceling SMS", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
