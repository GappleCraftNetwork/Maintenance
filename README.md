# Maintenance
Allows the server to be toggled in an out of maintenance mode using the command /maintenance, /dev, or /devmode. When toggled into maintenance all servers are sent to the hub if they are not a dev or a whitelisted player.  If the server they are on already is the hub then they are kicked from the server.  The motd is also changed to reflect that the server is currently in maintenance.  The normal motd is restored when the server is toggled out of maintenance mode.  Additionally, if a player tries to join the server while it is in maintenance they are sent back to the hub, or kicked if they are trying to connect to the hub.  The exception to this is if the player is a dev or a whitelisted player.  In that case they are just given a warning that the server is in maintenance mode.