package sg.edu.nus.sensors;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class ViewPictureActivity extends AppCompatActivity {
    private static final String TAG = "ViewPictureActivity";
    ImageView img1;
    ImageView img2;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);

        File sdcard = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MySensorApp");
        File file = new File(sdcard + File.separator +
                "VID_TEST" + ".mp4");
        ///storage/emulated/0/Pictures/MySensorApp/VID_TEST.mp4
        //file = new File(sdcard, "VID_TEST" + ".mp4");

        // MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();

        try {
            System.out.println(file.getAbsolutePath());
            retriever.setDataSource(file.getAbsolutePath());

            Date date= new Date();
            //getTime() returns current time in milliseconds
            long timeStamp = date.getTime();
            long timeStamp1 = 1460567779000L;
            long timeStamp2 = 1460567779109L;

            Bitmap bitmap1= retriever.getFrameAtTime(timeStamp1, retriever.OPTION_CLOSEST_SYNC);
            Bitmap bitmap2= retriever.getFrameAtTime(timeStamp2, retriever.OPTION_CLOSEST_SYNC);

            System.out.println(bitmap1.getHeight());
            //ImageProcessing myImageProcessing = new ImageProcessing();
            //Log.d(TAG, "are the two images correlated? : " + myImageProcessing.isCorrelated(bitmap1, bitmap2));
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
}
