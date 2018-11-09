package io.github.theonlygusti.doublejump;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DoubleJump extends JavaPlugin {
  private static HashMap<Player, DoubleJumper> doubleJumpers = new HashMap<Player, DoubleJumper>();

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new EventHandlers(), this);
  }

  public static void set(Player player, DoubleJumper doubleJumper) {
    doubleJumpers.put(player, doubleJumper);
  }

  public static void unset(Player player) {
    doubleJumpers.remove(player);
  }

  public static DoubleJumper get(Player player) {
    return doubleJumpers.get(player);
  }
}
