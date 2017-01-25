package local.soundanalysis.algorithm;

import local.soundanalysis.model.Coefficients;
import local.soundanalysis.model.signal.Sound;

import static local.soundanalysis.math.Operation.*;
/**
 * This is the class that contains functions to get Coefficient from given
 * samples
 * 
 */
public class LinearPredictive {

	private LinearPredictive() {
	}

	/**
	 * Function to extract coefficients wrapped in Coefficients object from
	 * given sound using Linear Predictive Coefficient algorithm
	 * 
	 * @param sound
	 *            Sound object that contains samples
	 * @param signatureSize
	 *            size of coefficients you want to extract from sound
	 * @return coefficients that wrapped in Coefficients object
	 */
	public static Coefficients extractCoefficients(Sound sound, int signatureSize) {
		return extractCoefficients(sound.getSamples(), sound.getSampleRate(), signatureSize);
	}

	/**
	 * 
	 * @param samples
	 * @param sampleRate
	 * @param signatureSize
	 * @return
	 */
	public static Coefficients extractCoefficients(double[] samples, double sampleRate, int signatureSize) {
		double[] signature = new double[signatureSize];
		Coefficients temp = null;
		
		int windowSize = (int)sampleRate/24;
		if(!isPowerOfTwo(windowSize))
			windowSize = getNearestPowerOfTwo(windowSize);
		
		double[][] frames = getOverlapFrames(samples, windowSize);
		double[] hammingFunction = getHammingFunction(windowSize);
		applyFunctionToFrames(frames, hammingFunction);
		
		int i = 0;
		for(i = 0; i + windowSize <= frames.length; i++){
			temp = linearPredictiveCoding(frames[i], signatureSize);
			double[] coefficient = temp.getSignatures();
			for(int j = 0; j < signatureSize; j++)
				signature[j] += coefficient[j];
		}
		
		if(i > 1){
			for(int j = 0; j < signatureSize; j++)
				signature[j] /= i;
		}
		
		return new Coefficients(signature, temp.getError());
	}

	private static Coefficients linearPredictiveCoding(double[] frame, int signatureSize) {
		double[] error = new double[signatureSize];
		double[] k = new double[signatureSize];
		double[] result = new double[signatureSize];
		double[][] matrix = new double[signatureSize][signatureSize];
		double[] autoCorrelation = new double[signatureSize];
		
		for(int i = 0; i < signatureSize; i++){
			autoCorrelation[i] = autoCorrelation(frame, i);
		}
		
		error[0] = autoCorrelation[0];
		
		for(int i = 1; i < signatureSize; i++){
			double temp = autoCorrelation[i];
			for(int j = 1; j < i; j++){
				temp -= matrix[i - 1][j] * autoCorrelation[i = 1];
			}
			k[i] = temp / error[i - 1];
			
			for(int j = 0; j < i; j++){
				matrix[i][j] = matrix[i-1][j] - k[i] * matrix[i-1][i-j];
			}
			matrix[i][i] = k[i];
			error[i] = (1 - (k[i] * k[i])) * error[i - 1];
		}
		
		for(int i = 0; i < signatureSize; i++){
			if(Double.isNaN(matrix[signatureSize - 1][i]))
				result[i] = 0.0;
			else
				result[i] = matrix[signatureSize - 1][i];
		}
		double sumError = 0.0;
		for(int i = 0; i < signatureSize; i++){
			sumError += error[i];
		}
		
		return new Coefficients(result, sumError);
	}

	private static double autoCorrelation(double[] frame, int index) {
		double result = 0.0;
        for (int i = index; i < frame.length; i++)
            result += frame[i] * frame[i - index];
        return result;
	}

	private static double[] getHammingFunction(int windowSize) {
            double[] factors = new double[windowSize];
                for(int i = 0; i < windowSize; i++) {
                    factors[i] = 0.54d - (0.46d * Math.cos((Math.PI * 2 * i) / windowSize - 1));
                }
            return factors;
	}
}
