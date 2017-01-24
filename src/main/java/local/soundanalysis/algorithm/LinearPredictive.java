package local.soundanalysis.algorithm;

import local.soundanalysis.model.Coefficients;
import local.soundanalysis.model.signal.Sound;

/**
 * This is the class that contains functions to get Coeficient from given
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
	 *            size of coeficients you want to extract from sound
	 * @return coefficients that wrapped in Coefficients object
	 */
	public static Coefficients extractSignatures(Sound sound, int signatureSize) {
		return extractSignatures(sound.getSamples(), signatureSize);
	}

	/**
	 * Function to extract coefficients wrapped in Coefficients object from
	 * given samples using Linear Predictive Coefficient algorithm
	 * 
	 * @param samples
	 *            array of doubles that contains samples. it must be normalize
	 *            into -1 to 1
	 * @param signatureSize
	 *            size of coeficients you want to extract from sound
	 * @return coeficients that wrapped in Coeficients object
	 */
	public static Coefficients extractSignatures(double[] samples, int signatureSize) {
		double error = 0;
		double[] coeficients = new double[signatureSize];
		double[] autoCorrelation = new double[signatureSize + 1];

		int i, j;
		j = signatureSize + 1;
		while (j-- != 0) {
			double temp = 0.0F;
			for (i = j; i < samples.length; i++)
				temp += samples[i] * samples[i - j];
			autoCorrelation[j] = temp;
		}

		error = autoCorrelation[0];
		for (i = 0; i < signatureSize; i++) {
			double temp = -autoCorrelation[i + 1];
			if (error == 0) {
				for (int k = 0; k < signatureSize; k++)
					coeficients[k] = 0.0f;
			}
			for (j = 0; j < i; j++)
				temp -= coeficients[j] * autoCorrelation[i - j];

			temp /= error;
			coeficients[i] = temp;

			for (j = 0; j < i / 2; j++) {
				double tmp = coeficients[j];
				coeficients[j] += temp * coeficients[i - 1 - j];
				coeficients[i - 1 - j] += temp * tmp;
			}
			if (i % 2 != 0)
				coeficients[j] += coeficients[j] * temp;
			error *= 1.0 - temp * temp;
		}

		return new Coefficients(coeficients, error);
	}
}
