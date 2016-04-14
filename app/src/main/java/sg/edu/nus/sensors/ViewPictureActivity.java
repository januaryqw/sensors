package sg.edu.nus.sensors;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

import wseemann.media.FFmpegMediaMetadataRetriever;


public class ViewPictureActivity extends Activity {

    private static final String TAG = "ViewPictureActivity";
    ImageView img1;
    private static long soundTimestamp;
    private static long timeLag;
    private static long videoStartTimestamp;
    PatternRecogManager pr = new PatternRecogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);
        videoStartTimestamp  = getIntent().getLongExtra("videoStartingTime",0L);
        Toast.makeText(this, "Received VideoStartTime " + videoStartTimestamp, Toast.LENGTH_LONG).show();

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
        String timeStampForMaxCorrelation = pr.timestamps.get((int) Math.ceil(max_index / 2 / 1280));
        soundTimestamp = Long.parseLong(timeStampForMaxCorrelation);

        img1 = (ImageView) findViewById(R.id.img);

        File sdcard = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MySensorApp");
        File file = new File(sdcard + File.separator +
                "VID_TEST" + ".mp4");

        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();

        try {
            retriever.setDataSource(file.getAbsolutePath());
            long timeStamp = soundTimestamp - videoStartTimestamp
            Bitmap bitmap2= retriever.getFrameAtTime(timeStamp2, retriever.OPTION_CLOSEST_SYNC);

            img1.setImageBitmap(bitmap1);
            img2.setImageBitmap(bitmap2);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }

    }
    public void goToMainActivity(View view){
        finish();
    }

    private double getMagnitude(double real, double imag){
        return Math.log(real * real + imag * imag);
    }




}
