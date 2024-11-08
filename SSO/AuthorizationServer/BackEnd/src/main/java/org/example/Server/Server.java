package org.example.Server;


import org.example.Handlers.AcceptedHandler;
import org.example.Handlers.AuthorizationHandler;
import org.example.Handlers.LoginHandler;
import org.example.Handlers.TokenHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Server {
    public Server(int port) {
        try {
            createServer(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createServer(int port) throws IOException {
        InetAddress localAddress = InetAddress.getByName("127.0.0.1");
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(localAddress, port), 0);
        server.createContext("/authorize" , new AuthorizationHandler());
        server.createContext("/login" , new LoginHandler());
        server.createContext("/accepted" , new AcceptedHandler());
        server.createContext("/token" , new TokenHandler());
        server.start();
    }
}
