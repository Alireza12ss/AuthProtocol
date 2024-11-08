package org.example.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Controllers.AuthorizeController;
import org.example.Controllers.LoginController;

import java.io.IOException;

public class AuthorizationHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String url = exchange.getRequestURI().toString();
        String path = exchange.getRequestURI().getPath();
        String[] pathSplit = path.split("/");
        String username = LoginHandler.queryParams(url).get("username");
        switch (method) {
            case "GET":
                if (pathSplit[1].equals("authorize")){
                    LoginController.SendPage(exchange , "access.html" , username);
                }
                break;
            case "POST":
                if (pathSplit[1].equals("authorize")){
                    LoginController.SendPage(exchange , "final.html" , username);
                    AuthorizeController.SendCodeToClient(exchange , username);
                }

        }
    }


}
