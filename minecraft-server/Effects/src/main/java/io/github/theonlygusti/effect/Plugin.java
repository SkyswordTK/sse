package io.github.theonlygusti.effect;

import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new ItemExplosionHandler(this), this);
  }
}
