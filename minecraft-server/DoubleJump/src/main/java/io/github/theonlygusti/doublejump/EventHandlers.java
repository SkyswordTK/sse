package io.github.theonlygusti.doublejump;

import io.github.theonlygusti.doublejump.DoubleJump;
import java.util.HashMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EventHandlers implements Listener {
  private HashMap<DoubleJumper, Boolean> hasLandedOnGround = new HashMap<DoubleJumper, Boolean>();
  private DoubleJump plugin;

  public EventHandlers(DoubleJump plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onFlightAttempt(PlayerToggleFlightEvent event) {
    DoubleJumper doubleJumper = DoubleJump.get(event.getPlayer());

    if (doubleJumper != null) {
      event.setCancelled(true);
      event.getPlayer().setVelocity(doubleJumper.getDoubleJumpVelocity());
      BukkitTask checkOffGround = new BukkitRunnable() {
        // prevent triple jump checking propelling the player forwards when they are underneath
        // a solid and stood on the ground.
        int counter = 0;

        @Override
        public void run() {
          if (counter > DoubleJump.checkOffGroundLimit) {
            this.cancel();
            return;
          }
          if (event.getPlayer().getLocation().getY() % 1 < DoubleJump.tripleJumpActivationHeight) {
            event.getPlayer().setVelocity(doubleJumper.getDoubleJumpVelocity());
            hasLandedOnGround.put(doubleJumper, false);
          } else {
            this.cancel();
          }
          counter++;
        }
      }.runTaskTimer(this.plugin, 0L, 1L);
      doubleJumper.runDoubleJumpExtra();
      hasLandedOnGround.put(doubleJumper, false);
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    DoubleJumper doubleJumper = DoubleJump.get(event.getPlayer());

    if (doubleJumper != null) {
      if (event.getPlayer().isOnGround()) {
        if (hasLandedOnGround.get(doubleJumper) == null || !hasLandedOnGround.get(doubleJumper)) {
          doubleJumper.landOnGround();
          hasLandedOnGround.put(doubleJumper, true);
        }
      }
    }
  }
}
