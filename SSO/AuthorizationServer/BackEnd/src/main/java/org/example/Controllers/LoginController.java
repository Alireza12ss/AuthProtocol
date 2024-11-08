package org.example.Controllers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.*;
import java.util.HashMap;

public class LoginController {


    public static boolean LoginDAO(HashMap<String, String> params) {
        String sql = "Select * From users where username = ? AND password = ?";

        try {
            Connection connection = CreateConnection();
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1 , params.get("username"));
            statement.setString(2 , params.get("password"));
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return true;
            }else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

     public static Connection CreateConnection() {
        String url = "jdbc:mysql://localhost:3306/projectac";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(url , username , password);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Login(HttpExchange exchange ,HashMap<String, String> params){
        if (LoginDAO(params)){
            try {
                exchange.sendResponseHeaders(200 , -1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                exchange.sendResponseHeaders(400 , -1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void SendPage(HttpExchange exchange, String filename, String username) {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        String filePath = "src/main/" + filename;
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            content = content.replace("${username}", username);
            SendResponse(exchange, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void SendResponse(HttpExchange exchange, byte[] response) {
        try {
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}