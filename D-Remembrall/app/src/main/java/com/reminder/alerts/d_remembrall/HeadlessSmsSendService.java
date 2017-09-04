package com.reminder.alerts.d_remembrall;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by hp on 8/22/2017.
 */

public class HeadlessSmsSendService extends IntentService {
    public HeadlessSmsSendService() {
        super(HeadlessSmsSendService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}