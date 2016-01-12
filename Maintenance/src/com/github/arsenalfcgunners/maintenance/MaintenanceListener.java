package com.github.arsenalfcgunners.maintenance;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * 
 * @author arsenalfcgunners
 * 
 * Listens for a player joining, or a server getting pinged.
 *
 */
@SuppressWarnings("deprecation")
public class MaintenanceListener implements Listener{
	Maintenance maintenance;
	String tag = ChatColor.GRAY+"["+ChatColor.GOLD+"GappleCraft"+ChatColor.GRAY+"] ";
	
	/** 
	 * @param plugin The maintenance plugin.
	 */
	public MaintenanceListener(Maintenance plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		maintenance = plugin;
	}
	
	/** 
	 * When in maintenance mode, lets a player know that the server is in maintenance mode.
	 * 
	 * @param e The event.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void playerJoin(PlayerJoinEvent e){
		if(maintenance.dev){
			e.getPlayer().sendMessage(tag+ChatColor.RED+"The server is currently in "+ChatColor.YELLOW+"maintenance "+ChatColor.RED+"mode. Normal players cannot join during this time.");
		}
	}
	
	/** 
	 * Listens for a player joining and kicks them if they are not a dev, or are not whitelisted.
	 * 
	 * @param e The event.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPreLogin(PlayerPreLoginEvent e){
		String name = e.getName();
		if(maintenance.dev && !PermissionsEx.getUser(e.getName()).has("gapple.dev") && !Bukkit.getOfflinePlayer(name).isWhitelisted()){
			e.disallow(Result.KICK_OTHER, "\n"+tag+ChatColor.RED+"The server is currently in "+ChatColor.YELLOW+"maintenance "+ChatColor.RED+"mode. We will be back soon. \n"+ChatColor.GRAY+"Check "+ChatColor.YELLOW+"www.gapplecraft.net"+ChatColor.GRAY+" for the latest updates.");
		}
	}
	
	/**
	 * Listens for the server getting pinged and changes the motd if the server is in maintenance.  If the server is not in maintenance it sets it back to the
	 * previous motd.
	 * 
	 * @param e The event.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void pingServer(ServerListPingEvent e){
		if(maintenance.dev){
			e.setMotd(tag+ChatColor.RED+"The server is in "+ChatColor.YELLOW+"maintenance "+ChatColor.RED+"mode.\n"+ChatColor.GRAY+"Check "+ChatColor.YELLOW+"www.gapplecraft.net"+ChatColor.GRAY+" for updates.");
		}
		else{
			e.setMotd(maintenance.config.getString("motd"));
		}
	}
}
