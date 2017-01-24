package local.soundanalysis.model;

import java.io.Serializable;

/**
 * 
 * 
 *
 */
public class Complex implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2181914414777467494L;
	
	private double real; // the real part
	private double imaginary; // the imaginary part

	/**
	 * 
	 * @param sample
	 * @return
	 */
	public static Complex Parse(double sample) {
		return new Complex(sample, 0);
	}

	/**
	 * 
	 * @param samples
	 * @return
	 */
	public static Complex[] Parse(double[] samples) {
		int length = samples.length;
		Complex[] ret = new Complex[length];
		for (int i = 0; i < length; i++) {
			ret[i] = Parse(samples[i]);
		}
		return ret;
	}

	/**
	 * 
	 * @param realPart
	 * @param imaginaryPart
	 */
	public Complex(double realPart, double imaginaryPart) {
		this.real = realPart;
		this.imaginary = imaginaryPart;
	}

	/**
	 * 
	 * @return
	 */
	public double getReal() {
		return real;
	}

	/**
	 * 
	 * @return
	 */
	public double getImaginary() {
		return imaginary;
	}

	/**
	 * 
	 * @return
	 */
	public double abs() {
		return (double) Math.hypot(real, imaginary);
	}

	/**
	 * 
	 * @return
	 */
	public double phase() {
		return (double) Math.atan2(imaginary, real);
	}

	/**
	 * 
	 * @param x
	 * @return
	 */
	public Complex plus(Complex x) {
		return plus(this, x);
	}

	private static Complex plus(Complex a, Complex b) {
		double realPart = a.real + b.real;
		double imaginaryPart = a.imaginary + b.imaginary;
		Complex sum = new Complex(realPart, imaginaryPart);
		return sum;
	}

	/**
	 * 
	 * @param x
	 * @return
	 */
	public Complex minus(Complex x) {
		return minus(this, x);
	}

	private static Complex minus(Complex a, Complex b) {
		double realPart = a.real - b.real;
		double imaginaryPart = a.imaginary - b.imaginary;
		return new Complex(realPart, imaginaryPart);
	}

	/**
	 * 
	 * @param x
	 * @return
	 */
	public Complex times(Complex x) {
		return times(this, x);
	}

	private static Complex times(Complex a, Complex b) {
		double realPart = a.real * b.real - a.imaginary * b.imaginary;
		double imaginaryPart = a.real * b.imaginary + a.imaginary * b.real;
		return new Complex(realPart, imaginaryPart);
	}

	/**
	 * 
	 * @param alpha
	 * @return
	 */
	public Complex scale(double alpha) {
		return new Complex(alpha * real, alpha * imaginary);
	}

	/**
	 * 
	 * @return
	 */
	public Complex conjugate() {
		return new Complex(real, -imaginary);
	}

	/**
	 * 
	 * @return
	 */
	public Complex reciprocal() {
		return reciprocal(this);
	}

	private static Complex reciprocal(Complex x) {
		double scale = x.real * x.real + x.imaginary * x.imaginary;
		return new Complex(x.real / scale, (-1 * x.imaginary) / scale);
	}

	/**
	 * 
	 * @param x
	 * @return
	 */
	public Complex divides(Complex x) {
		return divides(this, x);
	}

	private static Complex divides(Complex a, Complex b) {
		return times(a, reciprocal(b));
	}

	/**
	 * 
	 * @return
	 */
	public Complex exp() {
		return exp(this);
	}

	private static Complex exp(Complex x) {
		return new Complex((double) (Math.exp(x.real) * Math.cos(x.imaginary)),
				(double) (Math.exp(x.real) * Math.sin(x.imaginary)));
	}

	/**
	 * 
	 * @return
	 */
	public Complex sin() {
		return sin(this);
	}

	private static Complex sin(Complex x) {
		return new Complex((double) (Math.sin(x.real) * Math.cosh(x.imaginary)),
				(double) (Math.cos(x.real) * Math.sinh(x.imaginary)));
	}

	/**
	 * 
	 * @return
	 */
	public Complex cos() {
		return cos(this);
	}

	private static Complex cos(Complex x) {
		return new Complex((double) (Math.cos(x.real) * Math.cosh(x.imaginary)),
				(double) (-Math.sin(x.real) * Math.sinh(x.imaginary)));
	}

	/**
	 * 
	 * @return
	 */
	public Complex tan() {
		return tan(this);
	}

	private static Complex tan(Complex x) {
		return divides(sin(x), cos(x));
	}

	/**
	 * 
	 * @return
	 */
	public double getAmplitude() {
		return getAmplitude(this);
	}

	private static double getAmplitude(Complex x) {
		return Math.sqrt((x.imaginary * x.imaginary) + (x.real * x.real));
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	public static double[] getAmplitude(Complex[] c) {
		double[] amps = new double[c.length];
		for (int i = 0; i < c.length; i++) {
			amps[i] = getAmplitude(c[i]);
		}
		return amps;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(imaginary);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(real);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Complex other = (Complex) obj;
		if (Double.doubleToLongBits(imaginary) != Double.doubleToLongBits(other.imaginary))
			return false;
		if (Double.doubleToLongBits(real) != Double.doubleToLongBits(other.real))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if (imaginary == 0)
			return real + "";
		if (real == 0)
			return imaginary + "i";
		if (imaginary < 0)
			return real + " - " + (-imaginary) + "i";
		return real + " + " + imaginary + "i";
	}

}
