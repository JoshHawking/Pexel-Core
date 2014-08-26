package me.dobrakmato.plugins.pexel.ColorWar;

import me.dobrakmato.plugins.pexel.PexelCore.AdvancedMinigameArena;
import me.dobrakmato.plugins.pexel.PexelCore.AreaFlag;
import me.dobrakmato.plugins.pexel.PexelCore.ArenaOption;
import me.dobrakmato.plugins.pexel.PexelCore.GameState;
import me.dobrakmato.plugins.pexel.PexelCore.ItemUtils;
import me.dobrakmato.plugins.pexel.PexelCore.Minigame;
import me.dobrakmato.plugins.pexel.PexelCore.ParticleEffect2;
import me.dobrakmato.plugins.pexel.PexelCore.Pexel;
import me.dobrakmato.plugins.pexel.PexelCore.Region;
import me.dobrakmato.plugins.pexel.PexelCore.Team;
import me.dobrakmato.plugins.pexel.PexelCore.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Color War minigame by Dobrakmato.
 * 
 * @author Mato Kormuth
 * 
 */
public class ColorWarMinigameArena extends AdvancedMinigameArena
{
	private Team				redTeam			= new Team(Color.RED,
														"Red team",
														this.slots / 4);
	private Team				blueTeam		= new Team(Color.BLUE,
														"Blue team",
														this.slots / 4);
	private Team				greenTeam		= new Team(Color.GREEN,
														"Green team",
														this.slots / 4);
	private Team				yellowTeam		= new Team(Color.YELLOW,
														"Yellow team",
														this.slots / 4);
	
	private final TeamManager	manager;
	
	@ArenaOption(name = "redSpawn")
	public Location				redSpawn;
	@ArenaOption(name = "blueSpawn")
	public Location				blueSpawn;
	@ArenaOption(name = "greenSpawn")
	public Location				greenSpawn;
	@ArenaOption(name = "yellowSpawn")
	public Location				yellowSpawn;
	
	@ArenaOption(name = "maskSnowballs")
	public boolean				maskSnowBalls	= false;
	
	private int					taskId;
	
	//Slots
	@ArenaOption(name = "zbranSlot")
	private final int			zbranSlot		= 36;
	@ArenaOption(name = "colorSlot")
	private final int			colorSlot		= 37;
	@ArenaOption(name = "helmetSlot")
	private final int			sArmor1			= 41;
	@ArenaOption(name = "chestplateSlot")
	private final int			sArmor2			= 42;
	@ArenaOption(name = "leggingsSlot")
	private final int			sArmor3			= 43;
	@ArenaOption(name = "bootsSlot")
	private final int			sArmor4			= 44;
	
	/**
	 * @param minigame
	 * @param arenaName
	 * @param region
	 * @param maxPlayers
	 * @param minPlayers
	 * @param lobbyLocation
	 * @param gameSpawn
	 */
	public ColorWarMinigameArena(final Minigame minigame,
			final String arenaName, final Region region,
			final Location lobbyLocation, final Location gameSpawn)
	{
		super(minigame, arenaName, region, 32, 4, lobbyLocation, gameSpawn);
		this.setGlobalFlag(AreaFlag.BLOCK_PLACE, false);
		this.inventoryDisabled = true;
		
		this.manager = new TeamManager(this);
		this.manager.addTeam(this.redTeam);
		this.manager.addTeam(this.blueTeam);
		this.manager.addTeam(this.greenTeam);
		this.manager.addTeam(this.yellowTeam);
	}
	
	@Override
	public void onGameStart()
	{
		super.onGameStart();
		
		for (Player p : this.activePlayers)
			this.clearPlayer(p);
		
		this.redTeam.applyArmorAll();
		this.blueTeam.applyArmorAll();
		this.greenTeam.applyArmorAll();
		this.yellowTeam.applyArmorAll();
		
		for (Player p : this.redTeam.getPlayers())
		{
			p.getInventory().setItem(this.zbranSlot, this.getZbran());
			p.getInventory().setItem(this.colorSlot, this.getColorItem(14));
			this.updateHotBar(p);
		}
		
		for (Player p : this.blueTeam.getPlayers())
		{
			p.getInventory().setItem(this.zbranSlot, this.getZbran());
			p.getInventory().setItem(this.colorSlot, this.getColorItem(11));
			this.updateHotBar(p);
		}
		
		for (Player p : this.greenTeam.getPlayers())
		{
			p.getInventory().setItem(this.zbranSlot, this.getZbran());
			p.getInventory().setItem(this.colorSlot, this.getColorItem(5));
			this.updateHotBar(p);
		}
		
		for (Player p : this.yellowTeam.getPlayers())
		{
			p.getInventory().setItem(this.zbranSlot, this.getZbran());
			p.getInventory().setItem(this.colorSlot, this.getColorItem(4));
			this.updateHotBar(p);
		}
		
		this.redTeam.teleportAll(this.redSpawn);
		this.blueTeam.teleportAll(this.blueSpawn);
		this.greenTeam.teleportAll(this.gameSpawn);
		this.yellowTeam.teleportAll(this.yellowSpawn);
		
		this.taskId = Pexel.schedule(new Runnable() {
			@Override
			public void run()
			{
				ColorWarMinigameArena.this.makeTrails();
			}
		}, 0L, 2L);
	}
	
	private void playerHitByColor(final Team team, final Player p)
	{
		switch (Pexel.getRandom().nextInt(3))
		{
			case 0:
				this.setColor(p.getInventory().getHelmet(), team.getColor());
				break;
			case 1:
				this.setColor(p.getInventory().getChestplate(), team.getColor());
				break;
			case 2:
				this.setColor(p.getInventory().getLeggings(), team.getColor());
				break;
			case 3:
				this.setColor(p.getInventory().getBoots(), team.getColor());
				break;
		}
		
		this.updateHotBar(p);
		
		if (this.playerAllSameColor(p))
			this.playerChangeTeam(p, team);
	}
	
	private void playerChangeTeam(final Player p, final Team toTeam)
	{
		this.getTeamByPlayer(p).removePlayer(p);
		toTeam.addPlayer(p);
		
		if (toTeam == this.redTeam)
			p.getInventory().setItem(this.colorSlot, this.getColorItem(14));
		else if (toTeam == this.blueTeam)
			p.getInventory().setItem(this.colorSlot, this.getColorItem(11));
		else if (toTeam == this.greenTeam)
			p.getInventory().setItem(this.colorSlot, this.getColorItem(5));
		else if (toTeam == this.yellowTeam)
			p.getInventory().setItem(this.colorSlot, this.getColorItem(4));
		
		p.playSound(p.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
	}
	
	private boolean playerAllSameColor(final Player p)
	{
		Color c = this.getColor(p.getInventory().getHelmet());
		if (c != this.getColor(p.getInventory().getChestplate())
				|| c != this.getColor(p.getInventory().getLeggings())
				|| c != this.getColor(p.getInventory().getBoots()))
			return false;
		return true;
	}
	
	private Color getColor(final ItemStack item)
	{
		return ((LeatherArmorMeta) item.getData()).getColor();
	}
	
	private void setColor(final ItemStack item, final Color color)
	{
		((LeatherArmorMeta) item.getData()).setColor(color);
	}
	
	private Team getTeamByPlayer(final Player p)
	{
		if (this.redTeam.contains(p))
			return this.redTeam;
		if (this.blueTeam.contains(p))
			return this.blueTeam;
		if (this.greenTeam.contains(p))
			return this.greenTeam;
		if (this.yellowTeam.contains(p))
			return this.yellowTeam;
		
		this.onPlayerLeft(p);
		throw new RuntimeException("Player " + p.getName()
				+ " was not fond in any team!");
	}
	
	private void updateHotBar(final Player p)
	{
		p.getInventory().setItem(this.sArmor1,
				p.getInventory().getHelmet().clone());
		p.getInventory().setItem(this.sArmor2,
				p.getInventory().getChestplate().clone());
		p.getInventory().setItem(this.sArmor3,
				p.getInventory().getLeggings().clone());
		p.getInventory().setItem(this.sArmor4,
				p.getInventory().getBoots().clone());
	}
	
	private ItemStack getColorItem(final int color)
	{
		@SuppressWarnings("deprecation") ItemStack stack = new ItemStack(
				Material.WOOL, 1, (short) 0, (byte) color);
		ItemMeta im = stack.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "Your color");
		return stack;
	}
	
	private ItemStack getZbran()
	{
		return ItemUtils.namedItemStack(Material.INK_SACK,
				ChatColor.RESET.toString() + ChatColor.BOLD + "Color Gun", null);
	}
	
	@Override
	public void onReset()
	{
		super.onReset();
		
		Bukkit.getScheduler().cancelTask(this.taskId);
		
		this.redTeam = new Team(Color.RED, "Red team", this.slots / 4);
		this.blueTeam = new Team(Color.BLUE, "Blue team", this.slots / 4);
		this.greenTeam = new Team(Color.GREEN, "Green team", this.slots / 4);
		this.yellowTeam = new Team(Color.YELLOW, "Yellow team", this.slots / 4);
		
		this.manager.reset();
		
		this.manager.addTeam(this.redTeam);
		this.manager.addTeam(this.blueTeam);
		this.manager.addTeam(this.greenTeam);
		this.manager.addTeam(this.yellowTeam);
		
		this.state = GameState.WAITING_EMPTY;
	}
	
	private void gunFire(final Player p)
	{
		System.out.println("gunFire();");
		Snowball ball = (Snowball) this.gameSpawn.getWorld().spawnEntity(
				p.getEyeLocation().add(
						p.getEyeLocation().getDirection().multiply(2F)),
				EntityType.SNOWBALL);
		ball.setVelocity(p.getEyeLocation().getDirection().multiply(1.5F));
		ball.setMetadata("damagerTeam", new FixedMetadataValue(Pexel.getCore(),
				this.getTeamByPlayer(p)));
		
		p.playSound(p.getLocation(), Sound.IRONGOLEM_THROW, 1F, 0.5F);
		
		/*
		 * if (this.maskSnowBalls) for (Player player : this.activePlayers) { ((CraftPlayer)
		 * player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy( ball.getEntityId())); }
		 */
	}
	
	private void makeTrails()
	{
		for (Entity e : this.gameSpawn.getWorld().getEntities())
			if (e instanceof Snowball)
				if (this.region.intersectsXZ(e.getLocation()))
				{
					Team team = (Team) e.getMetadata("damagerTeam").get(0).value();
					byte color = 0;
					
					if (team == this.blueTeam)
						color = 11;
					else if (team == this.redTeam)
						color = 14;
					else if (team == this.greenTeam)
						color = 5;
					else if (team == this.yellowTeam)
						color = 4;
					
					ParticleEffect2.displayBlockCrack(e.getLocation(), 16, 35,
							color, 0.05F, 0.05F, 0.05F, 10);
				}
	}
	
	@Override
	public void onPlayerJoin(final Player player)
	{
		super.onPlayerJoin(player);
		
		//debug
		if (player.getName().equalsIgnoreCase("dobrakmato"))
		{
			this.greenTeam.addPlayer(player);
			player.getInventory().addItem(this.getZbran());
		}
		else if (player.getName().equalsIgnoreCase("pitkes22"))
		{
			this.redTeam.addPlayer(player);
			player.getInventory().addItem(this.getZbran());
		}
	}
	
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		if (this.activePlayers.contains(event.getPlayer()))
			this.gunFire(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerInteractEntity(final PlayerInteractEntityEvent event)
	{
		if (this.activePlayers.contains(event.getPlayer()))
			this.gunFire(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onHitBySnowball(final EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Snowball)
			if (event.getEntity() instanceof Player)
				if (this.activePlayers.contains(event.getEntity()))
				{
					this.playerHitByColor(
							(Team) event.getDamager().getMetadata("damagerTeam").get(
									0).value(), ((Player) event.getEntity()));
				}
	}
	
	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent event)
	{
		if (event.getEntity() instanceof Snowball)
			if (event.getEntity().hasMetadata("damagerTeam"))
			{
				Team team = (Team) event.getEntity().getMetadata("damagerTeam").get(
						0).value();
				byte color = 0;
				
				if (team == this.blueTeam)
					color = 11;
				else if (team == this.redTeam)
					color = 14;
				else if (team == this.greenTeam)
					color = 5;
				else if (team == this.yellowTeam)
					color = 4;
				
				ParticleEffect2.displayBlockCrack(
						event.getEntity().getLocation(), 16, 35, color, 0.5F,
						0.5F, 0.5F, 50);
			}
	}
}
