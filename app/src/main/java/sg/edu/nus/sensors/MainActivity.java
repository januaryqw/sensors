package sg.edu.nus.sensors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    Intent myIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getAudioAndAccelerometerData(View view){
        myIntent = new Intent(this, AudioAccelerometerActivity.class);
        startActivity(myIntent);
    }

    public void goToDataReader(View view){
        myIntent = new Intent(this, DataReaderScrollingActivity.class);
        startActivity(myIntent);
    }

    public void goToPatternRecog(View view){
        PatternRecogManager pr = new PatternRecogManager();
        double [] correlation =  pr.getCorrelation(this);
        Log.d(TAG, "correlation: "+correlation.length);
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
        int timeIndex = (int) Math.ceil(max_index / 2 / 1280);
        if (pr.timestamps.size() > timeIndex) {
            Log.d(TAG, "max correlation: " + max
                    + " index of max: " + max_index
                    + " timestamp: " + pr.timestamps.get(timeIndex));
            Toast.makeText(getApplicationContext(),
                    "max correlation: " + max
                            + " index of max: " + max_index
                            + " timestamp: " + pr.timestamps.get(timeIndex),
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Record longer to get the result",
                    Toast.LENGTH_LONG).show();
        }
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


