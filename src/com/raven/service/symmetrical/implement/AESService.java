package com.raven.service.symmetrical.implement;

import com.raven.service.symmetrical.ISymmetricService;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;

public class AESService implements ISymmetricService {

    private SecretKey key;
    private String transformation;

    @Override
    public SecretKey generateSecretKey(int length) throws Exception {
        KeyGenerator key_generator = KeyGenerator.getInstance("AES");
        key_generator.init(length); // Độ dài của khóa (128,192 hoặc 256 bit)
        key = key_generator.generateKey();
        return key;
    }

    @Override
    public String encrypt(String text) throws Exception {
        if (key == null) return "";
        Cipher cipher = Cipher.getInstance(transformation);

        if (transformation.contains("ECB")) cipher.init(Cipher.ENCRYPT_MODE, key);
        else cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));

        var text_bytes = text.getBytes("UTF-8");
        var encrypted_text_bytes = cipher.doFinal(text_bytes);
        return Base64.getEncoder().encodeToString(encrypted_text_bytes);
    }

    @Override
    public void encryptFile(String srcFile, String destFile) throws Exception {

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            File file = new File(srcFile);
            if (file.isFile()) {
                Cipher cipher = Cipher.getInstance(transformation);

                if (transformation.contains("ECB")) cipher.init(Cipher.ENCRYPT_MODE, key);
                else cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));

                fis = new FileInputStream(file);
                fos = new FileOutputStream(destFile);

                byte[] input_byte = new byte[1024];
                int bytes_read;

                while ((bytes_read = fis.read(input_byte)) != -1) {

                    byte[] output_byte = cipher.update(input_byte, 0, bytes_read);
                    if (output_byte != null) fos.write(output_byte);
                }

                byte[] output_byte = cipher.doFinal();
                if (output_byte != null) fos.write(output_byte);

                fos.flush();
                System.out.println("Mã hóa file thành công");
            }
        } finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }

    }



    @Override
    public String decrypt(String text) throws Exception {
        if (key == null) return "";
        Cipher cipher = Cipher.getInstance(transformation);

        if (transformation.contains("ECB")) cipher.init(Cipher.DECRYPT_MODE, key);
        else cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));

        var encrypted_text_bytes = Base64.getDecoder().decode(text);
        var decrypted_text_bytes = cipher.doFinal(encrypted_text_bytes);
        return new String(decrypted_text_bytes, "UTF-8");
    }

    @Override
    public void decryptFile(String srcFile, String destFile) throws Exception {

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File file = new File(srcFile);
            if (file.isFile()) {
                Cipher cipher = Cipher.getInstance(transformation);
                if (transformation.contains("ECB")) cipher.init(Cipher.DECRYPT_MODE, key);
                else cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
                fis = new FileInputStream(file);
                fos = new FileOutputStream(destFile);
                byte[] input_byte = new byte[1024];
                int byte_read;
                while ((byte_read = fis.read(input_byte)) != -1) {
                    byte[] output_byte = cipher.update(input_byte, 0, byte_read);
                    if (output_byte != null) fos.write(output_byte);
                }
                byte[] output_byte = cipher.doFinal();
                if (output_byte != null) fos.write(output_byte);
                fos.flush();
                System.out.println("Giải mã file thành công");
            }
        } finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }
    }

    @Override
    public String exportKey() throws Exception {
        if (key == null) return "";
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    @Override
    public SecretKey importKey(String keyText) throws Exception {
        if (keyText == null || keyText.isEmpty()) {
            throw new IllegalArgumentException("Invalid key text");
        }
        try {
            byte[] key_bytes = Base64.getDecoder().decode(keyText.getBytes());
            key = new SecretKeySpec(key_bytes, "AES");
            return key;
        } catch (Exception e) {
            throw new Exception("Failed to import key: " + e.getMessage());
        }
    }

    @Override
    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }
    public static void main(String[] args) throws Exception {
        String plain_text = "Thử code mã hóa";
        AESService aesService = new AESService();
        aesService.setTransformation("AES/CBC/PKCS5Padding");
        aesService.generateSecretKey(128);
        String encrypt_text = aesService.encrypt(plain_text);
        String decrypted_text = aesService.decrypt(encrypt_text);
        System.out.println("Key: " + aesService.exportKey());
        System.out.println("Encrypted Text: " + encrypt_text);
        System.out.println("Decrypted Text: " + decrypted_text);
        String srcFileEncrypt = "E:\\Dowload\\testMaHoa.json";
        String destFileEncrypt = "E:\\Dowload\\testDaMaHoa.json";
        String destFileDecrypt = "E:\\Dowload\\testDaGiai.json";
        aesService.encryptFile(srcFileEncrypt, destFileEncrypt);
        aesService.decryptFile(destFileEncrypt, destFileDecrypt);
    }
}

