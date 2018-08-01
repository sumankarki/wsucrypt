import java.io.*;

public class EncryptDecrypt {
	String keyTextFile;
	String plainTextFile;
	String cipherTextFile;
	
	public EncryptDecrypt(String keyFile, String plainFile, String cipherFile){
		this.keyTextFile = keyFile;
		this.plainTextFile = plainFile;
		this.cipherTextFile = cipherFile;
	}
	
	public void startProcess(boolean encrypt) throws FileNotFoundException{
		//Get bytes of key, Read Key
		byte[] key = Helper.getKey(keyTextFile);
		
		//To do cipher operation
		byte[][] textByte = null;
		
		if(encrypt){
			//Check if plain text file exists
			File plainFile = new File(plainTextFile);
			if(!plainFile.exists() || plainFile.isDirectory()){
				System.out.println("Please provide valid plain text file path.");
				System.exit(0);
			}
			System.out.println("***Encrypting***");
			System.out.println();
			//Get plain text and convert it to bytes
			textByte = Helper.plainTextFromFile(new File(plainTextFile));		
		} else {
			//Check if cipher text file exists
			File cipherFile = new File(cipherTextFile);
			if(!cipherFile.exists() || cipherFile.isDirectory()){
				System.out.println("Please provide valid cipher text file path.");
				System.exit(0);
			}
			System.out.println("***Decrypting***");
			System.out.println();
			//Get cipher text and convert it to bytes
			textByte = Helper.cipherTextFromFile(new File(cipherTextFile));	
		}
		
		//Run round for every plain text block
		for(int blockCount = 0; blockCount < textByte.length; blockCount++){
			//Current plain text block
			byte[] currentBlock = textByte[blockCount];
			
			if(encrypt){
				System.out.println("Plain text: " +(new String(currentBlock)));
				System.out.println("Plain text Hex: " +Helper.bytesArrayToHexString(currentBlock));
			} else
				System.out.println("Cipher text Hex: " +Helper.bytesArrayToHexString(currentBlock));			
			
			//Step 1: Whitening step
			Helper.whiteningStep(currentBlock, key);
			
			//For each rounds 1 -> 16, 20
			for(int round = 0; round < 20; round++){
				//System.out.println("Start Round "+round+ " : " +Helper.bytesArrayToHexString(currentBlock));
				// Compute R0, R1, R2, R3, [ Byte to short is copied from stack overflow ]
				short R0 = (short) ((currentBlock[0] << 8) | (currentBlock[1] & 0xff));	
				short R1 = (short) ((currentBlock[2] << 8) | (currentBlock[3] & 0xff));	
				short R2 = (short) ((currentBlock[4] << 8) | (currentBlock[5] & 0xff));	
				short R3 = (short) ((currentBlock[6] << 8) | (currentBlock[7] & 0xff));
				
				//Compute F funtion, we will get F0 and F1
				short[] F0_1 = Helper.F_function(R0, R1, round, encrypt, key);
				
				//Swapping the cipher block block, R2 = R0 and R3 = R1 i
				currentBlock[4] = currentBlock[0];
				currentBlock[5] = currentBlock[1];
				currentBlock[6] = currentBlock[2];
				currentBlock[7] = currentBlock[3];
				
				// For R0 ==> XOR( F0 and R2) and then shift right the results by 1 bit, opposite for decryption
				if(encrypt) { 
					R2 = (short)(F0_1[0] ^ R2);
					R0 = Helper.rotateRightShort(R2);
				} else { 
					R2 = Helper.rotateLeftShort(R2);
					R0 = (short)(F0_1[0] ^ R2);
				}
				currentBlock[0] = (byte) (R0 >>> 8);
				currentBlock[1] = (byte) R0;
				
				// For R1 ==> rotate R3 left by 1 bit and XOR the results with F1, opposite for decryption
				if (encrypt) {
					R3 = Helper.rotateLeftShort(R3);
					R1 = (short)(R3 ^ F0_1[1]);
				} else {
					R3 = (short)(R3 ^ F0_1[1]);
					R1 = Helper.rotateRightShort(R3);
				}
				currentBlock[2] = (byte) (R1 >>> 8);
				currentBlock[3] = (byte) R1;
				//System.out.println("End Round "+round+ " : " +Helper.bytesArrayToHexString(currentBlock));
			} //For loop rounds end
			
			//Swap the cipher block last time, R0<-R2, R1<-R3, R2<-R0 and R3<-R1
			//For R0 and R2
			byte temp1 = currentBlock[4];
			byte temp2 = currentBlock[5];
			currentBlock[4] = currentBlock[0];
			currentBlock[5] = currentBlock[1];
			currentBlock[0] = temp1;
			currentBlock[1] = temp2;
			//For R1 and R3
			temp1 = currentBlock[6];
			temp2 = currentBlock[7];
			currentBlock[6] = currentBlock[2];
			currentBlock[7] = currentBlock[3];
			currentBlock[2] = temp1;
			currentBlock[3] = temp2;

			//Last step: Whitening once again
			Helper.whiteningStep(currentBlock, key);
			
			if(encrypt){
				//For each cipher block, convert it to hex string and write in file
				System.out.println("Cipher Text Hex: "+Helper.bytesArrayToHexString(currentBlock));
				Helper.writeInFileString(cipherTextFile, Helper.bytesArrayToHexString(currentBlock));
			} else {
				//For each plain text block, convert it to ASCII and write in file
				System.out.println("Plain Text Hex: "+ Helper.bytesArrayToHexString(currentBlock));
				String text = new String(currentBlock);
				System.out.println("Plain Text Hex: "+ text);
				Helper.writeInFileString(plainTextFile, text);
			}
			System.out.println();
		}		
		System.out.println("***Operation Completed***");
	}
	
}
