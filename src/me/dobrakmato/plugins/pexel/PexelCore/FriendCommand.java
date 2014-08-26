package me.dobrakmato.plugins.pexel.PexelCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class used for handling 'friends' command.
 * 
 * @author Mato Kormuth
 * 
 */
public class FriendCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String paramString, final String[] args)
	{
		if (command.getName().equalsIgnoreCase("friend"))
		{
			if (sender instanceof Player)
			{
				if (sender.isOp())
				{
					this.processOpCommand((Player) sender, args);
				}
				else
				{
					this.processCommand((Player) sender, args);
				}
			}
			else
			{
				sender.sendMessage(ChatManager.error("This command is only avaiable for players!"));
			}
			return true;
		}
		return false;
	}
	
	private void processCommand(final Player sender, final String[] args)
	{
		if (args.length >= 1)
		{
			String playerName = args[0];
			boolean success = false;
			for (Player p : Bukkit.getOnlinePlayers())
			{
				if (p.getName().equalsIgnoreCase(playerName))
				{
					StorageEngine.getProfile(sender.getUniqueId()).addFriend(
							p.getUniqueId());
					sender.sendMessage(ChatManager.success("Player '"
							+ p.getName() + "' has been ADDED to your friends!"));
					p.sendMessage(ChatManager.success("Player '"
							+ sender.getName()
							+ "' added you to his/her friends! Add him too! /friend "
							+ sender.getName()));
					
					success = true;
				}
			}
			if (!success)
				sender.sendMessage(ChatManager.error("Player not found! Player must be online!"));
		}
		else
		{
			sender.sendMessage(ChatManager.error("You must provide player name!"));
		}
	}
	
	private void processOpCommand(final Player sender, final String[] args)
	{
		//No op commands.
		this.processCommand(sender, args);
	}
}
