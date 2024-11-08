package org.example.Controllers;

import com.sun.net.httpserver.HttpExchange;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.JwtGenerator;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.Key;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.example.Controllers.LoginController.CreateConnection;

public class TokenController {

    public static boolean CheckParameters(HashMap<String, String> params) {
        if (isValidClientId(params.get("client_id"))){
            return false;
        }
        if (isValidClientSecret(params.get("client_secret"))){
            return false;
        }
        if (!isValidAuthCode(params.get("code"))){
            return false;
        }
        return params.get("grant_type").equals("authorization_code");
    }

    private static boolean isValidAuthCode(String authCode) {
        String sql = "Select * From authcodes where authcode = ? AND used = 0";
        try {
            Connection connection = CreateConnection();
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1 , authCode);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                changeToUsed(authCode);
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

    private static boolean isValidClientId(String clientId) {
        return !clientId.equals("d8341d2e-8c5b-4e2d-89c2-123456789abc");
    }

    private static boolean isValidClientSecret(String clientId) {
        return !clientId.equals("K9fA3bV4zE7pO1qJ5tL6mW2xY8nZ0rS3");
    }

    private static void changeToUsed(String authCode){
        String sql = "Update authcodes SET used = 1 WHERE authcode = ?";
        try {
            Connection connection = CreateConnection();
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1 , authCode);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void SendAccessToken(HttpExchange exchange , HashMap<String, String> params ) throws IOException {
        String accessToken = generateAccessToken(params);
        saveAccessToken( accessToken);

        // ارسال پاسخ به کلاینت
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("access_token", accessToken);
        jsonResponse.put("token_type", "Bearer");
        jsonResponse.put("expires_in", 3600); // مدت زمان اعتبار توکن به ثانیه

        byte[] responseBytes = jsonResponse.toString().getBytes();
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();

    }

    public static String generateAccessToken(HashMap<String, String> params) {
        Map<String , Object > claims = new HashMap<>();
        claims.put("username" , "alireza");// To Do ...
        return JwtGenerator.createToken(claims , 60);
    }

    public static void saveAccessToken( String accessToken) {
        String sql = "INSERT INTO accesstokens (accesstoken) VALUES  (?)";
        try {
            Connection connection = CreateConnection();
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, accessToken);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
