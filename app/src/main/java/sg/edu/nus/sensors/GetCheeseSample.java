package sg.edu.nus.sensors;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class GetCheeseSample extends Activity{


    private sg.edu.nus.sensors.SoundSampler soundSampler;
    private File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gather_data);

        try {
            this.dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");
            if (!dir.exists()){
                if(dir.mkdirs()){
                    System.out.println("mkdir succeed.");
                }
                else{
                    System.out.println("cannot mkdir");
                }
            }

            soundSampler = new sg.edu.nus.sensors.SoundSampler(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        try {
            this.dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");
            soundSampler.init(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/cheese.txt");
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
}
