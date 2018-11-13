package io.github.theonlygusti.ssapi.events;

import io.github.theonlygusti.ssapi.SuperSmashController;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerEvents implements Listener {
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (SuperSmashController.isKitted(event.getPlayer())) {
      SuperSmashController.dekit(event.getPlayer());
    }
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent event) {
    if (SuperSmashController.isKitted(event.getPlayer())) {
      SuperSmashController.dekit(event.getPlayer());
    }
  }
}
