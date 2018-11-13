package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.supersmashkit.SuperSmashController;
import io.github.theonlygusti.supersmashkit.kit.SkeletonKit;
import io.github.theonlygusti.supersmashkit.events.IllegalEvents;
import io.github.theonlygusti.supersmashkit.events.KitEvents;
import io.github.theonlygusti.supersmashkit.events.PlayerEvents;
import io.github.theonlygusti.supersmashkit.item.ItemAbility;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

public final class Plugin extends JavaPlugin {
  private static BukkitTask itemAbilityCooldownTask;

  @Override
  public void onEnable() {
    SuperSmashController.registerKit("skeleton", SkeletonKit::new);

    getServer().getPluginManager().registerEvents(new IllegalEvents(), this);
    getServer().getPluginManager().registerEvents(new KitEvents(), this);
    getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

    this.getCommand("kit").setExecutor(new Commander());
    this.getCommand("dekit").setExecutor(new Commander());

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
        for(Player player : plugin.getServer().getOnlinePlayers()){
          if (SuperSmashController.isKitted(player)) {
            SuperSmashKit kit = SuperSmashController.get(player);

            if (kit.getHeldItemAbility() != null) {
              ItemAbility heldItemAbility = kit.getHeldItemAbility();

              if (System.currentTimeMillis() < heldItemAbility.getLastTimeUsed() + heldItemAbility.getCooldownTime()) {
                String actionBar = buildCooldownGraphic(heldItemAbility.getName(), heldItemAbility.getCooldownTime(), heldItemAbility.getLastTimeUsed());
                player.sendActionBar(actionBar);
              } else if (System.currentTimeMillis() < heldItemAbility.getLastTimeUsed() + heldItemAbility.getCooldownTime() + tick) {
                String actionBar = buildCooldownGraphic(heldItemAbility.getName(), heldItemAbility.getCooldownTime(), System.currentTimeMillis() - heldItemAbility.getCooldownTime());
                player.sendActionBar(actionBar);
              }
            }
          }
        }
      }
    }.runTaskTimer(this, 0L, 1L);
  }
}
