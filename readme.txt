This WSU crypt reads 80 bit key from file and perform encryption and decryption.

Assuming the key in key file is 80 bits and in hexadicemal format and java version is 1.8

For Encryption:
	1. Place the java files ( MainClass.java, Helper.java and EncryptDecrypt.java ) in a folder. For simplicity also place key.txt, plaintext.txt abd ciphertext.txt in the same folder. )
	2. Build java files (" javac *.java ")
	3. Run the program using command:
		"java MainClass 'key-text-file-path' 'plain-text-file-path-with-filename' 'cipher-text-file-path-to-be-saved-with-filename' 'encrypt'"
	e.g., "java MainClass key.txt plaintext.txt ciphertext.txt encrypt"


For Decryption:
	Everything is same as encryption except run command.
	1. Run the program using command:
		"java MainClass 'key-text-file-path' 'plain-text-file-path-with-filename' 'cipher-text-file-path-to-be-saved-with-filename' 'decrypt'"
	e.g., "java MainClass key.txt plaintext.txt ciphertext.txt decrypt"

