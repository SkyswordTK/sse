package io.github.theonlygusti.dmgindicator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityHandler implements Listener {
  // other plugins will use EntityDamageByEntityEvent to set custom damages,
  // this plugin should have lowest priority to make sure it shows the actual
  // damage dealt to the player (after other plugins have set it).
  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    // it only makes sense for players to be shown the damage of their last attack
    if (event.getDamager() instanceof Player) {
      Player player = (Player) event.getDamager();
      int damage = (int) event.getDamage();

      player.setLevel(damage);
    }
  }
}
