/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import se.de.hu_berlin.informatik.utils.compression.NumberArrayToCompressedByteArrayModule;

/**
 * @author SimHigh
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class NumberArrayToCompressedByteArrayModuleTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOneBitSeqLengthFourFirstZero() throws Exception {
		NumberArrayToCompressedByteArrayModule<Integer> module = new NumberArrayToCompressedByteArrayModule<>(1, 4);
		
		Integer[] temp = {
				0,1,0,1,
				1,0,0,1,
				0,1,1,0
				};
		module.submitAndStart(temp);
		
		byte[] expected = { 1, 0, 0, 0, 4, 0, 0, 0, 3, 
				(byte)Integer.parseInt("01011001", 2), (byte)Integer.parseInt("01100000", 2) };
		byte[] actual = module.getResult();
		assertEquals(expected.length, actual.length);
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void testOneBitSeqLengthFourFirstOne() throws Exception {
		NumberArrayToCompressedByteArrayModule<Integer> module = new NumberArrayToCompressedByteArrayModule<>(1, 4);
		
		Integer[] temp = {
				1,1,0,1,
				1,0,0,1,
				1,1,1,0
				};
		module.submitAndStart(temp);
		
		byte[] expected = { 1, 0, 0, 0, 4, 0, 0, 0, 3, 
				(byte)Integer.parseInt("11011001", 2), (byte)Integer.parseInt("11100000", 2) };
		byte[] actual = module.getResult();
		assertEquals(expected.length, actual.length);
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void testThreeBitSeqLengthFourFirstOne() throws Exception {
		NumberArrayToCompressedByteArrayModule<Integer> module = new NumberArrayToCompressedByteArrayModule<>(7, 4);
		
		Integer[] temp = {
				7,0,7,0,
				7,0,0,7
				};
		module.submitAndStart(temp);
		
		byte[] expected = { 3, 0, 0, 0, 4, 0, 0, 0, 2, 
				(byte)Integer.parseInt("11100011", 2), (byte)Integer.parseInt("10001110", 2), (byte)Integer.parseInt("00000111", 2) };
		byte[] actual = module.getResult();
		assertEquals(expected.length, actual.length);
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testTenBitSeqLength2FirstOne() throws Exception {
		NumberArrayToCompressedByteArrayModule<Integer> module = new NumberArrayToCompressedByteArrayModule<>(999, 2);
		
		Integer[] temp = {
				1023,63
				};
		module.submitAndStart(temp);
		
		byte[] expected = { 10, 0, 0, 0, 2, 0, 0, 0, 1, 
				(byte)Integer.parseInt("11111111", 2), (byte)Integer.parseInt("11000011", 2), (byte)Integer.parseInt("11110000", 2) };
		byte[] actual = module.getResult();
		assertEquals(expected.length, actual.length);
		assertArrayEquals(expected, actual);
	}
}
