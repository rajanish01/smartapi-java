package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;

public class Test {

	public static void main(String[] args) throws SmartAPIException {
		try {

			SmartConnect smartConnect = new SmartConnect();

			// PROVIDE YOUR API KEY HERE
			smartConnect.setApiKey("smartapi_key");

			// Set session expiry callback.
			smartConnect.setSessionExpiryHook(new SessionExpiryHook() {
				@Override
				public void sessionExpired() {
					System.out.println("session expired");
				}
			});

			User user = smartConnect.generateSession("S212741", "pass@123");
			System.out.println(user.toString());
			smartConnect.setAccessToken(user.getAccessToken());
			smartConnect.setUserId(user.getUserId());

			// token re-generate testing
			/*
			 * TokenSet tokenSet = smartConnect.renewAccessToken(user.getAccessToken(),
			 * user.getRefreshToken());
			 * smartConnect.setAccessToken(tokenSet.getAccessToken());
			 */

			Examples examples = new Examples();

			System.out.println("getProfile");
			examples.getProfile(smartConnect);

			System.out.println("placeOrder");
			examples.placeOrder(smartConnect);

			System.out.println("modifyOrder");
			examples.modifyOrder(smartConnect);

			System.out.println("cancelOrder");
			examples.cancelOrder(smartConnect);

			System.out.println("getOrder");
			examples.getOrder(smartConnect);

			System.out.println("getLTP");
			examples.getLTP(smartConnect);

			System.out.println("getTrades");
			examples.getTrades(smartConnect);

			System.out.println("getRMS");
			examples.getRMS(smartConnect);

			System.out.println("getHolding");
			examples.getHolding(smartConnect);

			System.out.println("getPosition");
			examples.getPosition(smartConnect);

			System.out.println("convertPosition");
			examples.convertPosition(smartConnect);

			System.out.println("logout");
			examples.logout(smartConnect);

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

}
