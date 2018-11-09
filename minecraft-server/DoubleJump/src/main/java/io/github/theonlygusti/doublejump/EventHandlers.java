package io.github.theonlygusti.doublejump;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class EventHandlers implements Listener {
  @EventHandler
  public void onFlightAttempt(PlayerToggleFlightEvent event) {
    event.setCancelled(true);
    Vector playerDirection = event.getPlayer().getLocation().getDirection();
    event.getPlayer().setVelocity(playerDirection.setY(Math.abs(playerDirection.getY())).multiply(1.3));
    for(Player p : Bukkit.getOnlinePlayers()){
      p.playSound(event.getPlayer().getLocation(), Sound.GHAST_FIREBALL, 1.0f, 1.0f);
    }
    event.getPlayer().setAllowFlight(false);
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (event.getPlayer().isOnGround()) {
      event.getPlayer().setAllowFlight(true);
    }
  }
}
