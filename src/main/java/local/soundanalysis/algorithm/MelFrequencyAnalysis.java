package local.soundanalysis.algorithm;

import static java.lang.Math.*;
import static local.soundanalysis.model.signal.Fourier.*;

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
	 * Function to extract coefficients wrapped in Coefficients object from
	 * given samples using Mel-Frequency Cepstral Coefficients algorithm
	 * @param sound
	 * @param signatureSize
	 * @return
	 */
	public static Signatures extractSignatures(Sound sound, int signatureSize) {
		return extractSignatures(sound, 48, 128, signatureSize);
	}

	/**
	 * 
	 * @param sound
	 * @param filterSize
	 * @param binSize
	 * @param signatureSize
	 * @return
	 */
	public static Signatures extractSignatures(Sound sound, int filterSize, int binSize, int signatureSize) {
		double[] mfcc = getFirstNCoefficient(fastFourierTransformToSpectra(sound), filterSize, binSize, signatureSize);
		return new Signatures(mfcc);
	}

	/**
	 * 
	 * @param spectra
	 * @param filterSize
	 * @param binSize
	 * @param coeficientSize
	 * @return
	 */
	public static double[] getFirstNCoefficient(Spectra spectra, int filterSize, int binSize, int coeficientSize) {
		double[] coef = new double[coeficientSize];
		for (int i = 0; i < coeficientSize; i++) {
			coef[i] = getCoefficient(spectra, filterSize, binSize, i);
		}
		return coef;
	}

	/**
	 * 
	 * @param spectra
	 * @param filterSize
	 * @param binSize
	 * @param m
	 * @return
	 */
	public static double getCoefficient(Spectra spectra, int filterSize, int binSize, int m) {
		double result = 0.0f;
		double outerSum = 0.0f;
		double innerSum = 0.0f;
		if (m >= filterSize) {
			return 0.0f;
		}
		result = normalizationFactor(filterSize, m);
		double[] spectrum = spectra.getSpectrum();
		for (int i = 1; i <= filterSize; i++) {
			innerSum = 0.0f;
			for (int j = 0; j < binSize - 1; j++) {
				innerSum += abs(spectrum[j] * getFilterParameter(spectra.getSampleRate(), binSize, j, i));
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

	/**
	 * 
	 * @param filterSize
	 * @param m
	 * @return
	 */
	private static double normalizationFactor(int filterSize, int m) {
		double normalizationFactor = 0.0f;
		if (m == 0) {
			normalizationFactor = sqrt(1.0f / filterSize);
		} else {
			normalizationFactor = sqrt(2.0f / filterSize);
		}
		return normalizationFactor;
	}

	/**
	 * 
	 * @param sampleRate
	 * @param binSize
	 * @param frequencyBand
	 * @param filterBand
	 * @return
	 */
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

	/**
	 * 
	 * @param filterBand
	 * @return
	 */
	private static double getMagnitudeFactor(int filterBand) {
		double magnitudeFactor = 0.0f;
		if (filterBand >= 1 && filterBand <= 14) {
			magnitudeFactor = 0.015;
		} else if (filterBand >= 15 && filterBand <= 48) {
			magnitudeFactor = 2.0f / (getCenterFrequency(filterBand + 1) - getCenterFrequency(filterBand - 1));
		}
		return magnitudeFactor;
	}

	/**
	 * 
	 * @param filterBand
	 * @return
	 */
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
