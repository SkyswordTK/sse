package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.supersmashkit.kit.SkeletonKit;
import java.util.HashMap;
import java.util.function.BiFunction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
  public static HashMap<String, BiFunction<Player, Plugin, SuperSmashKit>> kits = new HashMap<String, BiFunction<Player, Plugin, SuperSmashKit>>();

  @Override
  public void onEnable() {
    kits.put("skeleton", SkeletonKit::new);
    this.getCommand("kit").setExecutor(new Commander(this));
  }
}
