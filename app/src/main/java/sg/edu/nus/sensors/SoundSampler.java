package sg.edu.nus.sensors;

import android.media.AudioRecord;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ngtk on 4/2/16.
 */

public class SoundSampler {
    private static final int  FS = 16000;     // sampling frequency
    public AudioRecord audioRecord;
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    private Thread            recordingThread;

    public SoundSampler(sg.edu.nus.sensors.SoundSamplingActivity mAct) throws Exception
    {
        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release(); // not join
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));
        }
        catch (Exception e) {
            Log.d("Error in SoundSampler ", e.getMessage());
            throw new Exception();
        }

        return;

    }


    public void init() throws Exception
    {
        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));
        }
        catch (Exception e) {
            Log.d("Error in Init() ", e.getMessage());
            throw new Exception();
        }

        SoundSamplingActivity.bufferSize = AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding);
        SoundSamplingActivity.buffer = new short[SoundSamplingActivity.bufferSize];

        audioRecord.startRecording();

        recordingThread = new Thread()
        {
            public void run()
            {
                while (true)
                {
                    long milliseconds = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
                    Date resultDate = new Date(milliseconds);

                    String ts = sdf.format(resultDate);
                    System.out.println("Audio: " + ts + ' ' + SoundSamplingActivity.bufferSize);

                    audioRecord.read(SoundSamplingActivity.buffer, 0, SoundSamplingActivity.bufferSize);
                    SoundSamplingActivity.surfaceView.drawThread.setBuffer(SoundSamplingActivity.buffer);

                }
            }
        };
        recordingThread.start();

        return;



    }



}
