package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.supersmashkit.SuperSmashController;
import io.github.theonlygusti.supersmashkit.kit.SkeletonKit;
import io.github.theonlygusti.supersmashkit.events.IllegalEvents;
import io.github.theonlygusti.supersmashkit.events.KitEvents;
import io.github.theonlygusti.supersmashkit.events.PlayerEvents;

import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
  @Override
  public void onEnable() {
    SuperSmashController.registerKit("skeleton", SkeletonKit::new);

    getServer().getPluginManager().registerEvents(new IllegalEvents(), this);
    getServer().getPluginManager().registerEvents(new KitEvents(), this);
    getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

    this.getCommand("kit").setExecutor(new Commander());
    this.getCommand("dekit").setExecutor(new Commander());
  }
}
