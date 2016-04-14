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
        System.out.println("correlation: "+correlation.length);
        double max = getMagnitude(correlation[0], correlation[1]);
        int max_index = 0;
        for (int i = 0; i < correlation.length; i ++) {
            if (i % 2 == 0) {
                double value = getMagnitude(correlation[i], correlation[i + 1]);
                if (value > max) {
                    max = value;
                    max_index = i;
                }
            }
        }
        System.out.println("max: " + max + " index: " + max_index + " timestamp: " + pr.timestamps.get((int) Math.ceil(max_index / 2 / 1280)));
    }
    public void goToGetCheeseSample(View view){
        myIntent = new Intent(this, GetCheeseSample.class);
        startActivity(myIntent);
    }
    public void goToAudioVideoActivity(View view){
        myIntent = new Intent(this, AudioVideoActivity.class);
        startActivity(myIntent);
    }
    private double getMagnitude(double real, double imag){
        return Math.log(real * real + imag * imag);
    }
}


