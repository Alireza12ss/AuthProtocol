package org.example.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalTime;


public class LoginController {
    @FXML
    private Label email;
    public void LoginWithMCI(ActionEvent event) {
        MoveTOAuthServer();
    }

    private void MoveTOAuthServer() {
        System.out.println("here");
        String clientId = "d8341d2e-8c5b-4e2d-89c2-123456789abc";
        String redirectUri = "http://localhost:8085/callback";
        String state = CreateState();
        String scope = "read_user";
        String responseType = "code";
        String url = null;
        url = "http://localhost:5000/login?clientId=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8)
                + "&response_type=" + URLEncoder.encode(responseType, StandardCharsets.UTF_8);
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
                ReadyToGoFeed();
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Desktop is not supported");
        }
    }

    private void ReadyToGoFeed() {
        boolean connected = false;

        while (!connected) {
            try (Socket socket = new Socket()) {
                // تلاش برای اتصال به سرور
                socket.connect(new InetSocketAddress("localhost", 5555), 1000);
                connected = true;
                System.out.println("Connected to server");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    // خواندن داده‌های JSON از سرور
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line).append("\n");
                    }

                    System.out.println("Received from server:");
                    System.out.println(response.toString());
                    FeedController.info = response.toString();
                    goToFeedp(email);
                    return;
                }

            } catch (IOException e) {
                System.out.println("Failed to connect to server, retrying in 3 seconds..." + LocalTime.now());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    private String CreateState() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder state = new StringBuilder(25);
        for (int i = 0; i < 25; i++) {
            int index = random.nextInt(CHARACTERS.length());
            state.append(CHARACTERS.charAt(index));
        }
        return state.toString();
    }

    public static void transferp(FXMLLoader fxmlLoader, String styleSheet , javafx.scene.control.Label label){
        System.out.println(styleSheet);
        new Thread(() -> {
            final Scene newScene;
            try {
                newScene = new Scene(fxmlLoader.load(), label.getScene().getWidth(), label.getScene().getHeight());
                newScene.getStylesheets().add(styleSheet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {
                Stage currentStage = (Stage)label.getScene().getWindow();
                currentStage.setScene(newScene);
            });
        }).start();
    }


    @FXML
    public static void goToFeedp(Label label) {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/org/example/demo/feed.fxml"));
        transferp(fxmlLoader, String.valueOf(LoginController.class.getResource("/org/example/demo/FeedStyle.css")) , label);
    }
}
