package io.github.theonlygusti.dmgindicator;

import org.bukkit.entity.Arrow;
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
    if (event.getDamager() instanceof Player) {
      // it only makes sense for players to be shown the damage of their last attack
      Player player = (Player) event.getDamager();
      int damage = (int) event.getDamage();

      player.setLevel(damage);
    } else if (event.getDamager() instanceof Arrow) {
      // if a player damages another player with an arrow, event.getDamager is the arrow entity,
      // but we still want the shooter to know how much damage their arrow dealt
      Arrow arrow = (Arrow) event.getDamager();
      if (arrow.getShooter() instanceof Player) {
        Player player = (Player) arrow.getShooter();
        int damage = (int) event.getDamage();

        player.setLevel(damage);
      }
    }
  }
}
