package com.example.smsscheduler;

import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;

public class SendSMSWorker extends Worker {
    DatabaseHelper dbHelper;

    public SendSMSWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);

        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public Result doWork() {

        boolean updatedSuccessfully = dbHelper.updateState(this.getId().toString(), ScheduledSMSState.SENT);

        if (updatedSuccessfully) {
            String scAddress = null;
            ArrayList<PendingIntent> sentIntent = null, deliveryIntent = null;

            SmsManager smsManager = SmsManager.getDefault();

            try {
                ArrayList<String> msgs = smsManager.divideMessage(getInputData().getString("SMSMessage"));

                smsManager.sendMultipartTextMessage(getInputData().getString("phoneNumber"),
                        scAddress, msgs, sentIntent, deliveryIntent);
            } catch (Exception e) {
                dbHelper.updateState(this.getId().toString(), ScheduledSMSState.BROKEN);
                return Result.failure();
            }

            return Result.success();
        }
        else {
            return Result.failure();
        }
    }

}
