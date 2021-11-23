package com.abungo.batteryx2;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ImageView;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        TextView status=((MainActivity)context).findViewById(R.id.estimate);
        TextView percent=((MainActivity)context).findViewById(R.id.design);
        ImageView image=((MainActivity)context).findViewById(R.id.image);
        String action = intent.getAction();
        
        if(action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)){
           
            int level =intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_BATTERY_LOW,-1);
            int percentage = level*100/scale;
            percent.setText(String.valueOf(scale)+"%");
            int avc=intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);
            status.setText(String.valueOf(avc/10)+" °C");
        }
    }

}
