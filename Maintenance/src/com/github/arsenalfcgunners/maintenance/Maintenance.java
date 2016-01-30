package com.github.arsenalfcgunners.maintenance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author arsenalfcgunners
 * @version 1.0
 * 
 * This plugin allows the server to be put into maintenance mode. In this mode only dev ranks and up, or whitelisted players
 * are allowed on the server. All other players are sent to the hub or kicked. The server motd is also changed to reflect
 * that the server is in maintenance mode.  The normal motd is restored after the server is taken out of maintenance mode.
 * Both the mode of the server and the motd are stored in a yml file so they are not lost in a server restart or reload.
 */
public class Maintenance extends JavaPlugin{
	private boolean dev = false;
	private File file;
	private YamlConfiguration config;
	private String tag;
	private String server;
	private MessagingChannel mc;
	
	/**
	 * Performed when the plugin is enabled.
	 */
	@Override
	public void onEnable(){
		tag = ChatColor.GRAY+"["+ChatColor.GOLD+"GappleCraft"+ChatColor.GRAY+"] ";
		
		file = new File(getDataFolder()+File.separator+"/config.yml");
		config = YamlConfiguration.loadConfiguration(file);
		
		mc = new MessagingChannel(this);

		if(config.getString("status").equals("true")){
			dev = true;
		}
		
		if(config.getString("server") != null){
			server = config.getString("server");
		}
		
		else{
			List<Player> online = new ArrayList<Player>(Bukkit.getOnlinePlayers());
			if(online.size() != 0){
				mc.getServerName(online.get(0));
			}
		}
		
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
				
		new MaintenanceListener(this);
	}
	
	/**
	 * Performed when the plugin is disabled.
	 */
	@Override
	public void onDisable(){
		saveConfig(config, file);
	}
	
	/** 
	 * Saves the config file.
	 * 
	 * @param ymlConfig The config.
	 * @param ymlFile   The file.
	 */
	public void saveConfig(FileConfiguration ymlConfig, File ymlFile) {
		try {
			ymlConfig.save(ymlFile);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * If the command received is /maintenance, /dev, or /devmode the mode of the server is changed and true is returned. If the command
	 * is not recognized false is returned.
	 * 
	 * @param sender  The executer of the command.
	 * @param cmd The command performed.
	 * @param label   Not used.
	 * @param args    An array with the text entered after the command. 
	 * 
	 * @return If the command was recognized return true, false otherwise.
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("maintenance") || cmd.getName().equalsIgnoreCase("dev") || cmd.getName().equalsIgnoreCase("devmode")){
			if(sender.hasPermission("gapple.dev")){
				dev = !dev;
				if(dev){
					kickPlayers();
					config.set("status", "true");
					config.set("motd", Bukkit.getMotd());
					saveConfig(config, file);		
					
					Bukkit.broadcastMessage(tag+ChatColor.RED+"The server is now in maintenance mode.");
				}
				else{
					config.set("status", "false");
					saveConfig(config, file);
					
					Bukkit.broadcastMessage(tag+ChatColor.GREEN+"The server is no longer in maintenance mode.");
				}
			}
			else{
				sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: You do not have permission.");
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Sends all players to hub who are not whitelisted or don't have the "gappe.dev" permission. If the server is the hub, then they are kicked.  
	 */
	public void kickPlayers(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(!player.hasPermission("gapple.dev") && !player.isWhitelisted()){
				if(server != null && !server.equalsIgnoreCase("hub")){
					player.sendMessage(tag+ChatColor.RED+"The server has gone into "+ChatColor.YELLOW+"maintenance "+ChatColor.RED+"mode. We will be back soon. "+ChatColor.GRAY+"Check "+ChatColor.YELLOW+"www.gapplecraft.net"+ChatColor.GRAY+" for the latest updates.");
					mc.connectToServer(player, "hub");
				}
				else{
					player.kickPlayer(tag+ChatColor.RED+"The server has gone into "+ChatColor.YELLOW+"maintenance "+ChatColor.RED+"mode. We will be back soon.\n"+ChatColor.GRAY+"Check "+ChatColor.YELLOW+"www.gapplecraft.net"+ChatColor.GRAY+" for the latest updates.");
				}
			}
		}
	}
	
	/**
	 * Getter method for the MessagingChannel.
	 * 
	 * @return The MessagingChannel.
	 */
	public MessagingChannel getMessagingChannel(){
		return mc;
	}
	
	/**
	 * Sets the server name and saves it to the config.
	 * 
	 * @param sn The name of the server.
	 */
	public void setServerName(String sn){
		server = sn;
		config.set("server", server);
		saveConfig(config, file);
	}
	
	/**
	 * True if server is in dev mode, false if it is not.
	 * 
	 * @return Whether or not the server is in maitenance. 
	 */
	public boolean getDevStatus(){
		return dev;
	}
	
	/**
	 * Gets the config.
	 * 
	 * @return The configuration.
	 */
	public FileConfiguration getConfig(){
		return config;
	}
	
	/**
	 * Returns the chat message tag.
	 * 
	 * @return The string for the tag.
	 */
	public String getTag(){
		return tag;
	}
}
