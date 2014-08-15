package me.dobrakmato.plugins.pexel.PexelCore;

import me.dobrakmato.plugins.pexel.TntTag.TntTagMinigame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Alternate method of handling commands.
 * 
 * @author Mato Kormuth
 * 
 */
public class AlternativeCommands implements Listener
{
	public AlternativeCommands()
	{
		Bukkit.getPluginManager().registerEvents(this, Pexel.getCore());
	}
	
	@EventHandler
	private void onPrepocessCommand(final PlayerCommandPreprocessEvent event)
	{
		String command = event.getMessage().toLowerCase().replace("/", "");
		Player sender = event.getPlayer();
		if (command.equalsIgnoreCase("getcock"))
		{
			sender.getInventory().addItem(Pexel.getMagicClock().getClock());
		}
		else if (command.equalsIgnoreCase("list_arena_aliases"))
		{
			for (String key : StorageEngine.getAliases().keySet())
			{
				sender.sendMessage(ChatColor.BLUE + key + ChatColor.WHITE
						+ " = " + ChatColor.GREEN
						+ StorageEngine.getByAlias(key).getName());
			}
		}
		else if (command.contains("tnttest"))
		{
			sender.sendMessage(ChatColor.RED
					+ "Pexel.getMatchmaking().registerRequest(new MatchmakingRequest(Arrays.asList(event.getPlayer()),StorageEngine.getMinigame(\"tnttag\"), null));");
			((TntTagMinigame) StorageEngine.getMinigame("tnttag")).trrtrtr().onPlayerJoin(
					event.getPlayer());
		}
	}
}
