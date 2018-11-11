package io.github.theonlygusti.supersmashkit;

import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
  @Override
  public void onEnable() {
    this.getCommand("kit").setExecutor(new Commander(this));
  }
}
