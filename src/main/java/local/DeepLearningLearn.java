package local;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Scanner;

import local.soundanalysis.extractor.SoundExtractor;
import local.soundanalysis.extractor.EmaVoiceExtractor;
import local.soundanalysis.machinelearning.VoiceLearningCore;
import local.soundanalysis.model.signal.Sound;
import local.soundanalysis.util.AudioRecorder;
import local.soundanalysis.util.Printer;

public class DeepLearningLearn {

	public static final float SAMPLE_RATE = 22050f;
	public static final int BIT_DEPTH = 16;
	public static final int RECORDING_LENGTH = 5;
	public static final int SEED = 128;
	public static final int ITERATION = 1000;
	public static final double LEARNING_RATE = 0.01;
	public static final int NEURON_OUT = 2;

	public static VoiceLearningCore learningCore;

	public static double lpcError = 0.005;
	public static double mfccError = 0.05;

	public static void main(String[] args) {
		learningCore = get();
		if (learningCore == null)
			learningCore = new VoiceLearningCore(SEED, ITERATION, LEARNING_RATE,
					NEURON_OUT);
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
	private static void save(VoiceLearningCore learningCore) {
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
	private static VoiceLearningCore get() {
		try {
			FileInputStream fIn = new FileInputStream("learningCore.dat");
			ObjectInputStream objIn = new ObjectInputStream(fIn);
			Object obj = objIn.readObject();
			if (obj instanceof VoiceLearningCore)
				return (VoiceLearningCore) obj;
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

	public static Sound recordSignatures() {
		Sound voice = null;
		try {
			System.out.println("Recording...");
			Sound sound = AudioRecorder.record(SAMPLE_RATE, BIT_DEPTH, RECORDING_LENGTH);
			System.out.println("Finished recording");

			Printer.printSound(sound, "sound.txt");
			Printer.printSpectrum(sound, "soundSpectrum.txt");

			SoundExtractor<Sound> extractor = new EmaVoiceExtractor();
			voice = extractor.extract(sound);

			Printer.printSound(voice, "voice.txt");
			Printer.printSpectrum(voice, "voiceSpectrum.txt");

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return voice;
	}

	public static void learn(Sound sound) {
		System.out.println("Learning....");
		learningCore.learnNewVoice(sound, new double[] { 1, 0 });
		System.out.println("Finished learning....");
	}

	public static void test(Sound sound) {
		System.out.println("Test");
		double[] result = learningCore.testVoice(sound);
		DecimalFormat df = new DecimalFormat("#.#");
		df.setMaximumFractionDigits(2);
		System.out.println(
				"this voice have " + df.format(result[0] * 100.0) + "% possibility come from learned user's voices");
	}

}
