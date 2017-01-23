package local.soundanalysis.model;

import java.util.Arrays;

public class Coeficients extends Signatures {
	private double error;

	public Coeficients(double[] signatures, double error) throws IllegalArgumentException {
		super(signatures);
		this.error = error;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	@Override
	public boolean equals(Object x) {
		if (x == null)
			return false;
		if (this.getClass() != x.getClass())
			return false;
		Coeficients that = (Coeficients) x;
		if (that.length() != this.length())
			return false;
		for (int i = 0; i < this.length(); i++) {
			if (that.getSignature(i) != this.getSignature(i))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Coeficients [error=" + error + ", " + "signatures=" + Arrays.toString(getSignatures()) + "]";
	}

}
