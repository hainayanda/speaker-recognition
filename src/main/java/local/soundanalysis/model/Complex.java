package local.soundanalysis.model;

public class Complex {

	private double real; // the realPart part
	private double imaginary; // the imaginaryPartinary part

	public static Complex Parse(double sample) {
		return new Complex(sample, 0);
	}

	public static Complex[] Parse(double[] samples) {
		int length = samples.length;
		Complex[] ret = new Complex[length];
		for (int i = 0; i < length; i++) {
			ret[i] = Parse(samples[i]);
		}
		return ret;
	}

	public Complex(double realPart, double imaginaryPart) {
		this.real = realPart;
		this.imaginary = imaginaryPart;
	}

	public double getReal() {
		return real;
	}

	public double getImaginary() {
		return imaginary;
	}

	public double abs() {
		return (double) Math.hypot(real, imaginary);
	}

	public double phase() {
		return (double) Math.atan2(imaginary, real);
	}

	public Complex plus(Complex x) {
		return plus(this, x);
	}

	private static Complex plus(Complex a, Complex b) {
		double realPart = a.real + b.real;
		double imaginaryPart = a.imaginary + b.imaginary;
		Complex sum = new Complex(realPart, imaginaryPart);
		return sum;
	}

	public Complex minus(Complex x) {
		return minus(this, x);
	}

	private static Complex minus(Complex a, Complex b) {
		double realPart = a.real - b.real;
		double imaginaryPart = a.imaginary - b.imaginary;
		return new Complex(realPart, imaginaryPart);
	}

	public Complex times(Complex x) {
		return times(this, x);
	}

	private static Complex times(Complex a, Complex b) {
		double realPart = a.real * b.real - a.imaginary * b.imaginary;
		double imaginaryPart = a.real * b.imaginary + a.imaginary * b.real;
		return new Complex(realPart, imaginaryPart);
	}

	public Complex scale(double alpha) {
		return new Complex(alpha * real, alpha * imaginary);
	}

	public Complex conjugate() {
		return new Complex(real, -imaginary);
	}

	public Complex reciprocal() {
		return reciprocal(this);
	}

	private static Complex reciprocal(Complex x) {
		double scale = x.real * x.real + x.imaginary * x.imaginary;
		return new Complex(x.real / scale, (-1 * x.imaginary) / scale);
	}

	public Complex divides(Complex x) {
		return divides(this, x);
	}

	private static Complex divides(Complex a, Complex b) {
		return times(a, reciprocal(b));
	}

	public Complex exp() {
		return exp(this);
	}

	private static Complex exp(Complex x) {
		return new Complex((double) (Math.exp(x.real) * Math.cos(x.imaginary)),
				(double) (Math.exp(x.real) * Math.sin(x.imaginary)));
	}

	public Complex sin() {
		return sin(this);
	}

	private static Complex sin(Complex x) {
		return new Complex((double) (Math.sin(x.real) * Math.cosh(x.imaginary)),
				(double) (Math.cos(x.real) * Math.sinh(x.imaginary)));
	}

	public Complex cos() {
		return cos(this);
	}

	private static Complex cos(Complex x) {
		return new Complex((double) (Math.cos(x.real) * Math.cosh(x.imaginary)),
				(double) (-Math.sin(x.real) * Math.sinh(x.imaginary)));
	}

	public Complex tan() {
		return tan(this);
	}

	private static Complex tan(Complex x) {
		return divides(sin(x), cos(x));
	}

	public double getAmplitude() {
		return getAmplitude(this);
	}

	private static double getAmplitude(Complex x) {
		return Math.sqrt((x.imaginary * x.imaginary) + (x.real * x.real));
	}

	public static double[] getAmplitude(Complex[] c) {
		double[] amps = new double[c.length];
		for (int i = 0; i < c.length; i++) {
			amps[i] = getAmplitude(c[i]);
		}
		return amps;
	}

	@Override
	public boolean equals(Object x) {
		if (x == null)
			return false;
		if (this.getClass() != x.getClass())
			return false;
		Complex that = (Complex) x;
		return (this.real == that.real) && (this.imaginary == that.imaginary);
	}

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
