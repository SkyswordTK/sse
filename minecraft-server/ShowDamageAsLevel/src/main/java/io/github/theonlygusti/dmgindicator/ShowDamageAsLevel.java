package io.github.theonlygusti.dmgindicator;

import org.bukkit.plugin.java.JavaPlugin;

public final class ShowDamageAsLevel extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new EntityDamageByEntityHandler(), this);
  }
}
