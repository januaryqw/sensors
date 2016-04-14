package sg.edu.nus.sensors;

import android.media.AudioRecord;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SoundSampler {
    private static final String TAG = "SoundSampler";
    private static final int  FS = 16000;     // sampling frequency
    public AudioRecord audioRecord;
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    public Thread            recordingThread;
    public  static short[]  buffer;
    public  static int      bufferSize;     // in bytes
    public boolean stop;
    static File fileForAudio;
    static FileWriter fwForAudio;
    static BufferedWriter bwForAudio;
    static String path;


    public SoundSampler(String path) throws Exception
    {
        try {
            this.path = path;
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


    public void init(String path) throws Exception
    {
        try {
            this.path = path;
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

        this.bufferSize = AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding);
        this.buffer = new short[this.bufferSize];
        this.stop = false;
        this.fileForAudio = new File(path);
        Log.d(TAG, "file size before: " + fileForAudio.length());
        PrintWriter writer = new PrintWriter(this.fileForAudio);
        writer.print("");
        writer.close();
        Log.d(TAG, "file size after: " + fileForAudio.length());
        this.fwForAudio = new FileWriter(this.fileForAudio);
        this.bwForAudio = new BufferedWriter(this.fwForAudio);


        audioRecord.startRecording();

        recordingThread = new Thread()
        {
            public void run()
            {
                while (true)
                {
                    audioRecord.read(SoundSampler.buffer, 0, SoundSampler.bufferSize);

                    // the recoding timestamps
                    long milliseconds = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
                    Date resultDate = new Date(milliseconds);
                    String ts = sdf.format(resultDate);
                    Log.d(TAG, "Audio: " + ts + ' ' + SoundSampler.bufferSize);
                    for (int i=0; i<SoundSampler.bufferSize; i++) {
                        System.out.print(SoundSampler.buffer[i] + "  ");
                    }
                    System.out.println();

                    Date date = new Date();
                    long timeStamp = date.getTime();
                    try {
                        SoundSampler.bwForAudio.write(timeStamp + "\t");
                        for (int i=0; i<SoundSampler.bufferSize; i++) {
                            SoundSampler.bwForAudio.write(SoundSampler.buffer[i] + " ");
                        }
                        SoundSampler.bwForAudio.write("\n");
                    }catch(Exception e){
                        e.printStackTrace();
                        break;
                    }
                    if (stop){
                        break;
                    }
                }
            }
        };
        recordingThread.start();

        return;

    }
    public void stop(){
        try {
            stop = true;
            recordingThread.join();
            audioRecord.stop();
            // SoundSampler.bwForAudio.close();
        } catch (InterruptedException localInterruptedException) {
            System.out.println(localInterruptedException);
        } catch (Exception e){
            e.printStackTrace();
        }
    }



}
