package com.github.arsenalfcgunners.maintenance;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * 
 * @author arsenalfcgunners
 *
 * This class sends the PluginMessages to communicate with the BungeeCord.  It can send players to a specific server, or get the name of the server.
 */
public class MessagingChannel implements PluginMessageListener{
	Maintenance m;
	
	public MessagingChannel(Maintenance plugin){
		m = plugin;
		m.getServer().getMessenger().registerOutgoingPluginChannel(m, "BungeeCord");
	    m.getServer().getMessenger().registerIncomingPluginChannel(m, "BungeeCord", this);
	}

	/**
	 * A listener for the plugin message.
	 * 
	 * @param channel The channel that the message was sent through.
	 * @param player  The player that sent the message.
	 * @param message The message that was sent.
	 */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
		    String subchannel = in.readUTF();
		    if (subchannel.equals("GetServer")) {
		    	m.setServerName(in.readUTF());
		    }
		}
	}
	
	/**
	 * Sends a plugin message to get the name of this server.
	 * 
	 * @param player The player who will send the message.
	 */
	public void getServerName(Player player){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServer");

		player.sendPluginMessage(m, "BungeeCord", out.toByteArray());
	}
	
	/** 
	 * Connects a player to another server.
	 * 
	 * @param player The player to connect to the server.
	 * @param server The server to connect to.
	 */
	public void connectToServer(Player player, String server){
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		player.sendPluginMessage(m, "BungeeCord", b.toByteArray());
	}
}
