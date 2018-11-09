package io.github.theonlygusti.doublejump;

import org.bukkit.plugin.java.JavaPlugin;

public final class DoubleJump extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new EventHandlers(), this);
  }
}
