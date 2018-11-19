package io.github.theonlygusti.effect;

import java.util.HashMap;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ItemExplosionHandler implements Listener {
  private HashMap<Item, BukkitTask> items = new HashMap<Item, BukkitTask>();
  private Plugin plugin;

  public ItemExplosionHandler(Plugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onItemExplosion(ItemExplosionEvent event) {
    for (int i = 0 ; i < event.getParticles() ; i++) {
      Item item = event.getLocation().getWorld().dropItem(event.getLocation(), event.getItemStack());
      item.setVelocity(new Vector((Math.random() - 0.5)*event.getVelocityMultiplier(),Math.random()*event.getVelocityMultiplier(),(Math.random() - 0.5)*event.getVelocityMultiplier()));

      item.setPickupDelay(999999);

      items.put(item, new BukkitRunnable () {
        @Override
        public void run() {
          items.remove(item);
          item.remove();
        }
      }.runTaskLater(this.plugin, event.getKeepAlive()));
    }

    event.getPlayableSound().playFromLocation(event.getLocation());
  }

  @EventHandler
  public void onEntityPickupItem(EntityPickupItemEvent event)
  {
    if (items.containsKey(event.getItem()))
      event.setCancelled(true);
  }

  @EventHandler
  public void onHopperPickupItem(InventoryPickupItemEvent event)
  {
    if (items.containsKey(event.getItem()))
      event.setCancelled(true);
  }
}
