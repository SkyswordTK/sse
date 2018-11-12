package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.supersmashkit.SuperSmashController;
import io.github.theonlygusti.supersmashkit.kit.SkeletonKit;

import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
  @Override
  public void onEnable() {
    SuperSmashController.registerKit("skeleton", SkeletonKit::new);
    this.getCommand("kit").setExecutor(new Commander());
    //this.getCommand("dekit").setExecutor(new Commander());
  }
}
