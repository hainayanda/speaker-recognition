package local.soundanalysis.model.signal;

import static local.soundanalysis.math.Operation.*;

import java.io.Serializable;
import java.util.Arrays;

public class Spectra implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4548120935694838631L;
	
	private double[] spectrum;
	private Fourier series;

	Spectra(Fourier series, double[] spectrum) {
		this.series = series;
		setSpectrum(spectrum);
	}

	public Fourier getSeries() {
		return series;
	}

	public double[] getSpectrum() {
		return spectrum;
	}

	public double[] getRealSpectrum() {
		double[] spectrum = new double[spectrumLength() / 2];
		int start = 0;
		if (isEven(spectrumLength()))
			start++;
		for (int i = 0; i < spectrum.length; i++) {
			spectrum[i] = this.spectrum[start + i];
		}
		return spectrum;
	}

	private void setSpectrum(double[] series) throws IllegalArgumentException {
		if (series == null)
			throw new IllegalArgumentException("series cannot be null");
		this.spectrum = series;
	}

	public double getSampleRate() {
		return series.getSampleRate();
	}

	public int spectrumLength() {
		return spectrum.length;
	}

	public double getLowestFrequency() {
		return series.getLowestFrequency();
	}

	public double getHighestFrequency() {
		return series.getLowestFrequency();
	}

	public double getFrequencyBand() {
		return getLowestFrequency();
	}

	void setAmplitude(double amplitude, double frequency) {
		this.spectrum[getIndex(frequency)] = amplitude;
	}

	void setAmplitude(double amplitude, int index) {
		this.spectrum[index] = amplitude;
	}

	public double getAmplitude(double frequency) {
		return spectrum[getIndex(frequency)];
	}

	public double getAmplitude(int index) {
		return spectrum[index];
	}

	public static int getIndex(Spectra spectra, double frequency) {
		if (frequency < spectra.getLowestFrequency())
			throw new IllegalArgumentException("you can't get amplitude from frequency lower than nyquist frequency");
		else if (frequency > spectra.getHighestFrequency())
			throw new IllegalArgumentException("you can't get amplitude from frequency higher than nyquist frequency");

		int start = 0;
		if (isEven(spectra.spectrumLength()))
			start++;
		return round(frequency / spectra.getFrequencyBand()) + start;
	}

	public int getIndex(double frequency) {
		return getIndex(this, frequency);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((series == null) ? 0 : series.hashCode());
		result = prime * result + Arrays.hashCode(spectrum);
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
		Spectra other = (Spectra) obj;
		if (series == null) {
			if (other.series != null)
				return false;
		} else if (!series.equals(other.series))
			return false;
		if (!Arrays.equals(spectrum, other.spectrum))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Spectra [series=" + series.toString() + "]";
	}

}
