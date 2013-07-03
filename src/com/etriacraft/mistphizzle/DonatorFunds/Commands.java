package com.etriacraft.mistphizzle.DonatorFunds;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class Commands {
	
	DonatorFunds plugin;
	
	public Commands(DonatorFunds instance) {
		this.plugin = instance;
		init();
	}
	
	public static HashMap<String, String> spy = new HashMap();

	/* Permissions:
	 * donatorfunds.reset (Allows /df reset)
	 * donatorfunds.spy (Allows /df spy)
	 * donatorfunds.claim (Allows /df claim)
	 * donatorfunds.claim.<rank> (Allows /df claim [rank]
	 */
	private void init() {
		PluginCommand donatorfunds = plugin.getCommand("donatorfunds");
		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (args.length == 0) {
					s.sendMessage("-----§3DonatorFunds Commands§f-----");
					s.sendMessage("§3/df claim <RankName>§f - Claim money for <RankName>");
					if (s.hasPermission("donatorfunds.reset")) {
						s.sendMessage("§3/df reset <Player> <RankName>");
					}
					if (s.hasPermission("donatorfunds.spy")) {
						s.sendMessage("§3/df spy§f - Notifies you when a player claims a rank.");
					}
					return true;
				}
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("reset")) {
						if (!s.hasPermission("donatorfunds.reset")) {
							s.sendMessage("§cYou don't have permission to do that.");
							return true;
						}
						if (args.length != 3) {
							s.sendMessage("§6Proper Usage: §3/df reset [PlayerName] [RankName]");
							return true;
						}
						
						Set<String> ranks = plugin.getConfig().getConfigurationSection("Ranks").getKeys(false);
						String ranks2 = ranks.toString();
						String player = args[1];
						String desiredRank = args[2];
						if (!ranks.contains(desiredRank)) {
							s.sendMessage("§cThat rank does not exist.");
							s.sendMessage("§aAvailable Ranks: §3" + ranks2);
							return true;
						}
						if (!Methods.hasClaimed(player, desiredRank)) {
							s.sendMessage("§cThat player has not claimed that rank.");
							return true;
						}
						
						plugin.getConfig().set("players." + player + "." + desiredRank, false);
						plugin.saveConfig();
						s.sendMessage("§cYou have reset the §3" + desiredRank + "§c rank for §3" + player);
						return true;
					}
					if (args[0].equalsIgnoreCase("spy")) {
						if (!s.hasPermission("donatorfunds.spy")) {
							s.sendMessage("§cYou don't have permission to do that.");
							return true;
						}
						if (args.length > 1) {
							s.sendMessage("§6Proper Usage: §3/df spy");
							return true;
						}
						if (!spy.containsKey(s.getName())) {
							spy.put(s.getName(), null);
							s.sendMessage("§aYou have turned on DonatorFunds Spy.");
							s.sendMessage("§aYou will not receive a message when a user claims a rank.");
							s.sendMessage("§aTurn this off by running §6/df spy");
							return true;
						}
						if (spy.containsKey(s.getName())) {
							spy.remove(s.getName());
							s.sendMessage("§aYou have turned off DonatorFunds Spy.");
							return true;
						}
						return true;
					}
					if (args[0].equalsIgnoreCase("claim")) {
						if (!s.hasPermission("donatorfunds.claim")) {
							s.sendMessage("§cYou don't have permission to do that.");
							return true;
						}
						if (args.length != 2) {
							s.sendMessage("§6Proper Usage: §3/df claim [RankName]");
							return true;
						}
						Set<String> ranks = plugin.getConfig().getConfigurationSection("Ranks").getKeys(false);
						String ranks2 = ranks.toString();
						String desiredRank = args[1];
						if (!ranks.contains(desiredRank)) {
							s.sendMessage("§cThat rank does not exist.");
							s.sendMessage("§aAvailable Ranks: §3" + ranks2);
							return true;
						}
						if (ranks.contains(desiredRank)) {
							if (!s.hasPermission("donatorfunds.claim." + desiredRank)) {
								s.sendMessage("§cYou don't have permission to claim that rank.");
								return true;
							}
							String playerName = s.getName();
							Double money = plugin.getConfig().getDouble("Ranks." + desiredRank + ".money");
							String message = plugin.getConfig().getString("General.ClaimMessageToPlayer").replace("%rank", desiredRank).replace("%money", money.toString());
							String SpyMessage = plugin.getConfig().getString("General.ClaimMessageSpy").replace("%rank", desiredRank).replace("%money", money.toString()).replace("%player", playerName);
							
							if (Methods.hasClaimed(playerName, desiredRank)) {
								s.sendMessage("§cYou have already claimed this rank.");
								return true;
							}
							
							DonatorFunds.econ.depositPlayer(playerName, money);
							s.sendMessage(Methods.colorize(message));
							plugin.getConfig().set("players." + playerName + "." + desiredRank, true);
							plugin.saveConfig();
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (spy.containsKey(p.getName())) {
									s.sendMessage(Methods.colorize(SpyMessage));
								}
							}
							return true;
						}
					}
				}
				return true;
			}
		}; donatorfunds.setExecutor(exe);
	}
}
