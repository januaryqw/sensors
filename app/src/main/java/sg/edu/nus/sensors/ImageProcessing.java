package sg.edu.nus.sensors;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

/**
 * Created by Qiao Wei on 12/4/2016.
 */
public class ImageProcessing {

    public double[][] getCorrelationOfTwoImages(Bitmap bitmap1, Bitmap bitmap2){
        int[][] img1GrayScaleRaw = convertARGBToGrayScale(convertBitmapToARGB(bitmap1));
        int[][] img2GrayScaleRaw = convertARGBToGrayScale(convertBitmapToARGB(bitmap2));
        Pair<int[][], int[][]> adjusted = adjustSize(img1GrayScaleRaw, img2GrayScaleRaw);
        int[][] img1GrayScale = adjusted.first;
        int[][] img2GrayScale = adjusted.second;
        double[][] img1FFT = performFFTOnImageOneChannel(img1GrayScale);
        double[][] img2FFT = performFFTOnImageOneChannel(img2GrayScale);
        double[][] img2ConplexConjugate = getComplexConjugate(img2FFT);
        double[][] multiplicationResult = getPointWiseMultiplication(img1FFT, img2ConplexConjugate);
        double[][] correlation = performInverseFFTOnImageOneChannel(multiplicationResult);
        double[][] correlationMag = new double[correlation.length][correlation[0].length/2];
        for (int i = 0; i < correlationMag.length; i++) {
            for (int j = 0; j < correlationMag[0].length; j++) {
                double re = correlation[i][2 * j];
                double im = correlation[i][2 * j + 1];
                correlationMag[i][j] = Math.log(re * re + im * im + 0.001);
            }
        }
        return correlationMag;
    }

    public boolean isCorrelated(Bitmap bitmap1, Bitmap bitmap2){
        //TODO: how to check when given a matrix? the max point? threshold?
        double[][] correlation = getCorrelationOfTwoImages(bitmap1, bitmap2);
        // find the max location
        Pair<Double, int[]> result = getMaxValueAndItsLocation(correlation);
        double maxValue = result.first;
        int[] maxLocation = result.second;
        if (maxValue > 0.5){
            return true;
        }else {
            return false;
        }
    }

    public Pair<Double, int[]> getMaxValueAndItsLocation(double[][] a){
        int maxRow = 0;
        int maxCol = 0;
        double maxVal = a[0][0];

        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[i].length; j++){
                if (a[i][j] > maxVal){
                    maxRow = i;
                    maxCol = j;
                    maxVal = a[i][j];
                }
            }
        }
        return new Pair(maxVal, new int[]{maxRow,maxCol});
    }

    /*
        FFT on image to find the correlation of the two image
        We want to find the first one that has high correlation
           TODO: do we need to swap w and h?
     */
    private double[][] performFFTOnImageOneChannel(int[][] image){
        int rows = image.length;
        int cols = image[0].length;
        double[][] imageFFT = new double[rows][cols*2];
        for (int i = 0; i < rows; i++) {
            int colIndex = 0;
            while (colIndex < cols) {
                imageFFT[i][2 * colIndex] = (double)image[i][colIndex];
                imageFFT[i][2 * colIndex + 1] = 0.0;
                colIndex++;
            }
        }
        // fft
        DoubleFFT_2D fft = new DoubleFFT_2D(rows,cols);
        fft.complexForward(imageFFT);

        // perform fftshift here
        /*
            4 3  -  3 4
            2 1  -  1 2
         */
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double temp = imageFFT[i][j];
                imageFFT[i][j] = imageFFT[i][j + cols];
                imageFFT[i][j + cols] = temp;
            }
        }
        /*
            3 4  -  1 2
            1 2  -  3 4
         */
        for (int j = 0; j < cols * 2; j++) {
            for (int i = 0; i < rows / 2; i++) {
                double temp = imageFFT[i][j];
                imageFFT[i][j] = imageFFT[i+(rows/2)][j];
                imageFFT[i+(rows/2)][j] = temp;
            }
        }

        return imageFFT;
    }
    private double[][] performInverseFFTOnImageOneChannel(double[][] imageFFT){
        int rows = imageFFT.length;
        int cols = imageFFT[0].length/2;
        double[][] imageFFTInverse = new double[rows][cols*2];
        double[][] imageFFTInverseTemp = new double[rows][cols*2];
        for (int i = 0; i < imageFFT.length; i++) {
            for (int j = 0; j <imageFFT[0].length; j++){
                imageFFTInverse[i][j] = imageFFT[i][j];
            }
        }
        // fft
        DoubleFFT_2D fft = new DoubleFFT_2D(rows,cols);
        fft.complexForward(imageFFTInverse);

        // perform fftshift here
        /*
            4 3  -  3 4
            2 1  -  1 2
         */
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double temp = imageFFTInverse[i][j];
                imageFFTInverse[i][j] = imageFFTInverse[i][j + cols];
                imageFFTInverse[i][j + cols] = temp;
            }
        }
        /*
            3 4  -  1 2
            1 2  -  3 4
         */
        for (int j = 0; j < cols * 2; j++) {
            for (int i = 0; i < rows / 2; i++) {
                double temp = imageFFTInverse[i][j];
                imageFFTInverse[i][j] = imageFFTInverse[i+(rows/2)][j];
                imageFFTInverse[i+(rows/2)][j] = temp;
            }
        }

        return imageFFTInverse;
    }

    private double[][] getComplexConjugate(double[][] imageFFT){
        int rows = imageFFT.length;
        int cols = imageFFT[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j % 2 == 0) {
                    result[i][j] = imageFFT[i][j];
                } else {
                    result[i][j] = -imageFFT[i][j];
                }
            }
        }
        return result;
    }
    //all image has even number of rows
    //(a+bi)(c+di) = (ac -bd) + (ad + bc)i
    private double[][] getPointWiseMultiplication(double[][] H, double[][] Xconjugate){
        int r1 = H.length;
        int r2 = Xconjugate.length;
        int c1 = H[0].length;
        int c2 = Xconjugate[0].length;
        int lengthOfZeroInRow = (r1 - r2)/2;
        int lengthOfZeroInCol = (c1 - c2)/2;
        // double[][] result = new double[r1][r2];
        for (int i = lengthOfZeroInRow; i < r2 + lengthOfZeroInRow; i++) {
            for (int j = lengthOfZeroInCol; j < c2 + lengthOfZeroInCol; j=j+2) {
                //ac-bd
                double a = H[i][j];
                double b = H[i][j+1];
                double c = Xconjugate[i-lengthOfZeroInRow][j-lengthOfZeroInCol];
                double d = Xconjugate[i-lengthOfZeroInCol][j-lengthOfZeroInCol+1];
                H[i][j] = a*c - b*d;
                //ad+bc
                H[i][j+1] = a*d + b*c;
            }
        }
        return H;
    }

    private int[][][] convertBitmapToARGB(Bitmap imgBitmap){
        int w = imgBitmap.getWidth();
        int h = imgBitmap.getHeight();
        int[][][] imgARGB = new int[w][h][4];
        for (int i=0; i < w; i++){
            for (int j=0; j < h; j++){
                int color = imgBitmap.getPixel(i,j);
                imgARGB[i][j][0] = Color.alpha(color);
                imgARGB[i][j][1] = Color.red(color);
                imgARGB[i][j][2] = Color.green(color);
                imgARGB[i][j][3] = Color.blue(color);
            }
        }
        return imgARGB;
    }

    private int[][] convertARGBToGrayScale(int[][][] imageARGB){
        int[][] imageGrayScale = new int[imageARGB.length][imageARGB[0].length];
        for (int i = 0; i < imageGrayScale.length; i++) {
            for (int j = 0; j < imageGrayScale[0].length; j++) {
                imageGrayScale[i][j] = (int) (  0.299 * imageARGB[i][j][1]
                                              + 0.587 * imageARGB[i][j][2]
                                              + 0.114 * imageARGB[i][j][3]);
            }
        }
        return imageGrayScale;
    }
    private int[][] getAlphaChannel(int[][][] img){
        return getOneChannel(img, 0);
    }
    private int[][] getRedChannel(int[][][] img){
        return getOneChannel(img, 1);
    }
    private int[][] getGreenChannel(int[][][] img){
        return getOneChannel(img, 2);
    }
    private int[][] getBlueChannel(int[][][] img){
        return getOneChannel(img, 3);
    }
    private int[][] getOneChannel(int[][][] img, int channel){
        int w = img.length;
        int h = img[0].length;
        int[][] oneChannel = new int[w][h];
        for (int i=0; i < w; i++){
            for (int j=0; j < h; j++){
                oneChannel[i][j] = img[i][j][channel];
            }
        }
        return oneChannel;
    }
    /*
       Pad zeros to make the two images have the same size and of power of 2
     */
    private Pair<int[][], int[][]> adjustSize(int[][] img1, int[][] img2){
        int r1 = img1.length;
        int c1 = img1[0].length;
        int r2 = img2.length;
        int c2 = img2[0].length;
        int r = getNextPowerOfTwo(Math.max(r1, r2));
        int c = getNextPowerOfTwo(Math.max(c1, c2));
        Log.d("ImageProcessing: ", r + " " + c);
        int[][] img1Adjusted = new int[r][c];
        int[][] img2Adjusted = new int[r][c];
        for (int i = 0; i < r; i++){
            for (int j = 0; j < c; j++){
                if (i >= r1 || j >= c1){
                    img1Adjusted[i][j] = 0;
                }else{
                    img1Adjusted[i][j] = img1[i][j];
                }
                if (i >= r2 || j >= c2){
                    img2Adjusted[i][j] = 0;
                }else{
                    img2Adjusted[i][j] = img2[i][j];
                }
            }
        }
        return new Pair<>(img1Adjusted, img2Adjusted);
    }
    private int getNextPowerOfTwo(int n){
        double y = Math.ceil(Math.log(n)/Math.log(2));
        return (int)Math.pow(2, y);
    }
}
