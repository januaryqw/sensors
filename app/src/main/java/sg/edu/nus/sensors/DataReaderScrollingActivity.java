package sg.edu.nus.sensors;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DataReaderScrollingActivity extends AppCompatActivity {
    private TextView contentDisplay;
    private File dir;
    private File file;
    private FileReader fr;
    private BufferedReader br;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_reader_scrolling);
        contentDisplay = (TextView) findViewById(R.id.contentDisplay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        this.dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");
        this.file = new File(dir, "accelerometer.txt");
        try{
            fr = new FileReader(this.file);
            br = new BufferedReader(fr);
            String line;
            double max = 0;
            String max_timestamp="";
            while((line = br.readLine())!=null){
                contentDisplay.append(line);
                contentDisplay.append("\n");
                String[] tokens = line.split(",");
                double magnitude = getAccMagnitude(Double.parseDouble(tokens[0]),Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
                if (magnitude > max){
                    max = magnitude;
                    max_timestamp = tokens[3];
                }
            }
            System.out.println("max acc is "+ max+" at"+max_timestamp);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Override
    protected void onStop() {
        try {
            br.close();
            fr.close();
            super.onStop();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private double getAccMagnitude(double accX, double accY, double accZ){
        return Math.sqrt(accX*accX+accY*accY+accZ*accZ);

    }

}
