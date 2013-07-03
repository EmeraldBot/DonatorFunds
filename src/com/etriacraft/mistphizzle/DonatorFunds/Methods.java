package com.etriacraft.mistphizzle.DonatorFunds;


public class Methods {
	
	DonatorFunds plugin;

	public Methods(DonatorFunds instance) {
		plugin = instance;
	}
	
	public static boolean hasClaimed(String playerName, String rank) {
		if (!DonatorFunds.getInstance().getConfig().contains("players." + playerName + "." + rank)) {
			return false;
		}
		if (DonatorFunds.getInstance().getConfig().getBoolean("players." + playerName + "." + rank)) {
			return true;
		}
		if (!DonatorFunds.getInstance().getConfig().getBoolean("players." + playerName + "." + rank)) {
			return false;
		}
		return false;
	}
	
	public static String colorize(String message) {
		return message.replaceAll("(?i)&([a-fk-or0-9])", "\u00A7$1");
	}

}
