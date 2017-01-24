package local.soundanalysis.model.signal;

import static local.soundanalysis.math.Operation.*;

import java.io.Serializable;
import java.util.Arrays;

import local.soundanalysis.model.Complex;

public class Fourier implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8911981418832598908L;

	private static final Complex ZERO = new Complex(0, 0);

	private Complex[] series;
	private double sampleRate;
	private double lowestFrequency;
	private double highestFrequency;
	private Spectra spectra;

	public Fourier(Complex[] series, double sampleRate) {
		setSeries(series);
		setSampleRate(sampleRate);
		calcSpectra();
	}

	public Complex[] getSeries() {
		return series;
	}

	private void setSeries(Complex[] series) throws IllegalArgumentException {
		if (series == null)
			throw new IllegalArgumentException("series cannot be null");
		this.series = series;
		calcFrequency();
	}

	private void setSampleRate(double sampleRate) throws IllegalArgumentException {
		if (sampleRate <= 0)
			throw new IllegalArgumentException("sampleRate must be greater than zero, you are trying to input "
					+ sampleRate + " as sampleRate parameter");
		this.sampleRate = sampleRate;
		calcFrequency();
	}

	public double getSampleRate() {
		return sampleRate;
	}

	public double getLowestFrequency() {
		return lowestFrequency;
	}

	public double getHighestFrequency() {
		return highestFrequency;
	}

	public double getFrequencyBand() {
		return lowestFrequency;
	}

	public int seriesLength() {
		return series.length;
	}

	private void calcSpectra() {
		double[] spectrum = Complex.getAmplitude(this.series);
		this.spectra = new Spectra(this, spectrum);
	}

	private void calcFrequency() {
		this.lowestFrequency = 1 / (seriesLength() / sampleRate);
		this.highestFrequency = sampleRate / 2;
	}

	public void setComplexByFrequency(Complex complex, double frequency) {
		this.series[getIndex(frequency)] = complex;
		this.spectra.setAmplitude(complex.getAmplitude(), frequency);
	}

	public Complex getComplexByFrequency(double frequency) {
		return series[getIndex(frequency)];
	}

	public void setComplexByIndex(Complex complex, int index) {
		this.series[index] = complex;
		this.spectra.setAmplitude(complex.getAmplitude(), index);
	}

	public Complex getComplexByIndex(int index) {
		return series[index];
	}

	private static int getIndex(Fourier series, double frequency) {
		if (frequency < series.getLowestFrequency())
			throw new IllegalArgumentException(
					"you can't get complex number from frequency lower than nyquist frequency :"
							+ series.getLowestFrequency() + " You are trying to get :" + frequency);
		else if (frequency > series.getHighestFrequency())
			throw new IllegalArgumentException(
					"you can't get complex number from frequency higher than nyquist frequency :"
							+ series.getHighestFrequency() + " You are trying to get :" + frequency);

		int start = 0;
		if (isEven(series.seriesLength()))
			start++;
		return round(frequency / series.getFrequencyBand()) + start;
	}

	public int getIndex(double frequency) {
		return getIndex(this, frequency);
	}

	public Spectra getSpectra() {
		return spectra;
	}

	public void setSpectra(Spectra spectra) {
		this.spectra = spectra;
	}

	public static Spectra fastFourierTransformToSpectra(Sound sound) {
		Fourier series = new Fourier(fastFourierTransform(sound.getSamples()), sound.getSampleRate());
		return series.getSpectra();
	}

	public static double[] fastFourierTransformToSpectra(double[] samples) {
		return Complex.getAmplitude(fastFourierTransform(samples));
	}

	public static Fourier fastFourierTransform(Sound sound) {
		return new Fourier(fastFourierTransform(sound.getSamples()), sound.getSampleRate());
	}

	public static Complex[] fastFourierTransform(double[] samples) {
		if (!isPowerOfTwo(samples.length)) {
			int length = getNearestPowerOfTwo(samples.length);
			double[] newSamples = new double[length];
			for (int i = 0; i < length; i++) {
				if (i < samples.length)
					newSamples[i] = samples[i];
				else
					newSamples[i] = 0.0d;
			}
			return fastFourierTransform(Complex.Parse(newSamples));
		} else {
			return fastFourierTransform(Complex.Parse(samples));
		}
	}

	public static Complex[] fastFourierTransform(Complex[] samples) {
		int length = samples.length;

		if (length == 1)
			return new Complex[] { samples[0] };

		if (length % 2 != 0) {
			throw new RuntimeException("n is not a power of 2");
		}

		Complex[] even = new Complex[length / 2];
		for (int i = 0; i < length / 2; i++) {
			even[i] = samples[2 * i];
		}
		Complex[] complexEven = fastFourierTransform(even);

		Complex[] odd = even;
		for (int i = 0; i < length / 2; i++) {
			odd[i] = samples[2 * i + 1];
		}
		Complex[] complexOdd = fastFourierTransform(odd);

		Complex[] series = new Complex[length];
		for (int i = 0; i < length / 2; i++) {
			double omega = (double) (-2 * i * Math.PI / length);
			Complex compOmega = new Complex((double) Math.cos(omega), (double) Math.sin(omega));
			series[i] = complexEven[i].plus(compOmega.times(complexOdd[i]));
			series[i + length / 2] = complexEven[i].minus(compOmega.times(complexOdd[i]));
		}
		return series;
	}

	public static double[] reverseFourierTransform(Complex[] series) {
		return reverseFourierTransform(Complex.getAmplitude(series));
	}

	public static Sound reverseFourierTransform(Fourier series) {
		return new Sound(reverseFourierTransform(series.getSeries()), series.getSampleRate());
	}

	public static double[] reverseFourierTransform(double[] spectrum) {
		double[] samples = new double[spectrum.length];
		int i = 0;
		int scale = spectrum.length / 2;
		if (spectrum.length % 2 == 0)
			i = 1;
		while (i < spectrum.length) {
			for (int j = 0; j < spectrum.length / 2; j++) {
				samples[i] += (spectrum[j] / (double) scale)
						* Math.sin((double) 2 * Math.PI * ((double) j / ((double) (spectrum.length))) * (double) i);
			}
			i++;
		}
		return samples;
	}

	public static Sound inverseFourierTransform(Fourier series) {
		Complex[] complexSamples = inverseFourierTransform(series.getSeries());
		double[] samples = Complex.getAmplitude(complexSamples);
		return new Sound(samples, series.getSampleRate());
	}

	public static Complex[] inverseFourierTransform(Complex[] series) {
		int length = series.length;
		Complex[] inverse = new Complex[length];

		for (int i = 0; i < length; i++) {
			inverse[i] = series[i].conjugate();
		}
		inverse = fastFourierTransform(inverse);

		for (int i = 0; i < length; i++) {
			inverse[i] = inverse[i].conjugate();
		}

		for (int i = 0; i < length; i++) {
			inverse[i] = inverse[i].scale((double) (1.0 / length));
		}

		return inverse;

	}

	public static Complex[] complexConvolve(Complex[] firstFrame, Complex[] secondFrame) {
		if (firstFrame.length != secondFrame.length) {
			throw new RuntimeException("Dimensions don't agree");
		}

		int length = firstFrame.length;
		Complex[] firstFrameSeries = fastFourierTransform(firstFrame);
		Complex[] secondFrameSeries = fastFourierTransform(secondFrame);
		Complex[] temp = new Complex[length];
		for (int i = 0; i < length; i++) {
			temp[i] = firstFrameSeries[i].times(secondFrameSeries[i]);
		}

		return inverseFourierTransform(temp);
	}

	public static Complex[] convolve(Complex[] firstFrame, Complex[] secondFrame) {

		Complex[] firstComplex = new Complex[2 * firstFrame.length];
		for (int i = 0; i < firstFrame.length; i++)
			firstComplex[i] = firstFrame[i];
		for (int i = firstFrame.length; i < 2 * firstFrame.length; i++)
			firstComplex[i] = ZERO;

		Complex[] secondComplex = new Complex[2 * secondFrame.length];
		for (int i = 0; i < secondFrame.length; i++)
			secondComplex[i] = secondFrame[i];
		for (int i = secondFrame.length; i < 2 * secondFrame.length; i++)
			secondComplex[i] = ZERO;

		return complexConvolve(firstComplex, secondComplex);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(highestFrequency);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lowestFrequency);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(sampleRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(series);
		result = prime * result + ((spectra == null) ? 0 : spectra.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fourier other = (Fourier) obj;
		if (Double.doubleToLongBits(highestFrequency) != Double.doubleToLongBits(other.highestFrequency))
			return false;
		if (Double.doubleToLongBits(lowestFrequency) != Double.doubleToLongBits(other.lowestFrequency))
			return false;
		if (Double.doubleToLongBits(sampleRate) != Double.doubleToLongBits(other.sampleRate))
			return false;
		if (!Arrays.equals(series, other.series))
			return false;
		if (spectra == null) {
			if (other.spectra != null)
				return false;
		} else if (!spectra.equals(other.spectra))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Fourier [sampleRate=" + sampleRate + ", lowestFrequency=" + lowestFrequency + ", highestFrequency="
				+ highestFrequency + "]";
	}

}
