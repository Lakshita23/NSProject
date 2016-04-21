package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

/**
 * Created by Lakshita on 4/20/2016.
 */
public class ServerCP1 {

    public static final String ALGORITHM_CIPHER = "RSA/ECB/PKCS1Padding";
    public static final String ALGORITHM_KEYGEN = "RSA";
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(4321);

        while (true) {
            Socket connection = serverSocket.accept();
            handleRequest(connection);
        }
    }

    private static void handleRequest (Socket connection) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        PrintWriter out = new PrintWriter(connection.getOutputStream(), true);


        String[] messageNonce = in.readLine().split(":");   //read message: Hello...+nonce
        String replymsg = messageNonce[0]+":"+messageNonce[1];  //msg+nonce
        System.out.println("received1: "+replymsg);
        String nonce1 = messageNonce[1]; //extract nonce
        System.out.println("nonce: " + nonce1);
        byte[] encryptedMsg = getencryptedMsg(replymsg);     //encrypt message received with private key
        out.println(DatatypeConverter.printBase64Binary(encryptedMsg)); //send encrypted msg to client
        out.flush();

        String[] msg2 = in.readLine().split(":"); //give cert+nonce
        String nonce2 = msg2[1];
        String encryptedNonce = DatatypeConverter.printBase64Binary(getencryptedMsg(nonce2));
        out.println(CertsLoader.SERVER_CERTIFICATE+":"+encryptedNonce);


        out.close();
        in.close();
        connection.close();
    }


    public static byte[] getencryptedMsg(String msgTxt){
        generateKey();
        ObjectInputStream inputStream = null;
        try {

            inputStream = new ObjectInputStream(new FileInputStream(CertsLoader.SERVER_PRIVATE_KEY));
            final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            final byte[] cipherText = encrypt(msgTxt, privateKey);

            return cipherText;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] encrypt(String ciphertext, PrivateKey key){
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(ciphertext.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }
    public static String getdecryptedMsg(byte[] msgTxt){
        generateKey();

        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(CertsLoader.SERVER_PUBLIC_KEY));
            final PublicKey publicKey = (PublicKey) inputStream.readObject();
            final String plainText = decrypt(msgTxt, publicKey);

            return plainText;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String decrypt(byte[] encryptedTxt, PublicKey key){
        byte[] dectyptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(encryptedTxt);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }


    public static void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM_KEYGEN);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(CertsLoader.SERVER_PRIVATE_KEY);
            File publicKeyFile = new File(CertsLoader.SERVER_PUBLIC_KEY);

            // Create files to store public and private key
            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
            privateKeyFile.createNewFile();

            if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }
            publicKeyFile.createNewFile();

            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                    new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(
                    new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: in generate key");
        }

    }


}
