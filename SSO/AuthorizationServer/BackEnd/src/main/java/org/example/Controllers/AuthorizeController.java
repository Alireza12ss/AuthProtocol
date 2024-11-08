package org.example.Controllers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static org.example.Controllers.LoginController.CreateConnection;

public class AuthorizeController {
    public static boolean Authorize(HashMap<String, String> params){
        if (!isValidClientId(params.get("clientId"))){
            return false;
        }
        if (!params.get("response_type").equals("code")){
            return false;
        }
        if (!params.get("scope").equals("read_user")){
            return false;
        }
        return true;
    }


    public static String generateAuthorizationCode() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(32 * 5, random).toString(32);
    }

    private static boolean isValidClientId(String clientId) {
        if (clientId.equals("d8341d2e-8c5b-4e2d-89c2-123456789abc")){
            return true;
        }
        return false;
    }

    public static void SendCodeToClient(HttpExchange exchange ,String username) {
        String authCode = generateAuthorizationCode();
        SaveAuthCodeInDatabase(authCode,username);
        try {
            SendToClient(authCode , MainController.informations.get("redirect_uri") , MainController.informations.get("state"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            exchange.sendResponseHeaders(200 , -1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
//jbkjjnlk
    private static void SendToClient(String authCode , String redirectUri , String state) throws IOException {
        String query = String.format("code=%s&state=%s",
                URLEncoder.encode(authCode, StandardCharsets.UTF_8),
                URLEncoder.encode(state, StandardCharsets.UTF_8));

        URL url = null;
        try {
            url = new URL(redirectUri + "?" + query);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Authorization code sent successfully to " + redirectUri);
        } else {
            System.err.println("Failed to send authorization code. HTTP response code: " + responseCode);
        }
    }

    private static void SaveAuthCodeInDatabase(String authCode , String username){
        String sql = "INSERT INTO authcodes values (? , ? , ? ,? , ?)";
        try {
            Connection connection = CreateConnection();
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1 , username);
            statement.setString(2 , authCode);
            statement.setDate(4, Date.valueOf(LocalDate.now()));
            statement.setTime(3, Time.valueOf(LocalTime.now()));
            statement.setInt(5,0);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
