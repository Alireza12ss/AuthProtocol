package org.example.Controllers;

import java.util.HashMap;

public class MainController {
    public static HashMap<String, String> getInformations() {
        return informations;
    }

    public static void setInformations(HashMap<String, String> informations) {
        MainController.informations = informations;
    }

    static HashMap<String , String> informations = new HashMap<String, String>();
}
