import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor{
	private void Encryptor() {
		// NOTE FROM THE DEV: ALL CAN BE ENCRYPTED/DECRYPTED !EXCEPT THIS PROGRAM'S FILES AND SYSTEM FILES!
	}
	
	public void encryptFile(String inputFile, String outputDir, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        String originalFileName = inputFile.substring(inputFile.lastIndexOf("\\") + 1);
        String encryptedFileName = generateRandomFileName();
        String outputFile = outputDir + "\\" + encryptedFileName + ".enc";
        
        File encFile = new File(outputFile).getAbsoluteFile();
        
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File("encFiles.txt"),true),true);
        pw.println(encFile.toString());
        pw.close();
        
        System.out.println("Encrypting file! Please wait...");
        try (InputStream inputStream = new FileInputStream(inputFile);
             OutputStream outputStream = new CipherOutputStream(new FileOutputStream(outputFile), cipher)) {

            // Write original filename as a header
            byte[] fileNameBytes = originalFileName.getBytes(StandardCharsets.UTF_8);
            outputStream.write(fileNameBytes.length);
            outputStream.write(fileNameBytes);

            // Encrypt file content
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        }
        System.out.println("Encryption Complete!");
        File oldFile = new File(inputFile);
        oldFile.delete();
    }
	
	private static String generateRandomFileName() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}