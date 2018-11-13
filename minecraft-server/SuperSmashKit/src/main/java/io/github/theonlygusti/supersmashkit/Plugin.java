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

  protected static double round(double value, int precision) {
    int scale = (int) Math.pow(10, precision);
    return (double) Math.round(value * scale) / scale;
  }

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
      @Override
      public void run() {
        for(Player player : plugin.getServer().getOnlinePlayers()){
          if (SuperSmashController.isKitted(player)) {
            SuperSmashKit kit = SuperSmashController.get(player);

            if (kit.getHeldItemAbility() != null) {
              ItemAbility heldItemAbility = kit.getHeldItemAbility();

              if (System.currentTimeMillis() < heldItemAbility.getLastTimeUsed() + heldItemAbility.getCooldownTime()) {
                long millisecondsSinceUsed = System.currentTimeMillis() - heldItemAbility.getLastTimeUsed();
                String blankProgressBar = "▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌";
                String progressBar = "§a" + (new StringBuilder(blankProgressBar).insert((int) Math.round(millisecondsSinceUsed * 24L / heldItemAbility.getCooldownTime()), "§r§c").toString()) + "§r";;
                String actionBar = "§f§l" + heldItemAbility.getName() + "§r " + progressBar + " §r§f" + round(((double) heldItemAbility.getCooldownTime() - millisecondsSinceUsed)/1000, 1) + " Seconds";;
                player.sendActionBar(actionBar);
              }
            }
          }
        }
      }
    }.runTaskTimer(this, 0L, 1L);
  }
}
