package io.github.theonlygusti.doublejump;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DoubleJump extends JavaPlugin {
  private static HashMap<Player, DoubleJumper> doubleJumpers = new HashMap<Player, DoubleJumper>();
  private static HashMap<Player, Boolean> allowedFlight = new HashMap<Player, Boolean>();
  public static double tripleJumpActivationHeight = 0.4;
  // this is still too much, will need to change it to check for blocks above player
  public static int checkOffGroundLimit = 1;

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new EventHandlers(this), this);
    this.getCommand("doublejump").setExecutor(new DoubleJumpCommander(this));
  }

  public static void set(Player player, DoubleJumper doubleJumper) {
    allowedFlight.put(player, player.getAllowFlight());
    doubleJumpers.put(player, doubleJumper);
  }

  public static void unset(Player player) {
    player.setAllowFlight(allowedFlight.get(player));
    doubleJumpers.remove(player);
  }

  public static DoubleJumper get(Player player) {
    return doubleJumpers.get(player);
  }
}
