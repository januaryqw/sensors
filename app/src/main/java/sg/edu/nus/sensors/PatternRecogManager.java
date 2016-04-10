package sg.edu.nus.sensors;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by zhang_000 on 2016-04-10.
 */
public class PatternRecogManager {
    public double[] getCorrelation(Context context){
        double[] recorded_hn = getRecordedAudioList(context, 1);
        double[] sample_xn = getRecordedAudioList(context, 2);
        double[] FFT_Hk = getFFT(recorded_hn);
        double[] FFT_Xk = getFFT(sample_xn);
        double[] x_conjugate = getComplexConjugate(FFT_Xk);
        double[] G_k = getPointMultiplication(FFT_Hk,x_conjugate);
        return getInverseFFT(G_k);
    }
    public double[] getRecordedAudioList(Context context, int option){
        try {
            String filename;
            if (option == 1){
                filename = "audio.txt";
            }
            else {
                filename = "cheese.txt";
            }
            File dir = new File(context.getApplicationInfo().dataDir + "/Sensors/");
            File file = new File(dir, filename);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String s = "";
            while((line = br.readLine())!=null){
                String[] tokens = line.split("\t");
                String timestamp = tokens[0];
                String valuesString = tokens[1].trim();
                s += valuesString+" ";
            }
            String[] valueStringArray = s.split(" ");
            int size = valueStringArray.length;
            double[] values = new double[size];
            for(int i = 0; i < size; i++) {
                if (valueStringArray[i].startsWith("-")){
                    values[i] = Integer.parseInt(valueStringArray[i].substring(1));
                }
                else{
                    values[i] = Integer.parseInt(valueStringArray[i]);
                }
            }
           return values;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public double[] getFFT(double[] values){

        int FFT_Len = values.length/2;

        double[] soundFFT = new double[FFT_Len*2];
        double[] soundFFTTemp = new double[FFT_Len*2];
        int segmentIndex = 0;
        while (segmentIndex < FFT_Len) {
            soundFFT[2*segmentIndex] = (double)values[segmentIndex];
            soundFFT[2*segmentIndex+1] = 0.0;
            segmentIndex++;
        }
        // fft
        DoubleFFT_1D fft = new DoubleFFT_1D(FFT_Len);
        fft.complexForward(soundFFT);


        // perform fftshift here
        for (int i=0; i<FFT_Len; i++) {
            soundFFTTemp[i]         = soundFFT[i+FFT_Len];
            soundFFTTemp[i+FFT_Len] = soundFFT[i];
        }
        for (int i=0; i<FFT_Len*2; i++) {
            soundFFT[i] = soundFFTTemp[i];
        }
        return soundFFT;
    }
    public double[] getComplexConjugate(double[] FFT){
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
    public double[] getPointMultiplication(double[] H, double[] Xconjugate){
        int length1 = H.length;
        int length2 = Xconjugate.length;
        double[] result = new double[length1];
        int lengthOfZero = (length1 - length2)/2;
        System.out.println("length of 1: "+length1+", length of 2: "+length2);
        for (int i = lengthOfZero; i < length2 + lengthOfZero; i ++ ){
            result[i] = H[i] * Xconjugate[i - lengthOfZero];
        }
        return result;
    }
    public double[] getInverseFFT(double[] G){
        double[] FFT = getFFT(G);
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
}
