package io.github.theonlygusti.supersmashkit.events;

import io.github.theonlygusti.supersmashkit.SuperSmashController;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class IllegalEvents implements Listener {
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
  public void cancelPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
    Player player = (Player) event.getPlayer();

    if (SuperSmashController.isKitted(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.HIGH)
  public void cancelPlayerFallDamage(EntityDamageEvent event) {
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
