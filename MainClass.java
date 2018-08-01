import java.io.*;

public class MainClass {

	public static void main(String[] args) throws FileNotFoundException{
		// TODO Auto-generated method stub
		if(args.length == 4){
			String keyTextFile = args[0];
			String plainTextFile = args[1];
			String cipherTextFile = args[2];
			String cipherOperation = args[3];

			/*
			String keyTextFile = "D:\\key.txt";
			String plainTextFile = "D:\\plaintext.txt";
			String cipherTextFile = "D:\\cyphertext.txt";
			String cipherOperation = "encrypt";
			*/
			
			//For Encryption or decryption
			if(cipherOperation.toLowerCase().equals("encrypt")){
				EncryptDecrypt encryptObject = new EncryptDecrypt(keyTextFile, plainTextFile, cipherTextFile);
				encryptObject.startProcess(true);
			} else {
				EncryptDecrypt encryptObject = new EncryptDecrypt(keyTextFile, plainTextFile, cipherTextFile);
				encryptObject.startProcess(false);
			}
		} else {
			System.out.println("***** Usage *****");
			System.out.println();
			System.out.println("* FOR ENCRYPTION *");
			System.out.println("java MainClass 'key-text-file-path' 'plain-text-file-path' 'cipher-text-file-path-to-be-saved-with-txt-extension' 'encrypt'");
			
			System.out.println();
			System.out.println("* FOR DECRYPTION *");
			System.out.println("java MainClass 'key-text-file-path' 'plain-text-file-path-to-be-saved-with-txt-extension' 'cipher-text-file-path' 'decrypt'");
		}
	}
}
