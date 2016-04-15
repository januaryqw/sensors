package sg.edu.nus.sensors;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class ViewPictureActivity extends Activity {

    private static final String TAG = "ViewPictureActivity";
    PatternRecogManager pr;
    ImageView img;
    private static long soundTimestamp;
    private static long videoStartTimestamp;
    private static long timeLag = 1564L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);
        videoStartTimestamp  = getIntent().getLongExtra("videoStartingTime",0L);
        Toast.makeText(this, "Received VideoStartTime " + videoStartTimestamp, Toast.LENGTH_LONG).show();

        pr = new PatternRecogManager();
        soundTimestamp = pr.getTimeStampMaxCorrelation(this);

        img = (ImageView) findViewById(R.id.img);

        File sdcard = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MySensorApp");
        File file = new File(sdcard + File.separator +
                "VID_TEST" + ".mp4");

        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();

        try {
            retriever.setDataSource(file.getAbsolutePath());
            long timeStamp = soundTimestamp - videoStartTimestamp + timeLag;
            Bitmap bitmap= retriever.getFrameAtTime(timeStamp, retriever.OPTION_CLOSEST_SYNC);
            img.setImageBitmap(bitmap);
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
}
