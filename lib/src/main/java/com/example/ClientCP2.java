package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Lakshita on 4/20/2016.
 */
public class ClientCP2 {
    public static void main(String[] args) throws IOException {
        String hostName = "192.168.50.14";
        int portNumber = 4321;
        Socket echoSocket = new Socket(hostName, portNumber);

        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    }
}
