package org.example.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Controllers.AuthorizeController;
import org.example.Controllers.LoginController;
import org.example.Controllers.MainController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.UUID;

public class LoginHandler implements HttpHandler {
    private static String activeSessionId = null;
    private static HashMap<String, Object> activeSession = null;

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("hii  " + LocalTime.now());
        String method = exchange.getRequestMethod();
        String url = exchange.getRequestURI().toString();
        String path = exchange.getRequestURI().getPath();
        String[] pathSplit = path.split("/");
        System.out.println("hi");
        switch (method) {
            case "GET":
                if (pathSplit[1].equals("login")){
                    HashMap<String,String> params = queryParams(url);
                    if (AuthorizeController.Authorize(params)) {
                      SetInfos(params);
                        LoginController.SendPage(exchange, "login.html", "");
                    }else {
                        try {
                            exchange.sendResponseHeaders(400 , -1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                break;
            case "POST":
                if (pathSplit[1].equals("login")){
                    HashMap<String,String> params = queryParams(url);
                    LoginController.Login(exchange , params);
                }
                break;
            default:
                break;

        }
    }

    public void SetInfos(HashMap<String, String> params) {
        MainController.getInformations().put("state" , params.get("state"));
        MainController.getInformations().put("redirect_uri" , params.get("redirect_uri"));
    }

    public static HashMap<String , String > queryParams(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String query = uri.getQuery();
        HashMap<String, String> params = new HashMap<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            params.put(key, value);
        }
        return params;
    }




}
