package local.soundanalysis.model.signal;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class Sound implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3801721118200438779L;
	
	private double[] samples;
	private double sampleRate;

	/**
	 * 
	 * @param samples
	 * @param sampleRate
	 */
	public Sound(double[] samples, double sampleRate) {
		setSamples(samples);
		setSampleRate(sampleRate);
	}

	/**
	 * 
	 * @return
	 */
	public double[] getSamples() {
		return samples;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public double getSample(int index) {
		return samples[index];
	}

	/**
	 * 
	 * @param samples
	 * @param sampleRate
	 */
	public void setSamples(double[] samples, double sampleRate) {
		setSamples(samples);
		setSampleRate(sampleRate);
	}

	/**
	 * 
	 * @param samples
	 * @throws IllegalArgumentException
	 */
	public void setSamples(double[] samples) throws IllegalArgumentException {
		if (samples == null)
			throw new IllegalArgumentException("samples cannot be null");
		else if (!isInRange(samples))
			throw new IllegalArgumentException("samples must be at range between 1 and -1");
		this.samples = samples;
	}

	private void setSampleRate(double sampleRate) throws IllegalArgumentException {
		if (sampleRate <= 0)
			throw new IllegalArgumentException("sampleRate must be greater than zero, you are trying to input "
					+ sampleRate + " as sampleRate parameter");
		this.sampleRate = sampleRate;
	}

	/**
	 * 
	 * @return
	 */
	public double getSampleRate() {
		return sampleRate;
	}

	/**
	 * 
	 * @return
	 */
	public int samplesLength() {
		return samples.length;
	}

	private static boolean isInRange(double[] samples) {
		for (int i = 0; i < samples.length; i++) {
			double test = Math.abs(samples[i]);
			if (test > 1)
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(sampleRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(samples);
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
		Sound other = (Sound) obj;
		if (Double.doubleToLongBits(sampleRate) != Double.doubleToLongBits(other.sampleRate))
			return false;
		if (!Arrays.equals(samples, other.samples))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Sound [sampleRate=" + sampleRate + "]";
	}

}
