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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener((SensorEventListener) this);
        super.onStop();
    }
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
            oneReading += values[i]+",";
        }
        long milliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        Date resultDate = new Date(milliseconds);

        String ts = sdf.format(resultDate);
        writeToFile(oneReading+ts+"\n");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void writeToFile(String data) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/Sensors/");
            if (!dir.exists()){
                if(dir.mkdirs()){
                    System.out.println("mkdir succeed.");
                }
                else{
                    System.out.println("cannot mkdir");
                }
            }

            File file = new File(dir, "accelerometer.txt");

            FileWriter fw = new FileWriter(file,true); //the true will append the new data
            fw.write(data);//appends the string to the file
            fw.close();

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
