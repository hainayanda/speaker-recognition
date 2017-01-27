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

	/**
	 * 
	 * 
	 * @param windowSize
	 * @return
	 */
	public static double[] getHammingFunction(int windowSize) {
		double[] factors = new double[windowSize];
		for (int i = 0; i < windowSize; i++) {
			factors[i] = 0.54d - (0.46d * Math.cos((Math.PI * 2 * i) / windowSize - 1));
		}
		return factors;
	}
	
	/**
	 * 
	 * @param samples
	 * @param divider
	 * @param index
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static double[] getFrame(double[] samples, int divider, int index) throws IllegalArgumentException {
		int frameSize = samples.length / divider;
		if (frameSize == 0)
			throw new IllegalArgumentException("frameSize are too small");
		double[] frame = new double[frameSize];
		int startIndex = frameSize * index;
		for (int i = 0; i < frameSize; i++) {
			int j = i + startIndex;
			if (j < samples.length)
				frame[i] = samples[j];
			else
				frame[i] = 0;
		}
		return frame;
	}

	/**
	 * 
	 * @param samples
	 * @param divider
	 * @return
	 */
	public static double[][] getFrames(double[] samples, int divider) {
		if (divider <= 0)
			throw new IllegalArgumentException("divider must be greater than 0");
		double[][] frames = new double[divider][];
		for (int i = 0; i < divider; i++) {
			frames[i] = getFrame(samples, divider, i);
		}
		return frames;
	}

	/**
	 * 
	 * @param samples
	 * @param frameSize
	 * @return
	 */
	public static double[][] getOverlapFrames(double[] samples, int frameSize){
		if (frameSize < 4)
			throw new IllegalArgumentException("frameSize must be greater than 3");
		int numberOfFrame = (((samples.length / frameSize) + 1)*2)-1;
		double[][] frames = new double[numberOfFrame][];
		for (int i = 0; i < numberOfFrame; i++) {
			frames[i] = getOverlapFrame(samples, frameSize, i);
		}
		return frames;
	}
	
	/**
	 * 
	 * @param samples
	 * @param divider
	 * @param index
	 * @return
	 */
	private static double[] getOverlapFrame(double[] samples, int frameSize, int index) {
		if (frameSize < 4)
			throw new IllegalArgumentException("frameSize are too small");
		double[] frame = new double[frameSize];
		int startIndex = (frameSize / 2) * index;
		for (int i = 0; i < frameSize; i++) {
			int j = i + startIndex;
			if (j < samples.length)
				frame[i] = samples[j];
			else
				frame[i] = 0.0;
		}
		return frame;
	}
	
	/**
	 * 
	 * @param frames
	 * @param hammingFunction
	 * @return
	 */
	public static double[][] calculateFrames(double[][] frames, double[] function) {
		if(!isArraysSimetric(frames)) throw new IllegalArgumentException("frames is not symetric in size");
		else if(function.length != frames[0].length) throw new IllegalArgumentException("individual frames and function is not the same size");
		
		double[][] result = new double[frames.length][];
		
		for(int i = 0; i < frames.length; i++){
			result[i] = new double[function.length];
			for(int j = 0; j < function.length; j++){
				result[i][j] = frames[i][j] * function[j];
			}
		}
		return result;
	}

	/**
	 * 
	 * @param frames
	 * @param function
	 * @return
	 */
	public static void applyFunctionToFrames(double[][] frames, double[] function) {
		if(!isArraysSimetric(frames)) throw new IllegalArgumentException("frames is not symetric in size");
		else if(function.length != frames[0].length) throw new IllegalArgumentException("individual frames and function is not the same size");
		
		for(int i = 0; i < frames.length; i++){
			for(int j = 0; j < function.length; j++){
				frames[i][j] *= function[j];
			}
		}
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
