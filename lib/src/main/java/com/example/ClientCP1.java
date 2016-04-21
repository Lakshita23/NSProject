package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by Lakshita on 4/20/2016.
 */
public class ClientCP1 {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 4321;
        Socket echoSocket = new Socket(hostName, portNumber);

        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));


        /*AUTHENTICATION PROTOCOL*/
        byte[] noncegen = new byte[16];  //Creating nonce
        Random rand = new SecureRandom();   //System chooses best random number generator algorithm
        rand.nextBytes(noncegen);
        String nonce = DatatypeConverter.printBase64Binary(noncegen);
        out.flush();
        out.println("Hello SecStore, please prove your identity:" + nonce);
        out.flush();
        String M = in.readLine();          //M=K(server private key){ hi msg above }; decrypt when publice key is received
        out.println("Give me your certificate signed by CA:"+nonce); //Request for certificate
        out.flush();
        String[] serverCertNonce = (in.readLine()).split(":");     //[0] = Server's signed certificate and [1] = K(server's private key){nonce}
        System.out.println("Servercrt: "+serverCertNonce[0]);
        if (verifyCert(serverCertNonce[0])==1){
            System.out.println("Server cert verified");
        }
        else{
            System.out.println("Certificate can't be verified, closing connection");
            out.close();
            in.close();
            echoSocket.close();
        }


        out.close();
        in.close();
        echoSocket.close();
    }

    public static int verifyCert(String cert){
        InputStream fis = null, fis2 = null;
        try {
            fis = new FileInputStream(new File(CertsLoader.CA_CERTIFICATE));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate CAcert = (X509Certificate) cf.generateCertificate(fis);

            fis2 = new FileInputStream(cert);
            CertificateFactory cfserver = CertificateFactory.getInstance("X.509");
            X509Certificate ServerCert = (X509Certificate) cfserver.generateCertificate(fis2);

            PublicKey CAkey = CAcert.getPublicKey();

            ServerCert.checkValidity();
            ServerCert.verify(CAkey);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Server certificate can't be verified");
            return 0;
        }
        return 1;
    }

}
