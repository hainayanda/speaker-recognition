package local.soundanalysis.util;

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import local.soundanalysis.model.signal.Sound;

import static local.soundanalysis.util.Utils.*;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class AudioRecorder {

	private AudioRecorder() {
	}

	/**
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Sound record() throws IllegalArgumentException {
		return record(16000f, 16, 5);
	}

	/**
	 * 
	 * @param sampleRate
	 * @param bitDepth
	 * @param length
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Sound record(float sampleRate, int bitDepth, int length) throws IllegalArgumentException {
		if (bitDepth % 8 != 0)
			throw new IllegalArgumentException("bitDepth parameter must be in byte size, you are trying to input "
					+ bitDepth + " as parameter, which is incorrect");
		else if (length <= 0)
			throw new IllegalArgumentException("length parameter must be greater than 0, you are trying to input "
					+ bitDepth + " as parameter, which is incorrect");

		AudioFormat format = new AudioFormat(sampleRate, bitDepth, 1, true, true);
		TargetDataLine microphone;
		try {
			microphone = AudioSystem.getTargetDataLine(format);

			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);

			int numBytesRead;
			int CHUNK_SIZE = 1024;
			byte[] data;

			int bytesMaxSize = length * ((int) sampleRate * (bitDepth) / 8);
			int bytesRead = 0;
			List<byte[]> bytes = new LinkedList<byte[]>();
			List<Integer> reads = new LinkedList<Integer>();
			microphone.start();
			while (bytesRead < bytesMaxSize) {
				data = new byte[microphone.getBufferSize() / 5];
				numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
				bytesRead += numBytesRead;
				reads.add(numBytesRead);
				bytes.add(data);
			}
			microphone.close();
			byte[] sampleData = getByteArrayFromList(bytes, reads, bytesRead);

			double[] samples = getSamplesFromBytes(sampleData, bitDepth);
			return new Sound(samples, (int) sampleRate);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static double[] getSamplesFromBytes(byte[] sampleData, int bitDepth) {
		int byteDepth = bitDepth / 8;
		double[] samples = new double[sampleData.length / byteDepth];
		for (int i = 0; i < samples.length; i++) {
			int temp = 0;
			for (int j = 0; j < byteDepth; j++) {
				int temp1 = (int) sampleData[j + (i * byteDepth)];
				temp1 = temp1 << (8 * j);
				temp = temp | temp1;
			}
			samples[i] = ((double) temp) / (Math.pow(2, bitDepth));
		}
		return samples;
	}
}
