package com.abungo.batteryx2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import android.widget.TextView;
import android.os.Build;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {
    ProgressBar pb1,pb2,pb3;
	TextView status,percent,etr,t2f,btmp,design,estimate,bh,tv,mah,Vooc_status;
	//private BatteryReceiver mBatteryReceiver = new BatteryReceiver();
    //private IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    public static String battery_capacity = "/sys/class/power_supply/battery/capacity";
	public static String cool_down = "/sys/class/power_supply/battery/cool_down";
    public static String mmi_charging_enable = "/sys/class/power_supply/battery/mmi_charging_enable";
    public static String battery_temperature = "/sys/class/power_supply/battery/temp";
	public static String input_suspend = "/sys/class/power_supply/battery/input_suspend";
	public static String total_mah = "/sys/class/power_supply/battery/charge_full";
	public static String charge_counter = "/sys/class/power_supply/battery/charge_counter";
	public static String c_status = "/sys/class/power_supply/battery/status";
	public static String cycle = "/sys/class/power_supply/battery/cycle_count";
	public static String vooc = "/sys/class/power_supply/battery/voocchg_ing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		mah=findViewById(R.id.mah);
		status=findViewById(R.id.status);
		percent=findViewById(R.id.percent);
		etr=findViewById(R.id.t2f);
		t2f=findViewById(R.id.etr);
		btmp=findViewById(R.id.temperature);
		pb2=findViewById(R.id.pb2);
		pb1=findViewById(R.id.pb1);
		design=findViewById(R.id.design);
		estimate=findViewById(R.id.estimate);
		bh=findViewById(R.id.bh);
		pb3=findViewById(R.id.progress_bar);
		Vooc_status=findViewById(R.id.vooc_status);
		
		new Thread(){
			int count=0;
			@Override
			public void run(){
				while(!isInterrupted()){
					try{
						runOnUiThread(new Runnable(){
								@Override
								public void run(){
									BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
									Integer c_status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
									Integer cu = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
									Integer ca = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
									
									if(cu<0){
										cu=cu*(-1);
									}
									Integer q = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
									Integer percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
									//Integer energy = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
									long time;
									time = bm.computeChargeTimeRemaining()/60000;
									Integer dc =(q/percentage)/10;
									Integer ec = Integer.parseInt(readLine(total_mah));
									float ecd = -ec;
									float cad = ca/10000000;
									float tr = ecd/cad;
									Integer bhealth=(ec*100)/dc;
									if(c_status==2){
										status.setText("Charging");
									}
									else{
										status.setText("Discharging");
									}
									Integer Vooc=Integer.parseInt(readLine(vooc));
									mah.setText(cu/1000+" Ma\n"+percentage+" %");
									if(Vooc==1 && c_status==2){
										Vooc_status.setText("Vooc Charging");
										etr.setText(String.valueOf(time)+" Mins");
										t2f.setText("Time to Full Charge");
										Vooc_status.setTextColor(Color.GREEN);
									}
									else if(Vooc==0 && c_status==2){
										Vooc_status.setText("Charging Slowly");
										t2f.setText("Time to Full Charge");
										etr.setText(String.valueOf(time)+" Mins");
										Vooc_status.setTextColor(Color.rgb(255,165,5));
									}
									else{
										Vooc_status.setText("Discharging");
										t2f.setText("Estimated Time Left");
										etr.setText(String.format("%.2f",tr) +" Hrs");
										Vooc_status.setTextColor(Color.RED);
									}
									float tmp =  Float.parseFloat(readLine(battery_temperature))/10;
									btmp.setText(String.valueOf(tmp)+"°C");
									percent.setText(String.valueOf(percentage)+"%");
									pb2.setProgress(percentage);
									pb3.setProgress(percentage);
									bh.setText(String.valueOf(bhealth)+"%");
									estimate.setText(String.valueOf(ec)+" Mah");
									pb1.setProgress(bhealth);
									design.setText(String.valueOf(dc)+" Mah");
								}
							});
						Thread.sleep(500);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
	}
    //Util
	public static String readLine(String filename) {
        if (filename == null) {
            return null;
        }
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new FileReader(filename), 1024);
            line = br.readLine();
        } catch (IOException e) {
            return "000";
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return line;
    }
	
	protected void onResume()
    {
        super.onResume();
        //registerReceiver(mBatteryReceiver,mIntentFilter);
    }

    @Override
    protected void onPause()
    {
        //unregisterReceiver(mBatteryReceiver);
        super.onPause();
    }
	
	public static int getBatteryPercentage(Context context) {
		BatteryManager bm = ( BatteryManager) context.getSystemService(BATTERY_SERVICE);
		return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }
}
