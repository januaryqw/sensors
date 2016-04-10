package sg.edu.nus.sensors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    Intent myIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //PatternRecogManager pr = new PatternRecogManager();
        //System.out.println(pr.getRecordedAudioList(this,1));
    }

    //camera
    public void goToCamera(View view){
        myIntent = new Intent(this, CameraActivity.class);
        startActivity(myIntent);
    }

    //accelerometer
    public void goToAccelerometer(View view){
        myIntent = new Intent(this, AccelerometerActivity.class);
        startActivity(myIntent);
    }

    public void getAudioAndAccelerometerData(View view){
        myIntent = new Intent(this, GatherDataActivity.class);
        startActivity(myIntent);
    }

    public void goToDataReader(View view){
        myIntent = new Intent(this, DataReaderScrollingActivity.class);
        startActivity(myIntent);
    }
    public void goToPatternRecog(View view){
        PatternRecogManager pr = new PatternRecogManager();
        double [] correlation =  pr.getCorrelation(this);
        for (int i = 0; i < correlation.length; i ++) {
            System.out.print(correlation[i] + " ");
        }
    }
    public void goToGetCheeseSample(View view){
        myIntent = new Intent(this, GetCheeseSample.class);
        startActivity(myIntent);
    }

}


