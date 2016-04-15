package sg.edu.nus.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioAccelerometerActivity extends Activity implements SensorEventListener{
    // audio_for_accelerometer.txt
    // accelerometer.txt
    private static final String TAG = "ViewPictureActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private sg.edu.nus.sensors.SoundSampler soundSampler;
    private File dir;
    private File fileForAccelerometer;
    private FileWriter fwForAccelerometer;
    private BufferedWriter bwForAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_accelerometer);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        try {
            this.dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");
            if (!dir.exists()){
                if(dir.mkdirs()){
                    Log.d(TAG, "\"mkdir succeed.\"");
                }
                else{
                    Log.d(TAG, "cannot mkdir");
                }
            }
            this.fileForAccelerometer = new File(dir, "accelerometer.txt");
            this.fwForAccelerometer = new FileWriter(this.fileForAccelerometer);
            this.bwForAccelerometer = new BufferedWriter(this.fwForAccelerometer);

            soundSampler = new sg.edu.nus.sensors.SoundSampler(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        try {
            this.dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");
            this.fileForAccelerometer = new File(dir, "accelerometer.txt");
            this.fwForAccelerometer = new FileWriter(this.fileForAccelerometer);
            this.bwForAccelerometer = new BufferedWriter(this.fwForAccelerometer);
            soundSampler.init(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/audio_for_accelerometer.txt");

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Cannot initialize SoundSampler.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener((SensorEventListener) this);

        try {
            soundSampler.stop();
            this.bwForAccelerometer.close();
            this.fwForAccelerometer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onSensorChanged( SensorEvent event ) {

        // NOTE: Sensor callbacks are in the main UI thread, so do not
        //  do very long calculations here. A better approach would be
        //  to just store the values, and use a periodic timer to
        //  process them in another thread.

        float[] values = new float[3];
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            values = event.values;
        }
        String oneReading = "";
        for(int i = 0; i < values.length; i ++){
            oneReading += values[i]+",    ";
        }
        long milliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        Date resultDate = new Date(milliseconds);
        String ts = sdf.format(resultDate);
        try {
            Date date = new Date();
            long timeStamp = date.getTime();
            bwForAccelerometer.write(oneReading + timeStamp + "\n");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void goToMainActivity(View view){
        finish();
    }
    public void stopRecording(View view) {
        sensorManager.unregisterListener((SensorEventListener) this);

        try {
            soundSampler.stop();
            this.bwForAccelerometer.close();
            this.fwForAccelerometer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void showValues(View view){
        long timeStampAcc = getTimeStampAccelerometer();
        long timeStampAud = getTimeStampAudio();
        long timeLag = timeStampAud - timeStampAcc;
        Toast.makeText(this, "timeStamp from accelerometer: " + timeStampAcc
                            +"timeStamp from audio: " + timeStampAud
                            +"timelap from audio is " + timeLag, Toast.LENGTH_LONG).show();
        Log.d(TAG, "timeStamp from accelerometer: " + timeStampAcc
                    +"timeStamp from audio: " + timeStampAud
                    +"timelap from audio is " + timeLag);
    }

    private double getAccMagnitude(double accX, double accY, double accZ){
        return Math.sqrt(accX*accX+accY*accY+accZ*accZ);
    }
    private long getTimeStampAccelerometer(){
        File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");
        File file = new File(dir, "accelerometer.txt");
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            double max = 0;
            String max_timestamp="";
            while((line = br.readLine())!=null){
                String[] tokens = line.split(",");
                double magnitude = getAccMagnitude(Double.parseDouble(tokens[0]),
                                                   Double.parseDouble(tokens[1]),
                                                   Double.parseDouble(tokens[2]));
                if (magnitude > max){
                    max = magnitude;
                    max_timestamp = tokens[3];
                }
            }

            Log.d(TAG, "max acc is "+ max+" at "+max_timestamp);
            return Long.parseLong(max_timestamp.trim());

        }catch(Exception e){
            e.printStackTrace();
            return 0L;
        }
    }
    private long getTimeStampAudio(){
        PatternRecogManager pr = new PatternRecogManager();
        return pr.getTimeStampMaxValue(this);
    }
}
