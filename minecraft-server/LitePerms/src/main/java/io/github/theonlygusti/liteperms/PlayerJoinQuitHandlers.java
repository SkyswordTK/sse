package io.github.theonlygusti.liteperms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitHandlers implements Listener {
  private LitePerms plugin;

  public PlayerJoinQuitHandlers(LitePerms plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    LitePerms.attachments.put(event.getPlayer(), event.getPlayer().addAttachment(this.plugin));
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    event.getPlayer().removeAttachment(LitePerms.attachments.get(event.getPlayer()));
    LitePerms.attachments.remove(event.getPlayer());
  }
}
