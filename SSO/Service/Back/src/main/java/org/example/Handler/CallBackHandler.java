package org.example.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class CallBackHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println(exchange.getRequestURI());
        String query = exchange.getRequestURI().getQuery();

        Map<String, String> params = queryToMap(query);


        String authCode = params.get("code");

        // اگر کد احراز هویت دریافت شده بود
        if (authCode != null) {
            System.out.println("Authorization code received: " + authCode + "   " + LocalTime.now());

            // ارسال درخواست برای دریافت access token
            exchange.sendResponseHeaders(200,-1);
            String accessToken = requestAccessToken(authCode);
            System.out.println(accessToken);
            SendToFront(accessToken);

            // پاسخ به کاربر نهایی
        } else {
            exchange.sendResponseHeaders(400, -1); // Bad Request
        }
    }

    private void SendToFront(String accessToken) {
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            System.out.println("Server started on port 5555");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Client connected");

                    // ایجاد داده‌های نمونه JSON برای ارسال به کلاینت
                    JSONObject userInfo = getUserInfo(accessToken);

                    // ارسال داده‌ها به کلاینت
                    out.println(userInfo);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String requestAccessToken(String authCode) throws IOException {

        URL url = new URL("http://localhost:5000/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);


        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Charset", "UTF-8");

        String clientId = "d8341d2e-8c5b-4e2d-89c2-123456789abc";
        String clientSecret = "K9fA3bV4zE7pO1qJ5tL6mW2xY8nZ0rS3";
        String redirectUri = "https://localhost:8085/callback";

        String postData = String.format(
                "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
                URLEncoder.encode(authCode, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
        );


        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
            outputStream.write(postDataBytes);
            outputStream.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("access_token");

            }
        } else {
            throw new IOException("Failed to obtain access token. HTTP response code: " + responseCode);
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            result.put(entry[0], entry[1]);
        }
        return result;
    }


    public static JSONObject getUserInfo(String accessToken) throws IOException {
        URL url = new URL("http://localhost:5050/user");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // تبدیل پاسخ JSON به JSONObject
            return new JSONObject(response.toString());
        } else {
            throw new RuntimeException("Failed to get user info. HTTP response code: " + responseCode);
        }
    }

}
