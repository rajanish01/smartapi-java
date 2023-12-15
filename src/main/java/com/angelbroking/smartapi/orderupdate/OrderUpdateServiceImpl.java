package com.angelbroking.smartapi.orderupdate;

import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderUpdateServiceImpl implements OrderUpdateListener {
    @Override
    public void onConnected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onError(SmartStreamError error) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPong() {

    }

    @Override
    public void onOrderUpdate(String data) {
        log.info("order data {} ",data);
    }
}
