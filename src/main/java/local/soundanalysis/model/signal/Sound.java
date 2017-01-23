package local.soundanalysis.model.signal;

public class Sound {

	private double[] samples;
	private double sampleRate;

	public Sound(double[] samples, double sampleRate) {
		setSamples(samples);
		setSampleRate(sampleRate);
	}

	public double[] getSamples() {
		return samples;
	}

	public double getSample(int index) {
		return samples[index];
	}

	public void setSamples(double[] samples, double sampleRate) {
		setSamples(samples);
		setSampleRate(sampleRate);
	}

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

	public double getSampleRate() {
		return sampleRate;
	}

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
	public boolean equals(Object x) {
		if (x == null)
			return false;
		if (this.getClass() != x.getClass())
			return false;
		Sound that = (Sound) x;
		if (that.samplesLength() != this.samplesLength())
			return false;
		for (int i = 0; i < this.samplesLength(); i++) {
			if (that.getSample(i) != this.getSample(i))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Sound [sampleRate=" + sampleRate + "]";
	}

}
