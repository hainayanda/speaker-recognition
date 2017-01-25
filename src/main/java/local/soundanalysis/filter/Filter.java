package local.soundanalysis.filter;

import java.util.LinkedList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import local.soundanalysis.model.signal.Fourier;
import local.soundanalysis.model.signal.Sound;

import static local.soundanalysis.math.Operation.*;

/**
 * This is the class that contains filter functions
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class Filter {

	private Filter() {
	}

	/**
	 * Simple band pass filter function
	 * 
	 * @param sound
	 *            Sound object
	 * @param lowCutOff
	 *            desired low frequency cut off
	 * @param highCutOff
	 *            desired high frequency cut off
	 * @return sound filtered
	 * @throws OperationNotSupportedException
	 */
	public static Sound emaBandPass(Sound sound, double lowCutOff, double highCutOff)
			throws OperationNotSupportedException {
		highCutOff /= sound.getSampleRate();
		lowCutOff /= sound.getSampleRate();
		double[] samples = sound.getSamples();
		double[] bandStop = new double[samples.length];

		double emaSLow = samples[0];
		bandStop[0] = 0;
		double emaSHigh = samples[1];
		bandStop[1] = 0;
		double bandPass = 0;

		for (int i = 2; i < samples.length; i++) {
			emaSLow = (lowCutOff * samples[i]) + ((1 - lowCutOff) * emaSLow);
			emaSHigh = (highCutOff * samples[i]) + ((1 - highCutOff) * emaSHigh);

			bandPass = emaSHigh - emaSLow;
			bandStop[i] = samples[i] - bandPass;
			if (bandStop[i] == Double.NaN || bandStop[i] == Double.NEGATIVE_INFINITY
					|| bandStop[i] == Double.POSITIVE_INFINITY)
				throw new OperationNotSupportedException("EMA S LOW: " + emaSLow + " EMA S HIGH: " + emaSHigh
						+ " BAND PASS: " + bandPass + " SAMPLES: " + samples[i] + " ");

		}

		if (!isInRange(bandStop)) {
			return new Sound(Normalizer.normalizer(bandStop), sound.getSampleRate());
		}

		return new Sound(bandStop, sound.getSampleRate());
	}

	/**
	 * Band pass filter that using fourier transform, filter undesired frequency
	 * and reverse it into time domain signal. the process time is took so long
	 * and not optimized
	 * 
	 * @param sound
	 *            Sound object
	 * @param lowCutOff
	 *            desired low frequency cut off
	 * @param highCutOff
	 *            desired high frequency cut off
	 * @return sound filtered
	 */
	public static Sound spectralBandPass(Sound sound, double lowCutOff, double highCutOff) {
		int range = (int) (sound.getSampleRate() / 10.0);
		if (range * 3 > sound.samplesLength())
			range = 2;
		double[] ratio = getFilterRatio(range);
		Fourier series = Fourier.fastFourierTransform(sound);
		int low = series.getIndex(lowCutOff);
		int high = series.getIndex(highCutOff);
		int highMirror = sound.samplesLength() - high;
		int lowMirror = sound.samplesLength() - low;

		if (series.seriesLength() % 2 == 0) {
			highMirror++;
			lowMirror++;
		}

		for (int i = 0; i < ratio.length; i++) {
			if (low - i >= 0 && low - i <= series.seriesLength() / 2) {
				series.getComplexByIndex(low - i).scale(ratio[i]);
				series.getComplexByIndex(lowMirror + i).scale(ratio[i]);
			}
			if (high + i >= 0 && high + i <= series.seriesLength() / 2) {
				series.getComplexByIndex(high + i).scale(ratio[i]);
				series.getComplexByIndex(highMirror - i).scale(ratio[i]);
			}
		}

		for (int i = 0; i < low - ratio.length; i++) {
			series.getComplexByIndex(i).scale(0);
		}

		for (int i = series.seriesLength(); i > lowMirror + ratio.length; i--) {
			series.getComplexByIndex(i).scale(0);
		}

		for (int i = high + 1; i < highMirror; i++) {
			series.getComplexByIndex(i).scale(0);
		}

		return series.reverseFourierTransform();
	}

	/**
	 * filter that remove silence from sound given
	 * 
	 * @param sound
	 *            Sound object
	 */
	public static void removeSilence(Sound sound) {
		Sound newSound = silenceRemover(sound);
		sound.setSamples(newSound.getSamples(), newSound.getSampleRate());
	}

	/**
	 * filter that remove silence contains in sound
	 * 
	 * @param sound
	 *            Sound object
	 * @return Sound object that silence removed
	 */
	public static Sound silenceRemover(Sound sound) {
		int divider = (int) (((double) sound.samplesLength() / (double) sound.getSampleRate()) * 10);
		if (divider == 0)
			divider++;
		double[][] frames = getFrames(sound.getSamples(), divider);
		List<double[]> framesList = new LinkedList<double[]>();
		for (int i = 0; i < divider; i++) {
			if (average(absolute(frames[i])) > 0.02)
				framesList.add(frames[i]);
		}

		int newSamplesLength = framesList.size() * framesList.get(0).length;
		return new Sound(mergeFrames(framesList, newSamplesLength), sound.getSampleRate());
	}

	/**
	 * 
	 * @param framesList
	 * @param newSamplesLength
	 * @return
	 */
	public static double[] mergeFrames(List<double[]> framesList, int newSamplesLength) {
		int length = 0;
		for (int i = 0; i < framesList.size(); i++) {
			length += framesList.get(i).length;
		}
		int index = 0;
		double[] samples = new double[length];
		for (int i = 0; i < framesList.size(); i++) {
			for (int j = 0; j < framesList.get(i).length; j++) {
				samples[index] = framesList.get(i)[j];
				index++;
			}
		}
		return samples;
	}

	private static double[] getFilterRatio(int range) {
		if (range < 2)
			range = 2;
		double[] ratio = new double[range];
		double divider = 2.0 / range;
		for (int i = 0; i < range; i++) {
			if (i == range - 1)
				ratio[i] = 0.0;
			else {
				double radian = Math.PI * (1.0 + (divider * (i))) / 2;
				ratio[i] = (Math.sin(radian) + 1.0) / 2;
			}
		}
		return ratio;
	}

	private static boolean isInRange(double[] samples) {
		for (int i = 0; i < samples.length; i++) {
			double test = Math.abs(samples[i]);
			if (test > 1)
				return false;
		}
		return true;
	}

}
