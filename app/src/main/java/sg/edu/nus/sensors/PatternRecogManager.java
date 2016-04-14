package sg.edu.nus.sensors;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class PatternRecogManager {

    private static final String TAG = "ViewPictureActivity";
    ArrayList<Long> timestamps = new ArrayList<Long>();

    public long getTimeStampMaxCorrelation(Context context){
        double [] correlation =  this.getCorrelation(context);
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
        Log.d(TAG, "max: " + max + " index: " + max_index
                + " timestamp: " + this.timestamps.get((int) Math.ceil(max_index / 2 / 1280)));

        return this.timestamps.get((int) Math.ceil(max_index / 2 / 1280));
    }


    public long getTimeStampMaxValue(Context context){
        double[] recorded_hn = getRecordedAudioList(context, 1);
        double max = 0;
        int max_index = 0;
        for (int i = 0; i < recorded_hn.length; i ++){
            if (max > recorded_hn[i]){
                max = recorded_hn[i];
                max_index = i;
            }
        }
        return timestamps.get((int) Math.ceil(max_index/1280));
    }

    public long getTimeStampMaxValue(Context context, int fileIndex){
        double[] recorded_hn = getRecordedAudioList(context, fileIndex);
        double max = 0;
        int max_index = 0;
        for (int i = 0; i < recorded_hn.length; i ++){
            if (max > recorded_hn[i]){
                max = recorded_hn[i];
                max_index = i;
            }
        }
        return timestamps.get((int) Math.ceil(max_index/1280));
    }


    public double[] getCorrelation(Context context){
        double[] recorded_hn = getRecordedAudioList(context, 1);
        double max = 0;
        int max_index = 0;
        for (int i = 0; i < recorded_hn.length; i ++){
            if (max > recorded_hn[i]){
                max = recorded_hn[i];
                max_index = i;
            }
        }
        Log.d(TAG, "max volumn is "+max+" at"+timestamps.get((int) Math.ceil(max_index/1280)));
        double[] sample_xn = getRecordedAudioList(context, 2);
        Log.d(TAG, "pattern RecogManager recorded_hn length: " + recorded_hn.length);
        Log.d(TAG, "pattern RecogManager sample_xn length: " + sample_xn.length);
        double[] hn = convertRealToImaginary(recorded_hn);
        double[] xn = convertRealToImaginary(sample_xn);
        int length = getLengthOfPadded(hn,xn);
        double[] FFT_Hk = getFFT(hn, length);
        double[] FFT_Xk = getFFT(xn, length);
        double[] x_conjugate = getComplexConjugate(FFT_Xk);
        double[] G_k = getPointMultiplication(FFT_Hk,x_conjugate);
        return getInverseFFT(G_k);
    }


    private double[] getFFT(double[] imaginaryValues, int lengthAfterPadding){
        double[] paddedValues = new double[lengthAfterPadding];
        for (int i = 0; i < imaginaryValues.length; i ++){
            paddedValues[i] = imaginaryValues[i];
        }

        int FFT_Len = paddedValues.length/2;
        double[] soundFFTTemp = new double[FFT_Len*2];

        // fft
        DoubleFFT_1D fft = new DoubleFFT_1D(FFT_Len);
        fft.complexForward(paddedValues);

        // perform fftshift here
        for (int i=0; i<FFT_Len; i++) {
            soundFFTTemp[i]         = paddedValues[i+FFT_Len];
            soundFFTTemp[i+FFT_Len] = paddedValues[i];
        }
        for (int i=0; i<FFT_Len*2; i++) {
            paddedValues[i] = soundFFTTemp[i];
        }
        return paddedValues;
    }
    private double[] getInverseFFT(double[] G){
        double[] FFT = getFFT(G, getNextPowerOfTwo(G.length));
        int size = FFT.length;
        double[] result = new double[size];
        for (int i = 0; i < FFT.length; i ++){
            if (i %2 == 0){
                result[i] = FFT[i];
            }
            else {
                result[i] = -FFT[i];
            }
        }
        return result;
    }
    private double[] getRecordedAudioList(Context context, int option){
        try {
            String filename;
            if (option == 1){
                filename = "audio.txt";
            }
            else if(option == 2) {
                filename = "cheese.txt";
            }
            else if(option == 3){
                filename = "audio_for_accelerometer.txt";
            }else{
                filename = "";
            }
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sensors/");
            File file = new File(dir, filename);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String s = "";
            while((line = br.readLine())!=null){
                String[] tokens = line.split("\t");
                long timestamp = Long.parseLong(tokens[0]);
                if (option == 1){
                    timestamps.add(timestamp);
                }
                String valuesString = tokens[1].trim();
                s += valuesString+" ";
            }
            String[] valueStringArray = s.split(" ");
            int size = valueStringArray.length;
            double[] values = new double[size];
            for(int i = 0; i < size; i++) {
                if (valueStringArray[i].startsWith("-")){
                    values[i] = 0 - Integer.parseInt(valueStringArray[i].substring(1).trim());
                }
                else{
                    values[i] = Integer.parseInt(valueStringArray[i].trim());
                }
            }
            if(values == null){
                Log.d(TAG, "Reading Values fail");
            }
            return values;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getNextPowerOfTwo(int n)
    {
        double y = Math.ceil(Math.log(n)/Math.log(2));
        return (int) Math.pow(2,y);
    }
    private int getLengthOfPadded(double[] signal1, double[] signal2){
        return getNextPowerOfTwo(signal1.length+signal2.length -1);
    }
    private double[] getComplexConjugate(double[] FFT){
        int size = FFT.length;
        double[] result = new double[size];
        for(int i = 0; i < size; i ++){
            if(i % 2 == 0){
                result[i] = FFT[i];
            }
            else {
                result[i] = -FFT[i];
            }
        }
        return result;
    }
    private double[] getPointMultiplication(double[] H, double[] Xconjugate){
        int length1 = H.length;
        int length2 = Xconjugate.length;
        double[] result = new double[length1];
        int lengthOfZero = (length1 - length2)/2;
        Log.d(TAG, "length of 1: " + length1 + ", length of 2: " + length2);
        //(a+bi)(c+di) = (ac -bd) + (ad + bc)i
        for (int i = lengthOfZero; i < length2 + lengthOfZero; i = i+2 ){
            //ac-bd
            double a = H[i];
            double b = H[i+1];
            double c = Xconjugate[i-lengthOfZero];
            double d = Xconjugate[i-lengthOfZero+1];
            result[i] = a*c - b*d;
            //ad+bc
            result[i+1] = a*d + b*c;
        }
        return result;
    }

    private double getMagnitude(double real, double imag){
        return Math.log(real * real + imag * imag);
    }

    private double[] convertRealToImaginary(double[] values){
        int FFT_Len = values.length;

        double[] soundFFT = new double[FFT_Len*2];
        int segmentIndex = 0;
        while (segmentIndex < FFT_Len) {
            soundFFT[2*segmentIndex] = (double)values[segmentIndex];
            soundFFT[2*segmentIndex+1] = 0.0;
            segmentIndex++;
        }
        return soundFFT;
    }
}
