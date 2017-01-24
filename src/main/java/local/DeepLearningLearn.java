package local;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Scanner;

import local.soundanalysis.algorithm.LinearPredictive;
import local.soundanalysis.algorithm.MelFrequencyAnalysis;
import local.soundanalysis.extractor.SoundExtractor;
import local.soundanalysis.extractor.VoiceExtractor;
import local.soundanalysis.machinelearning.LearningCore;
import local.soundanalysis.model.Coefficients;
import local.soundanalysis.model.Signatures;
import local.soundanalysis.model.signal.Sound;
import local.soundanalysis.util.AudioRecorder;
import local.soundanalysis.util.Printer;

public class DeepLearningLearn {

	public static final int LPC_SIGNATURE_LENGTH = 20;
	public static final int MFCC_SIGNATURE_LENGTH = 13;
	public static final float SAMPLE_RATE = 22050f;
	public static final int BIT_DEPTH = 16;
	public static final int RECORDING_LENGTH = 5;
	public static final int SEED = 128;
	public static final int ITERATION = 1000;
	public static final double LEARNING_RATE = 0.01;
	public static final int NEURON_OUT = 2;

	public static LearningCore learningCore;

	public static double lpcError = 0.005;
	public static double mfccError = 0.05;

	public static void main(String[] args) {
		learningCore = get();
		if (learningCore == null)
			learningCore = new LearningCore(SEED, ITERATION, LEARNING_RATE,
					LPC_SIGNATURE_LENGTH + MFCC_SIGNATURE_LENGTH, NEURON_OUT, 16);
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("enter l to learn, enter t to test, enter s to save what machine has learnt");
			String input = scanner.next();
			input.toLowerCase();
			if (input.equals("l"))
				learn(recordSignatures());
			else if (input.equals("t"))
				test(recordSignatures());
			else if (input.equals("s"))
				save(learningCore);
		}
	}

	@SuppressWarnings("resource")
	private static void save(LearningCore learningCore) {
		try {
			FileOutputStream fOut = new FileOutputStream("learningCore.dat");
			ObjectOutputStream objOut = new ObjectOutputStream(fOut);
			objOut.writeObject(learningCore);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private static LearningCore get() {
		try {
			FileInputStream fIn = new FileInputStream("learningCore.dat");
			ObjectInputStream objIn = new ObjectInputStream(fIn);
			Object obj = objIn.readObject();
			if (obj instanceof LearningCore)
				return (LearningCore) obj;
			else
				return null;
		} catch (FileNotFoundException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public static Signatures recordSignatures() {
		Signatures mfcc = null;
		Coefficients lpc = null;
		try {
			System.out.println("Recording...");
			Sound sound = AudioRecorder.record(SAMPLE_RATE, BIT_DEPTH, RECORDING_LENGTH);
			System.out.println("Finished recording");

			Printer.printSound(sound, "sound.txt");
			Printer.printSpectrum(sound, "soundSpectrum.txt");

			SoundExtractor<Sound, Sound> extractor = new VoiceExtractor();
			Sound voice = extractor.extract(sound);

			Printer.printSound(voice, "voice.txt");
			Printer.printSpectrum(voice, "voiceSpectrum.txt");

			mfcc = MelFrequencyAnalysis.extractSignatures(voice, MFCC_SIGNATURE_LENGTH);
			lpc = LinearPredictive.extractSignatures(voice, LPC_SIGNATURE_LENGTH);

			System.out.println(mfcc.toString());
			System.out.println(lpc.toString());

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Signatures.mergeSignatures(new Signatures[] { lpc, mfcc });
	}

	public static void learn(Signatures signatures) {
		System.out.println("Learning....");
		learningCore.learnNewFeature(signatures.getSignatures(), new double[] { 1, 0 });
		System.out.println("Finished learning....");
	}

	public static void test(Signatures signatures) {
		System.out.println("Test");
		double[] result = learningCore.test(signatures.getSignatures());
		DecimalFormat df = new DecimalFormat("#.#");
		df.setMaximumFractionDigits(2);
		System.out.println(
				"this voice have " + df.format(result[0] * 100.0) + "% possibility come from learned user's voices");
	}

}
