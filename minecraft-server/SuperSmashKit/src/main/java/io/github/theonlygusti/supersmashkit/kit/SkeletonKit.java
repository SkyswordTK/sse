package io.github.theonlygusti.supersmashkit.kit;

import io.github.theonlygusti.supersmashkit.Plugin;
import io.github.theonlygusti.supersmashkit.SuperSmashKit;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SkeletonKit implements SuperSmashKit {
  private Player player;
  private Plugin plugin;

  public SkeletonKit(Player player, Plugin plugin) {
    this.player = player;
    this.plugin = plugin;
  }

  public void doubleJump() {
    Vector playerDirection = this.player.getLocation().getDirection();
    Vector velocity = playerDirection.setY(Math.abs(playerDirection.getY() / 2) + 0.5).multiply(1.3);

    this.player.setVelocity(velocity);

    for(Player p : this.plugin.getServer().getOnlinePlayers()){
      p.playSound(this.player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
    }

    this.player.setAllowFlight(false);
  }

  public void landOnGround() {
    this.player.setAllowFlight(true);
  }

  public Disguise getDisguise() {
    MobDisguise skeletonDisguise = new MobDisguise(DisguiseType.SKELETON).setKeepDisguiseOnPlayerDeath(true);
    return skeletonDisguise;
  }
}
