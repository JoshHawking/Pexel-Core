package me.dobrakmato.plugins.pexel.ZabiPitkesa;

import me.dobrakmato.plugins.pexel.PexelCore.LobbyMinigameArena;
import me.dobrakmato.plugins.pexel.PexelCore.Minigame;
import me.dobrakmato.plugins.pexel.PexelCore.Pexel;
import me.dobrakmato.plugins.pexel.PexelCore.Region;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author Mato Kormuth
 * 
 */
public class ZabiPitkesaArena extends LobbyMinigameArena
{
	private Pig		boss;
	private int		taskId;
	private long	ticks	= 0L;
	
	/**
	 * @param minigame
	 * @param arenaName
	 * @param region
	 * @param maxPlayers
	 */
	public ZabiPitkesaArena(final Minigame minigame, final String arenaName,
			final Region region, final int maxPlayers, final int minPlayers,
			final Location lobbyLocation, final Location gameSpawn)
	{
		super(minigame, arenaName, region, maxPlayers, 10, lobbyLocation,
				gameSpawn);
		this.setPlayersCanRespawn(false);
	}
	
	@Override
	public void onGameStart()
	{
		super.onGameStart();
		this.startTask();
		this.spawnBoss();
	}
	
	@Override
	public void onReset()
	{
		super.onReset();
		this.ticks = 0L;
	}
	
	@Override
	public void onGameEnd()
	{
		this.stopTask();
	}
	
	private void startTask()
	{
		this.taskId = Pexel.schedule(new Runnable() {
			@Override
			public void run()
			{
				//Update display name.
				ZabiPitkesaArena.this.updateBossHealth();
				//Each 3 seconds perform special attach.
				if ((ZabiPitkesaArena.this.ticks % 60) == 0)
					ZabiPitkesaArena.this.specialAttack();
				ZabiPitkesaArena.this.ticks += 5;
			}
		}, 0L, 5L);
	}
	
	private void specialAttack()
	{
		//Find target.
		Player target = this.findTraget();
		//Find attack - knockback. 
		target.setVelocity(target.getLocation().clone().subtract(
				this.boss.getLocation()).multiply(-1.5F).toVector());
		
		this.chatAll("The boss attacked '" + target.getDisplayName() + "'!");
	}
	
	private Player findTraget()
	{
		return this.activePlayers.get(Pexel.getRandom().nextInt(
				this.activePlayers.size() - 1));
	}
	
	private void updateBossHealth()
	{
		this.boss.setCustomName(ChatColor.RED + "pitkes22\nHealth: "
				+ ((Damageable) this.boss).getHealth() + "/"
				+ ((Damageable) this.boss).getMaxHealth());
	}
	
	private void stopTask()
	{
		Bukkit.getScheduler().cancelTask(this.taskId);
	}
	
	private void spawnBoss()
	{
		this.playSoundAll(Sound.WITHER_SPAWN, 1.0F, 1.0F);
		this.boss = (Pig) this.getWorld().spawnEntity(this.gameSpawn,
				EntityType.PIG);
		this.boss.setBreed(false);
		this.boss.setMaxHealth(500D);
		this.boss.setHealth(500D);
		this.boss.setCustomName(ChatColor.RED + "pitkes22");
		this.boss.setCustomNameVisible(true);
	}
	
	@EventHandler
	private void onEntityDied(final EntityDeathEvent event)
	{
		if (event.getEntity().getUniqueId() == this.boss.getUniqueId())
		{
			this.onBossDefeat();
		}
	}
	
	private void onBossDefeat()
	{
		this.chatAll("You managed to kill pitkes22! Congratulations!");
		
		this.kickAll();
	}
}
