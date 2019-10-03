package com.example.smsscheduler;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduledSMS {
    private String id;
    private String phoneNumber;
    private long time;
    private String message;
    private ScheduledSMSState state;

    public ScheduledSMS(@NonNull String id, String phoneNumber, long time, String message, ScheduledSMSState state) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.time = time;
        this.message = message;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public long getTime() {
        return time;
    }

    public String scheduledDateToString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm z");
        return sdf.format(new Date(time));
    }

    public String getMessage() {
        return message;
    }

    public ScheduledSMSState getState() {
        return state;
    }
}
