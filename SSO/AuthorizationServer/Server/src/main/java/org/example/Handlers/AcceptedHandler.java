package org.example.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Controllers.AuthorizeController;
import org.example.Controllers.LoginController;

import java.io.IOException;
import java.util.HashMap;

public class AcceptedHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                LoginController.SendPage(exchange, "final.html", "");
                break;
            default:
                break;

        }
    }
}
