package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.supersmashkit.SuperSmashController;
import io.github.theonlygusti.supersmashkit.SuperSmashKit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class EventCapturers implements Listener {
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
  public void onPlayerSwitchItem(PlayerItemHeldEvent event) {
    Player player = event.getPlayer();

    if (SuperSmashController.isKitted(player)) {
    }
  }

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

  @EventHandler(priority=EventPriority.HIGH)
  public void cancelBlockDestroy(BlockBreakEvent event) {
    Player player = (Player) event.getPlayer();

    if (SuperSmashController.isKitted(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.HIGH)
  public void cancelPlayerDropItem(PlayerDropItemEvent event){
    Player player = event.getPlayer();

    if (SuperSmashController.isKitted(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.HIGH)
  public void cancelPlayerStompFarmland(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action action = event.getAction();

    if (SuperSmashController.isKitted(player)) {
      // Action.PHYSICAL means the player has jumped on the block
      if (action.equals(Action.PHYSICAL)) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() == Material.SOIL) {
          event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
          event.setCancelled(true);
          block.setTypeIdAndData(block.getType().getId(), block.getData(), true);
        }
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGH)
  public void cancelPlayerClickInventory(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player) {
      Player player = (Player) event.getWhoClicked();

      if (SuperSmashController.isKitted(player)) {
        event.setResult(Result.DENY);
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGH)
  public void onPlayerFall(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();

      if (SuperSmashController.isKitted(player)) {
        if (event.getCause() == DamageCause.FALL) {
          event.setCancelled(true);
        }
      }
    }
  }
}
