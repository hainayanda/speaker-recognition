package local.soundanalysis.model.signal;

import static local.soundanalysis.math.Operation.*;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
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

	/**
	 * 
	 * @return
	 */
	public Fourier getSeries() {
		return series;
	}

	/**
	 * 
	 * @return
	 */
	public double[] getSpectrum() {
		return spectrum;
	}

	/**
	 * 
	 * @return
	 */
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

	/**
	 * 
	 * @return
	 */
	public double getSampleRate() {
		return series.getSampleRate();
	}

	/**
	 * 
	 * @return
	 */
	public int spectrumLength() {
		return spectrum.length;
	}

	/**
	 * 
	 * @return
	 */
	public double getLowestFrequency() {
		return series.getLowestFrequency();
	}

	/**
	 * 
	 * @return
	 */
	public double getHighestFrequency() {
		return series.getLowestFrequency();
	}

	/**
	 * 
	 * @return
	 */
	public double getFrequencyBand() {
		return getLowestFrequency();
	}

	void setAmplitude(double amplitude, double frequency) {
		this.spectrum[getIndex(frequency)] = amplitude;
	}

	void setAmplitude(double amplitude, int index) {
		this.spectrum[index] = amplitude;
	}

	/**
	 * 
	 * @param frequency
	 * @return
	 */
	public double getAmplitude(double frequency) {
		return spectrum[getIndex(frequency)];
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public double getAmplitude(int index) {
		return spectrum[index];
	}

	/**
	 * 
	 * @param frequency
	 * @return
	 */
	public int getIndex(double frequency) {
		return getIndex(this, frequency);
	}

	private static int getIndex(Spectra spectra, double frequency) {
		if (frequency < spectra.getLowestFrequency())
			throw new IllegalArgumentException("you can't get amplitude from frequency lower than nyquist frequency");
		else if (frequency > spectra.getHighestFrequency())
			throw new IllegalArgumentException("you can't get amplitude from frequency higher than nyquist frequency");

		int start = 0;
		if (isEven(spectra.spectrumLength()))
			start++;
		return round(frequency / spectra.getFrequencyBand()) + start;
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
