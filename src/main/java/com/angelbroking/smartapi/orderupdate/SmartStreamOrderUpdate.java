package com.angelbroking.smartapi.orderupdate;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.utils.Utils;
import com.neovisionaries.ws.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class SmartStreamOrderUpdate {
    private static final int pingIntervalInMilliSeconds = 10000; // 10 seconds
    private static final String headerAuthorization = "Authorization";
    private static final Integer delayInMilliSeconds = 5000;
    private static final Integer periodInMilliSeconds = 5000;
    private final Routes routes = new Routes();
    private final String wsuri = routes.getOrderUpdateUri();
    private WebSocket ws;
    private String accessToken;
    private final OrderUpdateListener orderUpdateListener;

    private Timer pingTimer;
    private LocalDateTime lastPongReceivedTime = LocalDateTime.now();

    /**
     * Initializes the OrderUpdateWebsocket.
     *
     * @param accessToken
     * @param orderUpdateListener
     */
    public SmartStreamOrderUpdate(String accessToken, OrderUpdateListener orderUpdateListener) {
        if (StringUtils.isEmpty(accessToken) || Utils.validateInputNullCheck(orderUpdateListener)) {
            throw new IllegalArgumentException(
                    "clientId, feedToken and SmartStreamListener should not be empty or null");
        }
        this.accessToken = accessToken;
        this.orderUpdateListener = orderUpdateListener;

        init();
    }

    private void init() {
        try {
            log.info("inside websocket init");
            log.info("accessToken {} ", accessToken);
            log.info("wsuri = {}", wsuri);
            ws = new WebSocketFactory()
                    .setVerifyHostname(false)
                    .createSocket(wsuri)
                    .setPingInterval(pingIntervalInMilliSeconds);
            ws.addHeader(headerAuthorization, "Bearer " + accessToken);
            ws.addListener(getWebsocketAdapter());
        } catch (IOException e) {
            if (Utils.validateInputNotNullCheck(orderUpdateListener)) {
                orderUpdateListener.onError(getErrorHolder(e));
            }
        }
    }

    private WebSocketListener getWebsocketAdapter() {
        return new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws WebSocketException {
                orderUpdateListener.onConnected();
                startPingTimer(websocket);
            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                try {
                    orderUpdateListener.onOrderUpdate(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * On disconnection, return statement ensures that the thread ends.
             *
             * @param websocket
             * @param serverCloseFrame
             * @param clientCloseFrame
             * @param closedByServer
             * @throws Exception
             */
            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                       WebSocketFrame clientCloseFrame, boolean closedByServer) {
                try {
                    if (closedByServer) {
                        reconnect();
                    } else {
                        stopPingTimer();
                        orderUpdateListener.onDisconnected();
                    }
                } catch (Exception e) {
                    SmartStreamError error = new SmartStreamError();
                    error.setException(e);
                    orderUpdateListener.onError(error);
                }
            }

            @Override
            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                super.onCloseFrame(websocket, frame);
            }

            @Override
            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                try {
                    lastPongReceivedTime = LocalDateTime.now();
                    orderUpdateListener.onPong();
                } catch (Exception e) {
                    SmartStreamError error = new SmartStreamError();
                    error.setException(e);
                    orderUpdateListener.onError(error);
                }
            }
        };
    }

    private void startPingTimer(final WebSocket websocket) {

        pingTimer = new Timer();
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    LocalDateTime currentTime = LocalDateTime.now();
                    if (lastPongReceivedTime.isBefore(currentTime.minusSeconds(20))) {
                        websocket.disconnect();
                        reconnect();
                    }
                } catch (Exception e) {
                    orderUpdateListener.onError(getErrorHolder(e));
                }
            }
        }, delayInMilliSeconds, periodInMilliSeconds); // run at every 5 second
    }

    private void stopPingTimer() {
        if (Utils.validateInputNotNullCheck(pingTimer)) {
            pingTimer.cancel();
            pingTimer = null;
        }
    }

    private void reconnect() throws WebSocketException {
        log.info("reconnect - started");
        init();
        connect();
        log.info("reconnect - done");
    }

    private SmartStreamError getErrorHolder(Exception e) {
        SmartStreamError error = new SmartStreamError();
        error.setException(e);
        return error;
    }

    public void connect() throws WebSocketException {
        ws.connect();
        log.info("connected to uri: {}", wsuri);
    }

    /**
     * Returns true if websocket connection is open.
     *
     * @return boolean
     */
    public boolean isConnectionOpen() {
        return (ws != null) && ws.isOpen();
    }

    /**
     * Returns true if websocket connection is closed.
     *
     * @return boolean
     */
    public boolean isConnectionClosed() {
        return !isConnectionOpen();
    }

    /**
     * Disconnects websocket connection.
     */
    public void disconnect() {

        if (ws != null) {
            stopPingTimer();
            ws.disconnect();
        }
    }
}
