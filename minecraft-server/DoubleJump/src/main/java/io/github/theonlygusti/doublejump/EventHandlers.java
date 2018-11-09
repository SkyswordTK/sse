package io.github.theonlygusti.doublejump;

import io.github.theonlygusti.doublejump.DoubleJump;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class EventHandlers implements Listener {
  @EventHandler
  public void onFlightAttempt(PlayerToggleFlightEvent event) {
    DoubleJumper doubleJumper = DoubleJump.get(event.getPlayer());

    if (doubleJumper != null) {
      event.setCancelled(true);
      doubleJumper.beforeVelocity();
      doubleJumper.setVelocity();
      doubleJumper.afterVelocity();
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (event.getPlayer().isOnGround()) {
      DoubleJumper doubleJumper = DoubleJump.get(event.getPlayer());

      if (doubleJumper != null) {
        doubleJumper.touchGround();
      }
    }
  }
}
