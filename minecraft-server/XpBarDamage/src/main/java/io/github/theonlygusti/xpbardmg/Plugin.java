package io.github.theonlygusti.xpbardmg;

import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new EventHandlers(), this);
  }
}
