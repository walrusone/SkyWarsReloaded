package com.walrusone.skywars.bungee;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.walrusone.skywars.game.Game;

public class BungeeSocket {

	public static String signUpdateString(Game game) {
		if (game == null) {
			return "sign:null:JOIN:0:0";
		}
		return "sign:" + game.getBungeeName() + ":" + game.getState().toString() + ":" + game.getPlayers().size() + ":" + game.getNumberOfSpawns();
	}

	static ArrayList<Integer> portsUp = new ArrayList<Integer>();
	static boolean init = false;
	static boolean initializing = false;

	public static void sendSignUpdate(final Game game) {
		try {
			if (init) {
				for (int i : portsUp) {
					Socket socket = new Socket("127.0.0.1", i);
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println(signUpdateString(game));
					socket.close();
				}
				return;
			}
			if (!initializing) {
				initializing = true;
				new Thread(new Runnable() {
					public void run() {
						for (int i = 13380; i < 13400; i++) {
							try {
								Socket socket = new Socket("127.0.0.1", i);
								if (socket.isConnected()) {
									portsUp.add(i);
								}
								PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
								out.println(signUpdateString(game));
								socket.close();
							} catch (Exception e) {
							}
						}
						init = true;
					}
				}).start();
			}
		} catch (Exception e) {
		}

	}
}
