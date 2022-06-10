package lt.viko.eif.algorithm;

import lt.viko.eif.enc.ShaEncryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;


@SuppressWarnings("all")
public class AesAlgorithm {

    private static String KEY = "secretkey1234567";

    public void encryptFile(String filePath) {
        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();

            String line;
            sb.append(br.readLine() + "\n");
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            fr.close();
            String encryptedText = encrypt(sb.toString());
            writeData(filePath, encryptedText);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String decryptFile(String filePath) {
        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            sb.append(br.readLine());
            fr.close();
            String decryptedText = decrypt(sb.toString());
            decryptedText = decryptedText.substring(0, decryptedText.length() - 1);
            return decryptedText;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeData(String file, String text) {
        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String text) {
        try {
            SecretKeySpec secretKey = setKey(KEY);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String text) {
        try {
            SecretKeySpec secretKey = setKey(KEY);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(text)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKeySpec setKey(final String myKey) {
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
