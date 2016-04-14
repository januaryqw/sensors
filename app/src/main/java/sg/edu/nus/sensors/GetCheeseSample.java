package sg.edu.nus.sensors;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GetCheeseSample extends Activity{


    private sg.edu.nus.sensors.SoundSampler soundSampler;
    private File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gather_data);

        try {
            this.dir = new File (getApplicationInfo().dataDir + "/Sensors/");
            if (!dir.exists()){
                if(dir.mkdirs()){
                    System.out.println("mkdir succeed.");
                }
                else{
                    System.out.println("cannot mkdir");
                }
            }

            soundSampler = new sg.edu.nus.sensors.SoundSampler(getApplicationInfo().dataDir + "/Sensors/");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        try {
            this.dir = new File (getApplicationInfo().dataDir + "/Sensors/");
            soundSampler.init(getApplicationInfo().dataDir + "/Sensors/cheese.txt");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Cannot initialize SoundSampler.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStop() {
        soundSampler.stop();
        try {
            //this.bwForAccelerometer.close();
            //this.fwForAccelerometer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }
    public void goToMainActivity(View view){
        finish();
    }

    private void writeToFile(String data) {
        try {
            File dir = new File (getApplicationInfo().dataDir + "/Sensors/");

            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("mkdir succeed.");
                } else {
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
