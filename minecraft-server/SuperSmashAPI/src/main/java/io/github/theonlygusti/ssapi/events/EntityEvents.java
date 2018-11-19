package io.github.theonlygusti.ssapi.events;

import io.github.theonlygusti.ssapi.SuperSmashController;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class EntityEvents implements Listener {
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

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (SuperSmashController.isKitted(event.getEntity())) {
      SuperSmashController.dekit(event.getEntity());
    }
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    if (event.getEntity().getShooter() instanceof Player) {
      Player shooter = (Player) event.getEntity().getShooter();
      if (SuperSmashController.isKitted(shooter)) {
        if (event.getEntity() instanceof Arrow) {
          ((Arrow) event.getEntity()).setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        }
      }
    }
  }
}
