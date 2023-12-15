package com.angelbroking.smartapi.orderupdatetest;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.orderupdate.OrderUpdateListener;
import com.angelbroking.smartapi.orderupdate.SmartStreamOrderUpdate;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.neovisionaries.ws.client.WebSocketException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;

import java.util.Scanner;

@Slf4j
public class OrderUpdateTest {
    private static String clientID;
    private static String clientPass;
    private static String apiKey;
    private static String accessToken;
    private static String totp;

    @BeforeAll
    public void init() throws Exception {
        clientID = System.getProperty("clientID");
        clientPass = System.getProperty("clientPass");
        apiKey = System.getProperty("apiKey");

        Scanner sc = new Scanner(System.in);
        log.info("enter totp: ");
        totp = sc.nextLine();

        SmartConnect smartConnect = new SmartConnect(apiKey);
        User user = smartConnect.generateSession(clientID, clientPass, totp);
        smartConnect.setAccessToken(user.getAccessToken());
        smartConnect.setUserId(user.getUserId());
        accessToken = user.getAccessToken();
    }

//    @Test
    public void testOrderUpdate_success() throws WebSocketException {
        SmartStreamOrderUpdate smartStreamOrderUpdate = new SmartStreamOrderUpdate(accessToken, new OrderUpdateListener() {
            @Override
            public void onConnected() {
                log.info("Connected");
            }

            @Override
            public void onDisconnected() {
                log.info("Disconnected");
            }

            @Override
            public void onError(SmartStreamError error) {
                log.info("error {} ",error.getException().getMessage());
            }

            @Override
            public void onPong() {
                log.info("Pong");
            }

            @Override
            public void onOrderUpdate(String data) {
                log.info("data {} ",data);
            }
        });

        smartStreamOrderUpdate.connect();

    }
}
