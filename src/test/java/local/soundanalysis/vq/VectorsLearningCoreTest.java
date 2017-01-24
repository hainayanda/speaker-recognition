package local.soundanalysis.vq;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class VectorsLearningCoreTest {

	private static VectorsLearningCore learningCore;
	private static Vectors[] vectors;
	private static boolean[] expectedResult;

	@BeforeClass
	public static void setup() {
		learningCore = new VectorsLearningCore("test");
		learningCore.train(new Vectors(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
		learningCore.train(new Vectors(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
		learningCore.train(new Vectors(new int[] { 1, 1, 3, 5, 5, 5, 7, 9, 9 }));
		learningCore.train(new Vectors(new int[] { 2, 2, 2, 4, 6, 6, 6, 8, 10 }));
		vectors = new Vectors[] { new Vectors(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }),
				new Vectors(new int[] { 3, 4, 5, 6, 7, 8, 9, 10, 11 }),
				new Vectors(new int[] { 5, 6, 7, 8, 9, 10, 11, 12, 13 }),
				new Vectors(new int[] { -1, 0, 1, 2, 3, 4, 5, 6, 7 }),
				new Vectors(new int[] { -5, -4, -3, -2, -1, 0, 1, 2, 3 }) };
	}

	@Test
	public void testLearning() {
		assertEquals(learningCore.toString(), 3, learningCore.size());
	}

	@Test
	public void testIsInVectors() {
		int error = 2;
		expectedResult = new boolean[] { true, true, false, true, false };
		for (int i = 0; i < vectors.length; i++) {
			assertEquals(learningCore.isInVectors(vectors[i], error), expectedResult[i]);
		}

		error = 4;
		expectedResult = new boolean[] { true, true, true, true, false };
		for (int i = 0; i < vectors.length; i++) {
			assertEquals(learningCore.isInVectors(vectors[i], error), expectedResult[i]);
		}

		error = 6;
		expectedResult = new boolean[] { true, true, true, true, true };
		for (int i = 0; i < vectors.length; i++) {
			assertEquals(learningCore.isInVectors(vectors[i], error), expectedResult[i]);
		}

		error = 1;
		expectedResult = new boolean[] { true, false, false, false, false };
		for (int i = 0; i < vectors.length; i++) {
			assertEquals(learningCore.isInVectors(vectors[i], error), expectedResult[i]);
		}
	}
}
