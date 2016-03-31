package sg.edu.nus.cs3218tut_qiaowei;

import android.media.AudioRecord;
import android.util.Log;

/**
 * Created by ngtk on 4/2/16.
 */

public class SoundSampler {
    private static final int  FS = 16000;     // sampling frequency
    public AudioRecord audioRecord;
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    private Thread            recordingThread;

    public SoundSampler(sg.edu.nus.cs3218tut_qiaowei.SoundSamplingActivity mAct) throws Exception
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

        sg.edu.nus.cs3218tut_qiaowei.SoundSamplingActivity.bufferSize = AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding);
        sg.edu.nus.cs3218tut_qiaowei.SoundSamplingActivity.buffer = new short[sg.edu.nus.cs3218tut_qiaowei.SoundSamplingActivity.bufferSize];

        audioRecord.startRecording();

        recordingThread = new Thread()
        {
            public void run()
            {
                while (true)
                {

                    audioRecord.read(sg.edu.nus.cs3218tut_qiaowei.SoundSamplingActivity.buffer, 0, SoundSamplingActivity.bufferSize);
                    sg.edu.nus.cs3218tut_qiaowei.SoundSamplingActivity.surfaceView.drawThread.setBuffer(sg.edu.nus.cs3218tut_qiaowei.SoundSamplingActivity.buffer);

                }
            }
        };
        recordingThread.start();

        return;



    }



}
