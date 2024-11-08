package org.example.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.JwtGenerator;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.io.IOException;
import java.security.SignatureException;
import java.sql.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


public class UserHandler implements HttpHandler {


    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("hii  " + LocalTime.now());
        String method = exchange.getRequestMethod();
        String url = exchange.getRequestURI().toString();
        String path = exchange.getRequestURI().getPath();
        System.out.println(url);
        String[] pathSplit = path.split("/");
        System.out.println("hi");
        switch (method) {
            case "GET":
                if (pathSplit[1].equals("user")){
                    String accessToken = extractAccessToken(exchange);
                    if (isValidAccessToken(accessToken)) {
                        String userName = extractUsernameFromToken(accessToken);
                        Map<String , String> params = getUserInfo(userName);
                        JSONObject jsonResponse = new JSONObject(params);
                        try {
                            exchange.getResponseHeaders().add("Content-Type", "application/json");
                            System.out.println("mo");
                            exchange.sendResponseHeaders(200, jsonResponse.toString().getBytes(StandardCharsets.UTF_8).length);

                            exchange.getResponseBody().write(jsonResponse.toString().getBytes(StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String response = "Invalid or expired access token";
                        try {
                            exchange.sendResponseHeaders(401, response.getBytes().length);
                            exchange.getResponseBody().write(response.getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                break;
            default:
                break;

        }
    }
    private String extractAccessToken(HttpExchange exchange) {
        String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/projectac";
        String username = "root";
        String password = "";

        return DriverManager.getConnection(url, username, password);
    }

    private boolean isValidAccessToken(String token) {
        String query = "SELECT COUNT(*) FROM accesstokens WHERE accesstoken = ?";

        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, token);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);

                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String extractUsernameFromToken(String token) {
        System.out.println(token);
        if (JwtGenerator.tokenIsValid(token)){
            String re = JwtGenerator.decodeToken(token).get("username").toString();
            System.out.println("re = " + re);
            return re;
        }
        return "";
    }

    private Map<String, String> getUserInfo(String username) {
        String query = "SELECT firstName, lastName, phoneNumber, address FROM users WHERE username = ?";
        Map<String, String> userInfo = new HashMap<>();

        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userInfo.put("first_name", resultSet.getString("firstName"));
                userInfo.put("last_name", resultSet.getString("lastName"));
                userInfo.put("phone_number", resultSet.getString("phoneNumber"));
                userInfo.put("address", resultSet.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

}
