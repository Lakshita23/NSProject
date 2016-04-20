package com.example;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lakshita on 4/20/2016.
 */
public class ServerCP1 {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(4321);

        while (true) {
            Socket connection = serverSocket.accept();
            handleRequest(connection);
        }
    }

    private static void handleRequest (Socket connection) throws Exception {

    }

}
