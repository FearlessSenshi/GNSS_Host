import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class Decryptor{
	private void Decryptor() {
		// NOTE FROM THE DEV: ALL CAN BE ENCRYPTED/DECRYPTED !EXCEPT THIS PROGRAM'S FILES AND SYSTEM FILES!
	}
	
	public void decryptFile(String inputFile, String outputDir, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        try (InputStream inputStream = new FileInputStream(inputFile);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
             DataInputStream dataInputStream = new DataInputStream(cipherInputStream)) {

            // Read original filename from the header
            int fileNameLength = dataInputStream.read();
            byte[] fileNameBytes = new byte[fileNameLength];
            dataInputStream.readFully(fileNameBytes);
            String originalFileName = new String(fileNameBytes, StandardCharsets.UTF_8);

            // Construct the output file path with the original filename
            String outputFile = outputDir + "\\" + originalFileName;

            // Decrypt and write file content
            System.out.println("Decrypting " + originalFileName + ". Please wait...");
            try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
            }
            
            System.out.println("[DEC] Decryption Complete!");
            
            inputStream.close();
            cipherInputStream.close();
            dataInputStream.close();
            
            File oldFile = new File(inputFile);
            File key = new File("encKey.txt");
            File encFiles = new File("encFiles.txt");
            //File dirFiles = new File("directories.txt");
            
            oldFile.delete();
            key.delete();
            encFiles.delete();
            //dirFiles.delete();
        }
    }
}