package io.github.theonlygusti.ssapi.events;

import io.github.theonlygusti.ssapi.SuperSmashController;
import io.github.theonlygusti.ssapi.SuperSmashKit;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class KitEvents implements Listener {
  @EventHandler
  public void passPlayerPunch(PlayerAnimationEvent event) {
    Player player = event.getPlayer();

    if (SuperSmashController.isKitted(player)) {
      if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
        SuperSmashKit kit = SuperSmashController.get(player);
        kit.doPunch();
      }
    }
  }

  @EventHandler
  public void passPlayerRightClick(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (SuperSmashController.isKitted(player)) {
      Action action = event.getAction();

      // issue: event.getAction() is never RIGHT_CLICK_AIR if player's hand is empty
      if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
        SuperSmashKit kit = SuperSmashController.get(player);
        kit.doRightClick();
      }
    }
  }

  @EventHandler
  public void passPlayerShootBow(EntityShootBowEvent event){
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();

      if (SuperSmashController.isKitted(player)) {
        if (event.getProjectile() instanceof Arrow) {
          SuperSmashController.get(player).shootBow((Arrow) event.getProjectile());
        }
      }
    }
  }

  @EventHandler
  public void passPlayerDropItem(PlayerDropItemEvent event){
    Player player = event.getPlayer();

    if (SuperSmashController.isKitted(player)) {
    }
  }

  @EventHandler
  public void passPlayerSwitchItem(PlayerItemHeldEvent event) {
    Player player = event.getPlayer();

    if (SuperSmashController.isKitted(player)) {
      event.getPlayer().sendActionBar("§r");
      SuperSmashKit kit = SuperSmashController.get(player);
      kit.changeHeldItem(event.getPreviousSlot(), event.getNewSlot());
    }
  }
}
