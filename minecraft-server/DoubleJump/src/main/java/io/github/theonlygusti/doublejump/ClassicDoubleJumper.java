package io.github.theonlygusti.doublejump;

import io.github.theonlygusti.doublejump.DoubleJumper;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ClassicDoubleJumper implements DoubleJumper {
  private Player player;

  public ClassicDoubleJumper(Player player) {
    this.player = player;
  }

  public void landOnGround() {
    this.player.setAllowFlight(true);
  }

  public Vector getDoubleJumpVelocity() {
    return this.player.getLocation().getDirection();
  }

  public void runDoubleJumpExtra() {
    this.player.setAllowFlight(false);
    this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);
  }
}
