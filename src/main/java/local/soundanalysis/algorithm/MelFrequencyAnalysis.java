package local.soundanalysis.algorithm;

import static java.lang.Math.*;
import static local.soundanalysis.model.signal.Fourier.*;
import static local.soundanalysis.filter.Filter.*;

import local.soundanalysis.model.Signatures;
import local.soundanalysis.model.signal.Sound;
import local.soundanalysis.model.signal.Spectra;

/**
 * This is the class that contains functions to extract Mel-Frequency Cepstral
 * Coefficients from given samples
 *
 */
public class MelFrequencyAnalysis {

	private MelFrequencyAnalysis() {
	}

	/**
	 * Function to extract coefficients wrapped in Signatures object from given
	 * samples using Mel-Frequency Cepstral Coefficients algorithm
	 * 
	 * @param sound
	 *            samples and sample rate wrapped in Sound object
	 * @param coefficientsSize
	 *            size of coefficient you want to extract
	 * @return coefficients in wrapped in Signatures object
	 */
	public static Signatures extractCoefficients(Sound sound, int coefficientsSize) {
		return extractCoefficients(sound, 48, 128, coefficientsSize);
	}

	/**
	 * Function to extract coefficients wrapped in Signatures object from given
	 * samples using Mel-Frequency Cepstral Coefficients algorithm
	 * 
	 * @param sound
	 *            samples and sample rate wrapped in Sound object
	 * @param filterSize
	 *            size of filter
	 * @param binSize
	 *            size of bin
	 * @param coefficientsSize
	 *            size of coefficient you want to extract
	 * @return coefficients in wrapped in Signatures object
	 */
	public static Signatures extractCoefficients(Sound sound, int filterSize, int binSize, int coefficientsSize) {
		return new Signatures(extractCoefficients(sound.getSamples(), sound.getSampleRate(), filterSize, binSize,
				coefficientsSize));
	}

	/**
	 * Function to extract coefficients wrapped in Signatures object from given
	 * samples using Mel-Frequency Cepstral Coefficients algorithm
	 * 
	 * @param samples
	 *            samples of signal you want to extract
	 * @param sampleRate
	 *            sample rate of signal
	 * @param filterSize
	 *            size of filter
	 * @param binSize
	 *            size of bin
	 * @param coefficientsSize
	 *            size of coefficients you want to extract
	 * @return coefficients
	 */
	public static double[] extractCoefficients(double[] samples, double sampleRate, int filterSize, int binSize,
			int coefficientsSize) {
		double[][] spectrums = fastFourierTransformToSpectra(calculateFrames(
				getOverlapFrames(samples, (int) (sampleRate * 0.1)), getHammingFunction((int) (sampleRate * 0.1))));
		
		double[][] mfccs = new double[spectrums.length][];
		for(int i = 0; i < spectrums.length; i++){
			mfccs[i] = extractFirstNCoefficient(spectrums[i], sampleRate,filterSize,
				binSize, coefficientsSize);
		}
		
		double[] mfcc = new double[mfccs[0].length];
		for(int i = 0; i < mfccs.length; i++){
			for(int j = 0; j < mfcc.length; j++){
				mfcc[j] += mfccs[i][j];
			}
		}
		
		for(int i = 0; i < mfcc.length; i++){
			mfcc[i] /= mfccs.length;
		}
		
		return mfcc;
	}

	/**
	 * Function to extract coefficients[0] - coefficients[n] wrapped in
	 * Signatures object from given samples using Mel-Frequency Cepstral
	 * Coefficients algorithm
	 * 
	 * @param spectra
	 *            spectrum of signal
	 * @param sampleRate
	 *            signal sample rate
	 * @param filterSize
	 *            size of filter
	 * @param binSize
	 *            size of bin
	 * @param coefficientsSize
	 *            size of coefficients you want to extract
	 * @return coefficients in wrapped in Signatures object
	 */
	public static double[] extractFirstNCoefficient(double[] spectra, double sampleRate, int filterSize, int binSize,
			int coefficientsSize) {
		double[] coef = new double[coefficientsSize];
		for (int i = 0; i < coefficientsSize; i++) {
			coef[i] = extractCoefficient(spectra, sampleRate, filterSize, binSize, i);
		}
		return coef;
	}

	/**
	 * Function to extract coefficients[0] - coefficients[n] wrapped in
	 * Signatures object from given samples using Mel-Frequency Cepstral
	 * Coefficients algorithm
	 * 
	 * @param spectra
	 *            spectrum and sample rate of signal wrapped in Spectra object
	 * @param filterSize
	 *            size of filter
	 * @param binSize
	 *            size of bin
	 * @param coefficientsSize
	 *            size of coefficients you want to extract
	 * @return coefficients in wrapped in Signatures object
	 */
	public static double[] extractFirstNCoefficient(Spectra spectra, int filterSize, int binSize,
			int coefficientsSize) {
		return extractFirstNCoefficient(spectra.getSpectrum(), spectra.getSampleRate(), filterSize, binSize,
				coefficientsSize);
	}

	private static double extractCoefficient(double[] spectra, double sampleRate, int filterSize, int binSize, int m) {
		double result = 0.0f;
		double outerSum = 0.0f;
		double innerSum = 0.0f;
		if (m >= filterSize) {
			return 0.0f;
		}
		result = normalizationFactor(filterSize, m);
		for (int i = 1; i <= filterSize; i++) {
			innerSum = 0.0f;
			for (int j = 0; j < binSize - 1; j++) {
				innerSum += abs(spectra[j] * getFilterParameter(sampleRate, binSize, j, i));
			}
			if (innerSum > 0.0f) {
				innerSum = log(innerSum);
			}
			innerSum = innerSum * cos(((m * PI) / filterSize) * (i - 0.5f));
			outerSum += innerSum;
		}
		result *= outerSum;
		return result;
	}

	private static double normalizationFactor(int filterSize, int m) {
		double normalizationFactor = 0.0f;
		if (m == 0) {
			normalizationFactor = sqrt(1.0f / filterSize);
		} else {
			normalizationFactor = sqrt(2.0f / filterSize);
		}
		return normalizationFactor;
	}

	private static double getFilterParameter(double sampleRate, int binSize, int frequencyBand, int filterBand) {
		double filterParameter = 0.0f;
		double boundary = (frequencyBand * sampleRate) / binSize;
		double prevCenterFrequency = getCenterFrequency(filterBand - 1);
		double thisCenterFrequency = getCenterFrequency(filterBand);
		double nextCenterFrequency = getCenterFrequency(filterBand + 1);
		if (boundary >= 0 && boundary < prevCenterFrequency) {
			filterParameter = 0.0f;
		} else if (boundary >= prevCenterFrequency && boundary < thisCenterFrequency) {
			filterParameter = (boundary - prevCenterFrequency) / (thisCenterFrequency - prevCenterFrequency);
			filterParameter *= getMagnitudeFactor(filterBand);
		} else if (boundary >= thisCenterFrequency && boundary < nextCenterFrequency) {
			filterParameter = (boundary - nextCenterFrequency) / (thisCenterFrequency - nextCenterFrequency);
			filterParameter *= getMagnitudeFactor(filterBand);
		} else if (boundary >= nextCenterFrequency && boundary < sampleRate) {
			filterParameter = 0.0f;
		}
		return filterParameter;
	}

	private static double getMagnitudeFactor(int filterBand) {
		double magnitudeFactor = 0.0f;
		if (filterBand >= 1 && filterBand <= 14) {
			magnitudeFactor = 0.015;
		} else if (filterBand >= 15 && filterBand <= 48) {
			magnitudeFactor = 2.0f / (getCenterFrequency(filterBand + 1) - getCenterFrequency(filterBand - 1));
		}
		return magnitudeFactor;
	}

	private static double getCenterFrequency(int filterBand) {
		double centerFrequency = 0.0f;
		double exponent;
		if (filterBand == 0) {
			centerFrequency = 0;
		} else if (filterBand >= 1 && filterBand <= 14) {
			centerFrequency = (200.0f * filterBand) / 3.0f;
		} else {
			exponent = filterBand - 14.0f;
			centerFrequency = pow(1.0711703, exponent);
			centerFrequency *= 1073.4;
		}
		return centerFrequency;
	}
}
