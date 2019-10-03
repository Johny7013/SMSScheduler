package com.example.smsscheduler;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final long ONE_MINUTE = 60000;

    Calendar date_and_time = new GregorianCalendar();
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForPermission();

        dbHelper = new DatabaseHelper(this);

        setDateAndTime((TextView) findViewById(R.id.tv_date), Calendar.getInstance().getTime());
    }

    // Date & time

    private void setDateAndTime(TextView tv, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_and_time_pattern));
        tv.setText(sdf.format(date));
    }

    public void pickDate(View view) {
        int day, month, year;

        date_and_time = Calendar.getInstance();

        day = date_and_time.get(Calendar.DAY_OF_MONTH);
        month = date_and_time.get(Calendar.MONTH);
        year = date_and_time.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this,
                year, month, day);

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        date_and_time.set(Calendar.YEAR, i);
        date_and_time.set(Calendar.MONTH, i1);
        date_and_time.set(Calendar.DAY_OF_MONTH, i2);

        int minute, hour;

        minute = date_and_time.get(Calendar.MINUTE);
        hour = date_and_time.get(Calendar.HOUR_OF_DAY);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, MainActivity.this,
                hour, minute, true);

        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        date_and_time.set(Calendar.HOUR_OF_DAY, i);
        date_and_time.set(Calendar.MINUTE, i1);

        setDateAndTime((TextView) findViewById(R.id.tv_date), date_and_time.getTime());
    }

    // On Click Send Button

    public void prepareSMSToSend(View view) {

        EditText editTextPhoneNumber = (EditText) findViewById(R.id.tv_phone_number);
        String phoneNumber = editTextPhoneNumber.getText().toString();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(MainActivity.this, "Wrong Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (date_and_time.getTimeInMillis() + ONE_MINUTE - Calendar.getInstance().getTimeInMillis() < 0){
            Toast.makeText(MainActivity.this, "Cannot schedule SMS on past", Toast.LENGTH_SHORT).show();
        }
        else {
            EditText editTextSMSMessage = (EditText) findViewById(R.id.et_sms_message);
            String SMSMessage = editTextSMSMessage.getText().toString();

            long delay = date_and_time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() - ONE_MINUTE / 6;

            Data smsData = new Data.Builder()
                    .putString("phoneNumber", phoneNumber)
                    .putString("SMSMessage", SMSMessage)
                    .build();

            OneTimeWorkRequest sendSMSWorkRequest = new OneTimeWorkRequest.Builder(SendSMSWorker.class)
                    .setInputData(smsData)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build();

            String id = sendSMSWorkRequest.getId().toString();

            boolean addToDbResult = dbHelper.addSMS(id, phoneNumber, date_and_time.getTimeInMillis(),
                    SMSMessage, ScheduledSMSState.SCHEDULED);

            if (addToDbResult) {
                WorkManager.getInstance().enqueue(sendSMSWorkRequest);

                Toast.makeText(MainActivity.this, "SMS scheduled successfully", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Error during scheduling SMS", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Show list

    public void showScheduledSMSList(View view) {
        Intent intent = new Intent(MainActivity.this, ScheduledSMSListActivity.class);
        startActivity(intent);
    }

    // Permisions

    private void checkForPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            Log.d("TAG", "PERMISSION NOT GRANTED!");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // permission wasn't granted
                    finish();
                }
            }
        }
    }

}
