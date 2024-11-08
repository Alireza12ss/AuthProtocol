package org.example.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Controllers.TokenController;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.example.Handlers.LoginHandler.queryParams;

public class TokenHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        InputStream requestBody = exchange.getRequestBody();
        String requestBodyString = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        String path = exchange.getRequestURI().getPath();
        String[] pathSplit = path.split("/");
        switch (method) {
            case "POST":
                if (pathSplit[1].equals("token")){
                    System.out.println("1");
                    HashMap<String,String> params = parseQueryString(requestBodyString);
                    System.out.println("2");
                    if (TokenController.CheckParameters(params)){
                        System.out.println(LocalTime.now());
                        TokenController.SendAccessToken(exchange , params);
                    }
                }
                break;
            default:
                break;

        }

    }
        private HashMap<String, String> parseQueryString(String query) throws UnsupportedEncodingException {
            HashMap<String, String> queryPairs = new HashMap<>();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                queryPairs.put(key, value);
            }
            return queryPairs;
        }
}
