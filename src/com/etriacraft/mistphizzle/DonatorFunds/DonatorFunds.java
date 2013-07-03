package com.etriacraft.mistphizzle.DonatorFunds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DonatorFunds extends JavaPlugin {

	protected static Logger log;
	public static DonatorFunds instance;
	public static Economy econ = null;

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			econ = economyProvider.getProvider();
		}
		return (econ != null);
	}

	File configFile;
	FileConfiguration config;

	Commands cmd;

	@Override
	public void onEnable() {
		instance = this;
		this.log = this.getLogger();

		configFile = new File(getDataFolder(), "config.yml");

		try {
			firstRun();
		} catch (Exception e) {
			e.printStackTrace();
		}

		setupEconomy();

		config = new YamlConfiguration();
		loadYamls();

		cmd = new Commands(this);
	}
	
	public void firstRun() throws Exception {
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
			log.info("Config not found. Generating.");
		}
	}
	
	private void loadYamls() {
		try {
			config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf))>0) {
				out.write(buf,0,len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveYamls() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DonatorFunds getInstance() {
		return instance;
	}
}
