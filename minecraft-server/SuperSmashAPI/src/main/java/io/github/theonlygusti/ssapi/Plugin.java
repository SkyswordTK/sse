package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.ssapi.SuperSmashController;
import io.github.theonlygusti.ssapi.events.IllegalEvents;
import io.github.theonlygusti.ssapi.events.KitEvents;
import io.github.theonlygusti.ssapi.events.PlayerEvents;
import io.github.theonlygusti.ssapi.item.ItemAbility;
import io.github.theonlygusti.ssapi.passive.Passive;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

public final class Plugin extends JavaPlugin {
  private BukkitTask itemAbilityCooldownTask;
  private Commander commander;
  private BukkitTask runPassivesTask;
  public static Plugin plugin;

  @Override
  public void onEnable() {
    plugin = this;

    getServer().getPluginManager().registerEvents(new IllegalEvents(), this);
    getServer().getPluginManager().registerEvents(new KitEvents(), this);
    getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

    commander = new Commander(this);
    this.getCommand("kit").setExecutor(commander);
    this.getCommand("dekit").setExecutor(commander);

    this.getCommand("kit").setTabCompleter(new KitCompleter());

    Plugin plugin = this;

    itemAbilityCooldownTask = new BukkitRunnable() {
      private long tick = 50;

      private double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
      }

      private String buildCooldownGraphic(String abilityName, long cooldownTime, long lastTimeUsed) {
        long millisecondsSinceUsed = System.currentTimeMillis() - lastTimeUsed;
        String blankProgressBar = "▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌";
        String progressBar = "§a" + (new StringBuilder(blankProgressBar).insert((int) Math.round(millisecondsSinceUsed * 24L / cooldownTime), "§r§c").toString()) + "§r";
        return "§f§l" + abilityName + "§r " + progressBar + " §r§f" + round(((double) cooldownTime - millisecondsSinceUsed)/1000, 1) + " Seconds";
      }

      @Override
      public void run() {
        for(SuperSmashKit kit : SuperSmashController.getPlayerKits()){
          if (kit.getHeldItemAbility() != null) {
            ItemAbility heldItemAbility = kit.getHeldItemAbility();

            if (System.currentTimeMillis() < heldItemAbility.getLastTimeUsed() + heldItemAbility.getCooldownTime()) {
              String actionBar = buildCooldownGraphic(heldItemAbility.getName(), heldItemAbility.getCooldownTime(), heldItemAbility.getLastTimeUsed());
              kit.getPlayer().sendActionBar(actionBar);
            } else if (System.currentTimeMillis() < heldItemAbility.getLastTimeUsed() + heldItemAbility.getCooldownTime() + tick) {
              String actionBar = buildCooldownGraphic(heldItemAbility.getName(), heldItemAbility.getCooldownTime(), System.currentTimeMillis() - heldItemAbility.getCooldownTime());
              kit.getPlayer().sendActionBar(actionBar);
            }
          }
        }
      }
    }.runTaskTimer(this, 0L, 1L);

    runPassivesTask = new BukkitRunnable() {
      @Override
      public void run() {
        for(SuperSmashKit kit : SuperSmashController.getPlayerKits()){
          for (Passive passive : kit.getPassives()) {
            Boolean wasStarted = SuperSmashController.getWasPassiveStarted(passive);

            if (!wasStarted && passive.shouldStart()) {
              SuperSmashController.startPassive(passive, plugin);
              wasStarted = true;
            }

            if (wasStarted && !passive.shouldStart()) {
              SuperSmashController.toggleWasPassiveStarted(passive);
              wasStarted = false;
            }
          }
        }
      }
    }.runTaskTimer(this, 0L, 1L);
  }
}
