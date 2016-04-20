package com.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;

/**
 * Created by Lakshita on 4/20/2016.
 */
public class ClientCP1 {
    public static void main(String[] args) throws IOException {
        String hostName = "192.168.50.14";
        int portNumber = 4321;
        Socket echoSocket = new Socket(hostName, portNumber);

        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));

        /*AUTHENTICATION PROTOCOL*/
        byte[] nonce = new byte[16];  //Creating nonce
        Random rand = new SecureRandom();   //System chooses best random number generator algorithm
        rand.nextBytes(nonce);

        out.println("Hello SecStore, please prove your identity."+nonce);
        out.flush();
        String M = in.readLine();          //M=K(server private key){ hi msg above }; decrypt when publice key is received
        out.println("Give me your certificate signed by CA."+nonce); //Request for certificate
        out.flush();
        String[] serverCertNonce = (in.readLine()).split(".");     //[0] = Server's signed certificate and [1] = K(server's private key){nonce}




//        String input;
//        do {
//            input = stdIn.readLine();
//            out.println(input);
//            out.flush();
//
//        } while (!input.equals("bye"));

        out.close();
        echoSocket.close();
    }

    public static int verifyCert(String cert){
        InputStream fis = null, fis2 = null;
        try {
            fis = new FileInputStream("C:\\Users\\Lakshita\\Documents\\SUTD\\TERM 5\\CSE\\NS Project\\CA.crt");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate CAcert =(X509Certificate)cf.generateCertificate(fis);

            fis2 = new FileInputStream("C:\\Users\\Lakshita\\Documents\\SUTD\\TERM 5\\CSE\\NS Project\\Signed Certificate - 1000943.crt");
            CertificateFactory cfserver = CertificateFactory.getInstance("X.509");
            X509Certificate ServerCert =(X509Certificate)cfserver.generateCertificate(fis2);

            PublicKey CAkey = CAcert.getPublicKey();

            ServerCert.checkValidity();
            ServerCert.verify(CAkey);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: No file found");
        } catch (CertificateException e) {
            System.out.println("ERROR: Check certificate instance");
        } catch (NoSuchProviderException e) {
            System.out.println("ERROR: Verification error, No such provider");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR: Verification error, No such algorithm");
        } catch (InvalidKeyException e) {
            System.out.println("ERROR: Verification error, Invalid Key");
        } catch (SignatureException e) {
            System.out.println("ERROR: Verification error, Signature length not correct");
        }
        return 0;
    }

}
