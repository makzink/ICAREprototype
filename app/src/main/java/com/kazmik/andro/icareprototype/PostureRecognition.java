package com.kazmik.andro.icareprototype;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class PostureRecognition extends Activity implements SensorEventListener {

    public final static String EXTRA_MESSAGE = "com.example.sensefall.MESSAGE";
    public double ax,ay,az;
    public double a_norm,v_sum;
    public int i=0,flag=0;
    static int BUFF_SIZE=50;
    static public double[] window = new double[BUFF_SIZE];
    double sigma=0.5,th=10,th1=5,th2=2;
    private SensorManager sensorManager;
    public static String curr_state,prev_state;
    public MediaPlayer m1_fall,m2_sit,m3_stand,m4_walk;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posture_recognition);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        initialize();
    }
    private void initialize() {
        // TODO Auto-generated method stub
        for(i=0;i<BUFF_SIZE;i++){
            window[i]=0;
        }
        prev_state="none";
        curr_state="none";
        m1_fall=MediaPlayer.create(getBaseContext(), R.raw.fall);
        m2_sit=MediaPlayer.create(getBaseContext(), R.raw.sitting);
        m3_stand=MediaPlayer.create(getBaseContext(), R.raw.standing);
        m4_walk=MediaPlayer.create(getBaseContext(), R.raw.walking);


    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @SuppressLint("ParserError")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
            v_sum= Math.sqrt((ax*ax)+(ay*ay)+(az*az));
            AddData(ax,ay,az);
            posture_recognition(window,ay);
            SystemState(curr_state,prev_state);
            if(!prev_state.equalsIgnoreCase(curr_state)){
                prev_state=curr_state;
            }

        }
    }
    private void posture_recognition(double[] window2,double ay2) {
        // TODO Auto-generated method stub
        int zrc=compute_zrc(window2);
        if(zrc==0){

            if(Math.abs(ay2)<th1){
                curr_state="sitting";
            }else{
                curr_state="standing";
            }
            flag=0;

        }else{

            if(zrc>th2){
                curr_state="walking";
                flag=0;
            }else{

                curr_state="none";
                if(v_sum<3)
                {
                    flag=1;
                }
                if(v_sum>17&&flag==1)
                {
                    flag =2;
                }
                if(v_sum>3&&v_sum<15&&flag==2)
                {

                    curr_state="fall";
                }
            }


        }




    }
    private int compute_zrc(double[] window2) {
        // TODO Auto-generated method stub
        int count=0;
        for(i=1;i<=BUFF_SIZE-1;i++){

            if((window2[i]-th)<sigma && (window2[i-1]-th)>sigma){
                count=count+1;
            }

        }
        return count;
    }
    private void SystemState(String curr_state1,String prev_state1) {
        // TODO Auto-generated method stub

        //Fall !!
        if(!prev_state1.equalsIgnoreCase(curr_state1)){
            if(curr_state1.equalsIgnoreCase("fall")){
                m1_fall.start();
                Toast.makeText(PostureRecognition.this,"FALLING",Toast.LENGTH_SHORT).show();
            }
            if(curr_state1.equalsIgnoreCase("sitting")){
                m2_sit.start();
                Toast.makeText(PostureRecognition.this,"SITTING",Toast.LENGTH_SHORT).show();
            }
            if(curr_state1.equalsIgnoreCase("standing")){
                m3_stand.start();
                Toast.makeText(PostureRecognition.this,"STANDING",Toast.LENGTH_SHORT).show();
            }
            if(curr_state1.equalsIgnoreCase("walking")){
                m4_walk.start();
                Toast.makeText(PostureRecognition.this,"WALKIING",Toast.LENGTH_SHORT).show();
            }
        }


    }
    private void AddData(double ax2, double ay2, double az2) {
        // TODO Auto-generated method stub
        a_norm=Math.sqrt(ax*ax+ay*ay+az*az);
        for(i=0;i<=BUFF_SIZE-2;i++){
            window[i]=window[i+1];
        }
        window[BUFF_SIZE-1]=a_norm;

    }

    public void exit_app(View view){

        finish();


    }



}
