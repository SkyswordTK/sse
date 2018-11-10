package io.github.theonlygusti.liteperms;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

public final class LitePerms extends JavaPlugin {
  public static HashMap<Player, PermissionAttachment> attachments = new HashMap<Player, PermissionAttachment>();

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new PlayerJoinQuitHandlers(this), this);
    this.getCommand("allow").setExecutor(new Commander(this));
    this.getCommand("disallow").setExecutor(new Commander(this));
  }
}
