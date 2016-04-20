package com.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class CertVerification {
    public static void main(String[] args) {
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

    }
}
