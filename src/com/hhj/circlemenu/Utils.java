
package com.hhj.circlemenu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class Utils {

    public static void playRemindVoice(Context context, RemindInfo remindInfo) {
        Log.i("debug", "=====Util=============playRemindVoice=====");

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File remindVoiceFile = new File(Environment.getExternalStorageDirectory() +
                    "/health/remindVoice/" + "我终于失去了你_new.amr");
            if (remindVoiceFile.exists()) {
                Intent playRemindVoiceIntent = new Intent();
                playRemindVoiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                playRemindVoiceIntent.setAction("android.intent.action.REMIND_AUDIO");
                playRemindVoiceIntent.putExtra("remindName", "记得吃药");
                playRemindVoiceIntent.putExtra("remindTime", System.currentTimeMillis());
                playRemindVoiceIntent.setDataAndType(Uri.fromFile(remindVoiceFile),
                        "audio/amr");
                context.startActivity(playRemindVoiceIntent);
            } else {
                Toast.makeText(context, "语音文件不存在",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "sdcard卡错误", Toast.LENGTH_LONG).show();
        }

    }
}
