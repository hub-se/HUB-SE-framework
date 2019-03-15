/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import se.de.hu_berlin.informatik.utils.compression.CompressedByteArrayToIntSequencesProcessor;
import se.de.hu_berlin.informatik.utils.compression.IntSequencesToCompressedByteArrayProcessor;
import se.de.hu_berlin.informatik.utils.miscellaneous.Abort;
import se.de.hu_berlin.informatik.utils.processors.sockets.module.Module;

/**
 * @author SimHigh
 *
 */
public class CompressedByteArrayToIntSequenceModuleTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("JavaDoc")
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		decoder = new CompressedByteArrayToIntSequencesProcessor().asModule();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	Module<byte[], List<List<Integer>>> decoder;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testOneBitSeqLengthFourFirstZero() throws Exception {
		Module<List<Integer>, byte[]> module = new IntSequencesToCompressedByteArrayProcessor(1, 4, false).asModule();
		
		List<Integer> temp = new ArrayList<>();
		temp.add(0);temp.add(1);temp.add(0);temp.add(1);
		module.submit(temp);
		temp = new ArrayList<>();
		temp.add(1);temp.add(0);temp.add(0);temp.add(1);
		module.submit(temp);
		temp = new ArrayList<>();
		temp.add(0);temp.add(1);temp.add(1);temp.add(0);
		module.submit(temp);
		
		List<List<Integer>> actual = decoder.submit(module.getResultFromCollectedItems()).getResult();
		
		assertEquals(3, actual.size());
		List<Integer> sequence = actual.get(0);
		assertEquals(4, sequence.size());		
		assertEquals(0, sequence.get(0).intValue());
		assertEquals(1, sequence.get(1).intValue());
		assertEquals(0, sequence.get(2).intValue());
		assertEquals(1, sequence.get(3).intValue());
		
		sequence = actual.get(1);
		assertEquals(4, sequence.size());
		assertEquals(1, sequence.get(0).intValue());
		assertEquals(0, sequence.get(1).intValue());
		assertEquals(0, sequence.get(2).intValue());
		assertEquals(1, sequence.get(3).intValue());
		
		sequence = actual.get(2);
		assertEquals(4, sequence.size());
		assertEquals(0, sequence.get(0).intValue());
		assertEquals(1, sequence.get(1).intValue());
		assertEquals(1, sequence.get(2).intValue());
		assertEquals(0, sequence.get(3).intValue());
	}
	
	@Test
	public void testOneBitSeqLengthFourFirstOne() throws Exception {
		Module<List<Integer>, byte[]> module = new IntSequencesToCompressedByteArrayProcessor(1, 4, false).asModule();
		
		List<Integer> temp = new ArrayList<>();
		temp.add(1);temp.add(1);temp.add(0);temp.add(1);
		module.submit(temp);
		temp = new ArrayList<>();
		temp.add(1);temp.add(0);temp.add(0);temp.add(1);
		module.submit(temp);
		temp = new ArrayList<>();
		temp.add(1);temp.add(1);temp.add(1);temp.add(0);
		module.submit(temp);
		
		List<List<Integer>> actual = decoder.submit(module.getResultFromCollectedItems()).getResult();
		
		assertEquals(3, actual.size());
		List<Integer> sequence = actual.get(0);
		assertEquals(4, sequence.size());		
		assertEquals(1, sequence.get(0).intValue());
		assertEquals(1, sequence.get(1).intValue());
		assertEquals(0, sequence.get(2).intValue());
		assertEquals(1, sequence.get(3).intValue());
		
		sequence = actual.get(1);
		assertEquals(4, sequence.size());
		assertEquals(1, sequence.get(0).intValue());
		assertEquals(0, sequence.get(1).intValue());
		assertEquals(0, sequence.get(2).intValue());
		assertEquals(1, sequence.get(3).intValue());
		
		sequence = actual.get(2);
		assertEquals(4, sequence.size());
		assertEquals(1, sequence.get(0).intValue());
		assertEquals(1, sequence.get(1).intValue());
		assertEquals(1, sequence.get(2).intValue());
		assertEquals(0, sequence.get(3).intValue());
	}
	
	@Test
	public void testThreeBitSeqLengthFourFirstOne() throws Exception {
		Module<List<Integer>, byte[]> module = new IntSequencesToCompressedByteArrayProcessor(7, 4, false).asModule();
		
		List<Integer> temp = new ArrayList<>();
		temp.add(7);temp.add(0);temp.add(7);temp.add(0);
		module.submit(temp);
		temp = new ArrayList<>();
		temp.add(7);temp.add(0);temp.add(0);temp.add(7);
		module.submit(temp);
		
		List<List<Integer>> actual = decoder.submit(module.getResultFromCollectedItems()).getResult();
		
		assertEquals(2, actual.size());
		List<Integer> sequence = actual.get(0);
		assertEquals(4, sequence.size());		
		assertEquals(7, sequence.get(0).intValue());
		assertEquals(0, sequence.get(1).intValue());
		assertEquals(7, sequence.get(2).intValue());
		assertEquals(0, sequence.get(3).intValue());
		
		sequence = actual.get(1);
		assertEquals(4, sequence.size());
		assertEquals(7, sequence.get(0).intValue());
		assertEquals(0, sequence.get(1).intValue());
		assertEquals(0, sequence.get(2).intValue());
		assertEquals(7, sequence.get(3).intValue());
	}

	@Test
	public void testTenBitSeqLength2FirstOne() throws Exception {
		Module<List<Integer>, byte[]> module = new IntSequencesToCompressedByteArrayProcessor(999, 2, false).asModule();
		
		List<Integer> temp = new ArrayList<>();
		temp.add(1023);temp.add(63);
		module.submit(temp);
		
		List<List<Integer>> actual = decoder.submit(module.getResultFromCollectedItems()).getResult();
		
		assertEquals(1, actual.size());
		List<Integer> sequence = actual.get(0);
		assertEquals(2, sequence.size());		
		assertEquals(1023, sequence.get(0).intValue());
		assertEquals(63, sequence.get(1).intValue());
	}
	
	@Test
	public void testDelimiterMethod() throws Exception {
		Module<List<Integer>, byte[]> module = new IntSequencesToCompressedByteArrayProcessor(7, false).asModule();
		
		List<Integer> temp = new ArrayList<>();
		temp.add(7);temp.add(2);temp.add(7);temp.add(3);
		module.submit(temp);
		temp = new ArrayList<>();
		temp.add(7);temp.add(2);temp.add(3);temp.add(7);
		module.submit(temp);
		
		List<List<Integer>> actual = decoder.submit(module.getResultFromCollectedItems()).getResult();
		
		assertEquals(2, actual.size());
		List<Integer> sequence = actual.get(0);
		assertEquals(4, sequence.size());		
		assertEquals(7, sequence.get(0).intValue());
		assertEquals(2, sequence.get(1).intValue());
		assertEquals(7, sequence.get(2).intValue());
		assertEquals(3, sequence.get(3).intValue());
		
		sequence = actual.get(1);
		assertEquals(4, sequence.size());
		assertEquals(7, sequence.get(0).intValue());
		assertEquals(2, sequence.get(1).intValue());
		assertEquals(3, sequence.get(2).intValue());
		assertEquals(7, sequence.get(3).intValue());
	}
	
	@Test
	public void testDelimiterMethodWrongNumberInput() throws Exception {
		Module<List<Integer>, byte[]> module = new IntSequencesToCompressedByteArrayProcessor(7, false).asModule();
		
		exception.expect(Abort.class);
		List<Integer> temp = new ArrayList<>();
		temp.add(7);temp.add(0);temp.add(7);temp.add(3);
		module.submit(temp);
	}
}
