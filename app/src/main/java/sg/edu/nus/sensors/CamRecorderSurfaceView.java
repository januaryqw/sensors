package sg.edu.nus.sensors;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Qiao Wei on 10/4/2016.
 */
public class CamRecorderSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;

    private Camera mCamera;
    private MediaRecorder mRecorder;
    static String path;

    public CamRecorderSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);

        mCamera = Camera.open();
        mRecorder = new MediaRecorder();

    }

    public void stop() {
        mRecorder.stop();
        mRecorder.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera.unlock();
        mRecorder.setCamera(mCamera);

        mRecorder.setPreviewDisplay(mHolder.getSurface());

        // You may want to change these
        mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mRecorder.setVideoFrameRate(20);

        // You'll definitely want to change this
        //mRecorder.setOutputFile("/mnt/sdcard/out");
        mRecorder.setOutputFile(path);


        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.e("IllegalStateException", e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        mRecorder.start();

    }


}
