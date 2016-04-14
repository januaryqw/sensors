package sg.edu.nus.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioVideoActivity extends Activity{
    private static final String TAG = "AudioVideoActivity";
    static Camera mCamera;
    static MediaRecorder mMediaRecorder;
    private sg.edu.nus.sensors.SoundSampler soundSampler;
    private SurfaceView mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private boolean isRecording = false;
    private long videoStartingTimestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_video);
        try {
            soundSampler = new SoundSampler(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!checkCameraHardware(this)) {
            Log.d(TAG, "No Camera on this device");
            finish();
        }

        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Log.d(TAG, "before button");
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Camera.getCameraInfo(0,new Camera.CameraInfo());
                        toggleButtonText(v, isRecording);
                        if (isRecording) {
                            //getTime() returns current time in milliseconds
                            Date date = new Date();
                            long milliseconds = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
                            Date resultDate = new Date(milliseconds);
                            String ts = sdf.format(resultDate);
                            Log.d(TAG, "MediaRecorder End Time: " + date.getTime() + "    " + ts);
                            // stop recording and release camera
                            mMediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            mCamera.lock();         // take camera access back from MediaRecorder

                            try {
                                soundSampler.stop();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            // inform the user that recording has stopped
                            // setCaptureButtonText("Capture");
                            isRecording = false;
                        } else {

                            try {
                                soundSampler.init(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/audio.txt");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // initialize video camera
                            if (prepareVideoRecorder()) {
                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording
                                //getTime() returns current time in milliseconds

                                Date date = new Date();
                                long milliseconds = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
                                Date resultDate = new Date(milliseconds);
                                String ts = sdf.format(resultDate);
                                Log.d(TAG, "MediaRecorder Start Time: " + date.getTime() + "    " + ts);
                                videoStartingTimestamp = date.getTime();
                                mMediaRecorder.start();

                                // inform the user that recording has started
                                // setCaptureButtonText("Stop");
                                isRecording = true;
                            } else {
                                // prepare didn't work, release the camera
                                releaseMediaRecorder();
                                // inform user
                                soundSampler.stop();
                            }
                        }
                    }
                }
        );

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    @Override
    protected void onStop() {
        try {
            soundSampler.stop();
            releaseMediaRecorder();
            releaseCamera();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }

    private void toggleButtonText(View v, boolean isRecording){
        Button button = (Button) v;
        if (isRecording){
            button.setText("Start Recording");
        }
        else{
            button.setText("Stop Recording");
        }
    }
    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            if (Camera.getNumberOfCameras() == 2){
                camera = Camera.open(1);
            }
            else{
                camera = Camera.open();
            }
        } catch (Exception e) {
            // cannot get camera or does not exist
            e.printStackTrace();
        }
        return camera;
    }

    android.hardware.Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private void releaseMediaRecorder(){
        Log.d(TAG, "release media recorder");
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        Log.d(TAG, "release camera");
        if (mCamera != null){
            // mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    private boolean prepareVideoRecorder(){
        Log.d(TAG, "prepare video recorder");
        mMediaRecorder = new MediaRecorder();
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        //mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        CamcorderProfile profile = CamcorderProfile.get(1, CamcorderProfile.QUALITY_HIGH);
        mMediaRecorder.setOutputFormat(profile.fileFormat);
        mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        Log.d(TAG, "Video FrameRate is " + profile.videoFrameRate); //30
        mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mMediaRecorder.setVideoEncoder(profile.videoCodec);


        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        Log.d(TAG, "get output media file");
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MySensorApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MySensorApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_TEST" + ".mp4");
            Log.d(TAG, mediaStorageDir.getPath() + File.separator +
                    "VID_TEST" + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    public void goToMainActivity(View view){
        finish();
    }

    public void goToViewPicture(View view){
        Intent myIntent;
        myIntent = new Intent(this, ViewPictureActivity.class);
        Bundle bO = new Bundle();

        bO.putLong("videoStartingTime", videoStartingTimestamp);
        myIntent.putExtras(bO);
        startActivity(myIntent);
    }
}
