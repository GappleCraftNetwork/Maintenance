package com.github.arsenalfcgunners.maintenance;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * 
 * @author arsenalfcgunners
 * 
 * Listens for a player joining, or a server getting pinged.
 *
 */
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
	 * Listens for a player joining and kicks them or sends them back to hub if the server is in maintenance mode and they do not have the permission "gapple.dev" or are whitelisted.
	 * They are sent to the hub if the server is not the hub.  If the server is the hub they are kicked from the network.
	 * 
	 * @param e The event.
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void playerJoin(PlayerJoinEvent e){
		final Player player = e.getPlayer();
		if(maintenance.dev && !player.isWhitelisted() && !player.hasPermission("gapple.dev")){
			if(maintenance.server != null && !maintenance.server.equalsIgnoreCase("hub")){
				player.sendMessage(tag+ChatColor.RED+"The server is currently in "+ChatColor.YELLOW+"maintenance "+ChatColor.RED+"mode. We will be back soon. "+ChatColor.GRAY+"Check "+ChatColor.YELLOW+"www.gapplecraft.net"+ChatColor.GRAY+" for the latest updates.");
				Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(maintenance, new Runnable() { // Plugin messages need delay in join event.
		            public void run() {
		            	if(maintenance.server == null){
		            		maintenance.getMessagingChannel().getServerName(player);
		            	}
						maintenance.getMessagingChannel().connectToServer(player, "hub");
		            }
		        }, 2L);
			}
			else{
				player.kickPlayer(tag+ChatColor.RED+"The server is currently in "+ChatColor.YELLOW+"maintenance "+ChatColor.RED+"mode. We will be back soon.\n"+ChatColor.GRAY+"Check "+ChatColor.YELLOW+"www.gapplecraft.net"+ChatColor.GRAY+" for the latest updates.");
			}
		}
		else if(maintenance.dev){
			player.sendMessage(tag+ChatColor.RED+"The server is currently in "+ChatColor.YELLOW+"maintenance "+ChatColor.RED+"mode. Normal players cannot join during this time.");
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
