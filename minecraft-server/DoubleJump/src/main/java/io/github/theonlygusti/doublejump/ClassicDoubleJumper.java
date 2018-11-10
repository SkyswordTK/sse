package io.github.theonlygusti.doublejump;

import io.github.theonlygusti.doublejump.DoubleJumper;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ClassicDoubleJumper implements DoubleJumper {
  private Player player;

  public ClassicDoubleJumper(Player player) {
    this.player = player;
  }

  public void landOnGround() {
    this.player.setAllowFlight(true);
  }

  public void setVelocity() {
    this.player.setVelocity(this.player.getLocation().getDirection());
  }

  public void afterVelocity() {
    this.player.setAllowFlight(false);
    for(Player p : Bukkit.getOnlinePlayers()){
      p.playSound(this.player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);
    }
  }
}
