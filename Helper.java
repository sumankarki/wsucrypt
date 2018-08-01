import java.io.*;
import java.nio.file.*;

public class Helper {
	
	//F-table, copied from project slide given in class
	private static short[] Ftable = {
		0xa3,0xd7,0x09,0x83,0xf8,0x48,0xf6,0xf4,0xb3,0x21,0x15,0x78,0x99,0xb1,0xaf,0xf9,
		0xe7,0x2d,0x4d,0x8a,0xce,0x4c,0xca,0x2e,0x52,0x95,0xd9,0x1e,0x4e,0x38,0x44,0x28,
		0x0a,0xdf,0x02,0xa0,0x17,0xf1,0x60,0x68,0x12,0xb7,0x7a,0xc3,0xe9,0xfa,0x3d,0x53,
		0x96,0x84,0x6b,0xba,0xf2,0x63,0x9a,0x19,0x7c,0xae,0xe5,0xf5,0xf7,0x16,0x6a,0xa2,
		0x39,0xb6,0x7b,0x0f,0xc1,0x93,0x81,0x1b,0xee,0xb4,0x1a,0xea,0xd0,0x91,0x2f,0xb8,
		0x55,0xb9,0xda,0x85,0x3f,0x41,0xbf,0xe0,0x5a,0x58,0x80,0x5f,0x66,0x0b,0xd8,0x90,
		0x35,0xd5,0xc0,0xa7,0x33,0x06,0x65,0x69,0x45,0x00,0x94,0x56,0x6d,0x98,0x9b,0x76,
		0x97,0xfc,0xb2,0xc2,0xb0,0xfe,0xdb,0x20,0xe1,0xeb,0xd6,0xe4,0xdd,0x47,0x4a,0x1d,
		0x42,0xed,0x9e,0x6e,0x49,0x3c,0xcd,0x43,0x27,0xd2,0x07,0xd4,0xde,0xc7,0x67,0x18,
		0x89,0xcb,0x30,0x1f,0x8d,0xc6,0x8f,0xaa,0xc8,0x74,0xdc,0xc9,0x5d,0x5c,0x31,0xa4,
		0x70,0x88,0x61,0x2c,0x9f,0x0d,0x2b,0x87,0x50,0x82,0x54,0x64,0x26,0x7d,0x03,0x40,
		0x34,0x4b,0x1c,0x73,0xd1,0xc4,0xfd,0x3b,0xcc,0xfb,0x7f,0xab,0xe6,0x3e,0x5b,0xa5,
		0xad,0x04,0x23,0x9c,0x14,0x51,0x22,0xf0,0x29,0x79,0x71,0x7e,0xff,0x8c,0x0e,0xe2,
		0x0c,0xef,0xbc,0x72,0x75,0x6f,0x37,0xa1,0xec,0xd3,0x8e,0x62,0x8b,0x86,0x10,0xe8,
		0x08,0x77,0x11,0xbe,0x92,0x4f,0x24,0xc5,0x32,0x36,0x9d,0xcf,0xf3,0xa6,0xbb,0xac,
		0x5e,0x6c,0xa9,0x13,0x57,0x25,0xb5,0xe3,0xbd,0xa8,0x3a,0x01,0x05,0x59,0x2a,0x46
	};
	
	//Method to read ASCII input from plain text file
	public static byte[][] plainTextFromFile(File plainFile) throws FileNotFoundException {
		try {
			RandomAccessFile file = new RandomAccessFile(plainFile, "r");
			int blocks = (int) Math.ceil(plainFile.length() / 8.0);
			byte[][] input = new byte[blocks][];			
			for (int i = 0; i < input.length; i++) {
				byte[] block = new byte[8];
				file.read(block);
				//For padding in the last block
				if(i + 1 == input.length) {
					int lastBlockLength = (new String(block)).trim().length();
					if(lastBlockLength != 8)
						block[7] = (byte) (8 - lastBlockLength); //bytes to be padded at the end
				}
				input[i] = block;
			}
			file.close();
			return input;
		} catch (Exception e) {
			System.err.println("Plain text file read error: "+e.getMessage());
			System.exit(0);
			return null;
		}		
	}
	
	//Method to read hex string from cipher text.
	public static byte[][] cipherTextFromFile(File f) throws FileNotFoundException {			
		byte[][] input = null;
		try {
			RandomAccessFile file = new RandomAccessFile(f, "r");
			
			byte[] bytes = new byte[(int)file.length()];
			file.readFully(bytes);
			String c = new String(bytes);
			input = new byte[(c.length() + 15) / 16][];
			for (int i = 0; i < input.length; i++) {
				byte[] block = hexStringToBytesArray(c.substring(0, 16));
				input[i] = block;
				c = c.substring(16, c.length());
			}
			file.close();			
		} catch (Exception e) {
			System.err.println("File not found or couldnot read the file");
			System.exit(0);
		}
		return input;
	}
	
	//Method to write given string in given filename
	public static void writeInFileString(String fileName, String text) {
		try {
			FileWriter fw = new FileWriter(fileName, true);
			fw.write(text);
			fw.close();
		} catch (Exception e) {
			throw new IllegalStateException("Could not write to file", e);
		}
	}	
	
	//Method to get key from given file name
	public static byte[] getKey(String keyTextFile){
		byte[] keys = null;
		try {
			String hexKeyString = new String(Files.readAllBytes(new File(keyTextFile).toPath()));
			//String hexKeyString = "abcdef01234567891234"; 
			keys = Helper.hexStringToBytesArray(hexKeyString.trim());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return keys;
	}
	
	//Method for whitening
	public static void whiteningStep(byte[] inputByte, byte[] key){
		for (int i = 0; i < 8; i++){
			inputByte[i] = (byte) (inputByte[i] ^ key[i]);
		}
	}
	
	//Method to compute F function
	public static short[] F_function(short R0, short R1, int roundCounter, boolean encryption, byte[] key){
		short[] F0_1 = new short[2];
		byte[] subKeys = new byte[12];
		if(encryption){
			subKeys[0] = K(4*roundCounter, encryption, key);
			subKeys[1] = K(4*roundCounter + 1, encryption, key);
			subKeys[2] = K(4*roundCounter + 2, encryption, key);
			subKeys[3] = K(4*roundCounter + 3, encryption, key);
			subKeys[4] = K(4*roundCounter, encryption, key);
			subKeys[5] = K(4*roundCounter + 1, encryption, key);
			subKeys[6] = K(4*roundCounter + 2, encryption, key);
			subKeys[7] = K(4*roundCounter + 3, encryption, key);
			subKeys[8] = K(4*roundCounter, encryption, key);
			subKeys[9] = K(4*roundCounter + 1, encryption, key);
			subKeys[10] = K(4*roundCounter + 2, encryption, key);
			subKeys[11] = K(4*roundCounter + 3, encryption, key);
		}
		else{
			roundCounter = 19 - roundCounter;
			subKeys[11] = K(4*roundCounter + 3, encryption, key);
			subKeys[10] = K(4*roundCounter + 2, encryption, key);
			subKeys[9] = K(4*roundCounter + 1, encryption, key);
			subKeys[8] = K(4*roundCounter, encryption, key);
			subKeys[7] = K(4*roundCounter + 3, encryption, key);
			subKeys[6] = K(4*roundCounter + 2, encryption, key);
			subKeys[5] = K(4*roundCounter + 1, encryption, key);
			subKeys[4] = K(4*roundCounter, encryption, key);
			subKeys[3] = K(4*roundCounter + 3, encryption, key);
			subKeys[2] = K(4*roundCounter + 2, encryption, key);
			subKeys[1] = K(4*roundCounter + 1, encryption, key);
			subKeys[0] = K(4*roundCounter, encryption, key);
		}
		
		//Compute T0 abd T1 from G_Function
		short T0 = G_function(R0, roundCounter, encryption, key, subKeys[0], subKeys[1], subKeys[2], subKeys[3]);
		short T1 = G_function(R1, roundCounter, encryption, key, subKeys[4], subKeys[5], subKeys[6], subKeys[7]);
		
		//For F0
		short k8_k9 = (short) ((subKeys[8] << 8) | (subKeys[9] & 0xff));		//Concatenate k8, k9
		F0_1[0] = (short) (T0 + 2*T1 + k8_k9); 
		
		//For F1
		short k10_k11 = (short) ((subKeys[10] << 8) | (subKeys[11] & 0xff));		//Concatenate k10, k11
		F0_1[1] = (short) (2*T0 + T1 + k10_k11); 

		return F0_1;
	}
	
	//Method to compute G function
	public static short G_function(short w, int roundCounter, boolean encryption, byte[] key, byte k0,  byte k1,  byte k2,  byte k3){
		short g5_g6 = w;
		byte g1, g2, g3, g4, g5, g6;
		//Compute g1 and g2 first
		g1 = (byte) (w >>> 8);
		g2 = (byte) (w & 0x00FF);
		
		//Compute g3, g4, g5, g6
		g3 = (byte) (Ftable[(g2 ^ k0) & 0xFF] ^ g1);
		g4 = (byte) (Ftable[(g3 ^ k1) & 0xFF] ^ g2);
		g5 = (byte) (Ftable[(g4 ^ k2) & 0xFF] ^ g3);
		g6 = (byte) (Ftable[(g5 ^ k3) & 0xFF] ^ g4);
		
		//concatenating bytes
		g5_g6 = (short) ((g5 << 8) | (g6 & 0xff));
		return g5_g6;
	}
	
	//Method to generate sub keys
	public static byte K(int x, boolean encryption, byte[] key){
		if(encryption){
			//For Encryption, left rotate by 1-bit and return key
			shiftBitsLeft(key, 1);
			int byteNumber = 9 - x % 10;
			return key[byteNumber];
		} else {
			//For Decryption first return key then only rotate right.
			byte[] tempKey = key.clone();
			shiftBitsRight(key, 1);
			int byteNumber = 9 - x % 10;
			return tempKey[byteNumber];
		}
	}
	
	//[Copied from StackOverflow]
	public static String HexToBinary(String hexString) {
		hexString = hexString.toUpperCase();
		String[] hexArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F" };
		String[] binaryArray = { "0000", "0001", "0010", "0011", "0100",
				"0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100",
				"1101", "1110", "1111" };
		String binaryString = "";
		for (int i = 0; i < hexString.length(); i++) {
			char hexChar = hexString.charAt(i);
			String hexCharVer = "" + hexChar + "";
			for (int j = 0; j < hexArray.length; j++) {
				if (hexCharVer.equalsIgnoreCase(hexArray[j])) {
					binaryString = binaryString + binaryArray[j];
				}
			}
		}
		return binaryString;
	}
	
	/* 8 bit works fine
	public static byte[] rotateRight(byte[] value, int shift)
	{
		long BitMask = 0x1L << 63;
		long keyValue = 0;
		for (int i = 0; i < value.length; i++)
			keyValue = (keyValue << 8) + (value[i] & 0xff);
		
		keyValue = (keyValue >> shift) | ((keyValue & BitMask) >>> 64);
		return longToBytes(keyValue);
	}
	public static byte[] rotateLeft(byte[] value, int shift)
	{
		long BitMask = 0x1L << 63;
		long keyValue = 0;
		for (int i = 0; i < value.length; i++)
			keyValue = (keyValue << 8) + (value[i] & 0xff);
		
		keyValue = (keyValue << shift) | ((keyValue & BitMask) >>> 63);
		return longToBytes(keyValue);
	}*/
	
	//[Copied from Stack overflow for right shift, but modified this for left shift]
	public static void shiftBitsRight(byte[] bytes, final int rightShifts) {
	   assert rightShifts >= 1 && rightShifts <= 7;

	   final int leftShifts = 8 - rightShifts;

	   byte previousByte = bytes[0]; // keep the byte before modification
	   bytes[0] = (byte) (((bytes[0] & 0xff) >> rightShifts) | ((bytes[bytes.length - 1] & 0xff) << leftShifts));
	   for (int i = 1; i < bytes.length; i++) {
	      byte tmp = bytes[i];
	      bytes[i] = (byte) (((bytes[i] & 0xff) >> rightShifts) | ((previousByte & 0xff) << leftShifts));
	      previousByte = tmp;
	   }
	}
	
	public static void shiftBitsLeft(byte[] bytes, final int rightShifts) {
	   assert rightShifts >= 1 && rightShifts <= 7;

	   final int leftShifts = 8 - rightShifts;
	   
	   byte previousByte = bytes[bytes.length - 1]; // keep the byte before modification
	   bytes[bytes.length - 1] = (byte) (((bytes[bytes.length - 1] & 0xff) << rightShifts) | ((bytes[0] & 0xff) >> leftShifts));
	   for (int i = bytes.length - 2; i >= 0; i--) {
	      byte tmp = bytes[i];
	      bytes[i] = (byte) (((bytes[i] & 0xff) << rightShifts) | ((previousByte & 0xff) >> leftShifts));
	      previousByte = tmp;
	   }
	}
	
	//Rotation for short, //Copied from Stackoverflow
	public static short rotateRightShort(short x) {
		return (short)(((x & 0xffff) >>> 1) | ((x & 0x1) << 15));
	}
	
	//Copied from Stackoverflow
	public static short rotateLeftShort(short x) {
		return (short)(((x & 0xffff) << 1) | ((x & 0x8000) >>> 15));
	}
	
	//Copied from Stackoverflow
	public static byte[] hexStringToBytesArray(String hexString){
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len ; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i+1), 16));
		}
		return data;
	}
	
	//[Copied from Stackoverflow]
	public static char[] HEX_CHARS = "0123456789abcdef".toCharArray();
	public static String bytesArrayToHexString(byte[] buf) {
		char[] chars = new char[2 * buf.length];
	    for (int i = 0; i < buf.length; ++i){
	        chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
	        chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}
}
