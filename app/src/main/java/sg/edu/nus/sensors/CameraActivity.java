package sg.edu.nus.sensors;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class CameraActivity extends Activity {
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static int position = 0;
    VideoView mVideoView;
    MediaController media_Controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }
    public void dispatchTakeVideoIntent(View v) {
        mVideoView = (VideoView)findViewById(R.id.video_view);
        media_Controller = new MediaController(this);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            System.out.println(videoUri);
            try{
                mVideoView.setVideoURI(videoUri);
            }catch(Exception e){
                e.printStackTrace();
            }
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                public void onPrepared(MediaPlayer mediaPlayer){
                    mVideoView.seekTo(position);
                    if (position == 0){
                        mVideoView.start();
                    }else{
                        mVideoView.pause();
                    }
                }
            });
        }
    }
}
