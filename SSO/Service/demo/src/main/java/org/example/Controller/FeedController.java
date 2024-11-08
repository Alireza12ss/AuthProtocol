package org.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FeedController {


    public static String info;
    @FXML
    private Label addressLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;


    public static HashMap<String, String> jsonToHashMap(String jsonString) {
        HashMap<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            // استفاده از Iterator برای پیمایش کلیدهای JSON
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @FXML
    private void initialize()  {
        HashMap<String , String> params = jsonToHashMap(info);
        nameLabel.setText(params.get("first_name") + " " + params.get("last_name"));
        phoneLabel.setText(params.get("phone_number"));
        addressLabel.setText( params.get("address"));
    }
}
