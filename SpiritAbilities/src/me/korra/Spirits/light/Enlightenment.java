package me.Pride.korra.Spirits.light;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.ParticleEffect;

import me.Pride.korra.Spirits.listener.AbilListener;
import me.xnuminousx.spirits.ability.api.LightAbility;
import me.xnuminousx.spirits.elements.SpiritElement;
import net.md_5.bungee.api.ChatColor;

public class Enlightenment extends LightAbility implements AddonAbility {
	
	private static String path = "ExtraAbilities.Prride.Spirits.Abilities.Light.Enlightenment.";
	FileConfiguration config = ConfigManager.getConfig();
	
	private ArrayList<Entity> enlighteners = new ArrayList<Entity>();
	
	private long cooldown;
	private double radius;
	private int potionPower;
	private int potionDuration;
	private float absorptionHealth;
	private double chargeTime;
	
	private double size;
	private int rotation;
	private double time;
	private boolean charged;
	
	private Entity entities;
	Random rand = new Random();

	public Enlightenment(Player player) {
		super(player);
		
		if (!bPlayer.canBend(this)) {
			return;
		}
		
		cooldown = config.getLong(path + "Cooldown");
		radius = config.getDouble(path + "EnlightenRadius");
		potionPower = config.getInt(path + "EffectAmplifier");
		potionDuration = config.getInt(path + "EffectDuration");
		absorptionHealth = config.getInt(path + "AbsorptionHealth");
		chargeTime = config.getDouble(path + "ChargeTime");
		
		start();
	}
	
	@Override
	public boolean isEnabled() {
		return ConfigManager.getConfig().getBoolean("ExtraAbilities.Prride.Spirits.Abilities.Light.Enlightenment.Enabled");
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "Enlightenment";
	}

	@Override
	public boolean isExplosiveAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isIgniteAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public void progress() {
		if (player.isSneaking()) {
			
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1F, 1F);
			
			time += 0.05;
			
			if (time >= chargeTime) {
				charged = true;
				ParticleEffect.SPELL_INSTANT.display(player.getLocation(), 1, 0F, 0F, 0F, 0.1F);
			}
			
			if (size >= radius) {
				size = 0;
			}
			
			if (size == 0) {
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
			}
			
			size += 0.1;
			rotation++;
			for (int i = -180; i < 180; i += 20) {
		        double angle = i * 3.141592653589793D / 180.0D;
		        double x = size * Math.cos(angle + rotation);
		        double z = size * Math.sin(angle + rotation);
		        Location loc = player.getLocation().clone();
		        loc.add(x, 0, z);
		        ParticleEffect.CRIT_MAGIC.display(loc, 1, 0F, 0F, 0F, 0F);
	    	}
			
			for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), radius)) {
				if (entity.getUniqueId() != player.getUniqueId() && entity instanceof LivingEntity) {
					
					if (entity instanceof Player) {
		                Player ePlayer = (Player) entity;
		                BendingPlayer bEntity = BendingPlayer.getBendingPlayer(ePlayer);
		                
		                Element lightSpirit = SpiritElement.LIGHT_SPIRIT;
		                Element spirit = SpiritElement.SPIRIT;
		                
		                if (bEntity.hasElement(lightSpirit) || bEntity.hasElement(spirit)) {
		                	for (int i = -180; i < 180; i += 20) {
						        double angle = i * 3.141592653589793D / 180.0D;
						        double x = size * Math.cos(angle + rotation);
						        double z = size * Math.sin(angle + rotation);
						        Location entityLoc = entity.getLocation().clone();
						        entityLoc.add(x, 0, z);
						        ParticleEffect.CRIT_MAGIC.display(entityLoc, 1, 0F, 0F, 0F, 0F);
					    	}
							entities = entity;
							enlighteners.add(entity);
		                }
					}
				}
			}
		} else {
			if (charged) {
				enlighten();
				bPlayer.addCooldown(this);
				remove();
				return;
			} else {
				bPlayer.addCooldown(this);
				remove();
				return;
			}
		}
	}
	
	private void enlighten() {
		ParticleEffect.BLOCK_CRACK.display(player.getLocation().add(0, 2, 0), 3, 0.2F, 0.2F, 0.2F, 0F, Material.GLOWSTONE.createBlockData());
		player.getWorld().playSound(player.getLocation().add(0, 2, 0), Sound.BLOCK_GLASS_BREAK, 1F, 1F);
		
		if (enlighteners.contains(entities)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, potionDuration + 80, potionPower + 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, potionDuration + 80, potionPower + 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, potionDuration + 80, potionPower + 1));
			
			GeneralMethods.setAbsorbationHealth(player, absorptionHealth * 2);
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, potionDuration, potionPower));
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, potionDuration, potionPower));
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, potionDuration, potionPower));
			
			GeneralMethods.setAbsorbationHealth(player, absorptionHealth);
		}
	}

	@Override
	public String getDescription() {
		return SpiritElement.DARK_SPIRIT.getColor() + "Enlightenment allows the user to gain buffs and positive effects "
				+ "through the use of spiritual knowledge! With the help of other spirits and light spirits, buffs are "
				+ "more stronger and effective!";
	}
	
	@Override
	public String getInstructions() {
		return ChatColor.GOLD + "To use, hold sneak until a certain time and release. If close to other spirits or light spirits, "
				+ "your buffs increase.";
	}

	@Override
	public String getAuthor() {
		return SpiritElement.DARK_SPIRIT.getColor() + "" + ChatColor.UNDERLINE + 
				"Prride";
	}

	@Override
	public String getVersion() {
		return SpiritElement.DARK_SPIRIT.getColor() + "" + ChatColor.UNDERLINE + 
				"VERSION 1";
	}

	@Override
	public void load() {
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new AbilListener(), ProjectKorra.plugin);
		
		ConfigManager.getConfig().addDefault(path + "Enabled", true);
		ConfigManager.getConfig().addDefault(path + "Cooldown", 6500);
		ConfigManager.getConfig().addDefault(path + "ChargeTime", 4.5);
		ConfigManager.getConfig().addDefault(path + "EnlightenRadius", 1.5);
		ConfigManager.getConfig().addDefault(path + "EffectAmplifier", 2);
		ConfigManager.getConfig().addDefault(path + "EffectDuration", 200);
		ConfigManager.defaultConfig.save();
	}

	@Override
	public void stop() {
		ProjectKorra.log.info(getName() + " by " + getAuthor() + " " + getVersion() + " stopped!");
		super.remove();
	}

}
