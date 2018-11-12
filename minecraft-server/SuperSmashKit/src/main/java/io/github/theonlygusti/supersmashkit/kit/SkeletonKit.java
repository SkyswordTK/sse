package io.github.theonlygusti.supersmashkit.kit;

import io.github.theonlygusti.supersmashkit.Plugin;
import io.github.theonlygusti.supersmashkit.SuperSmashKit;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import org.bukkit.Location;
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
    // testing: horizontal double jump distance is about 6.334
    //          vertical double jump height is about 6.40872
    Location playerLocation = this.player.getLocation();

    float playerPitch = playerLocation.getPitch();
    float positivePlayerPitch = Math.abs(playerPitch);
    float newPitch = -90 + ((90 - positivePlayerPitch) * 0.6f);

    Location newPitchLocation = playerLocation.clone();
    newPitchLocation.setPitch(newPitch);

    Vector pseudoDirection = newPitchLocation.getDirection();

    Vector velocity = pseudoDirection.multiply(0.9);

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
