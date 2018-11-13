package io.github.theonlygusti.kit;

import io.github.theonlygusti.ssapi.SuperSmashKit;
import io.github.theonlygusti.ssapi.item.ItemAbility;

import java.util.Arrays;
import java.util.List;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SkeletonKit implements SuperSmashKit {
  private Player player;
  private BoneExplosion boneExplosion;

  public SkeletonKit(Player player) {
    this.player = player;
    this.boneExplosion = new BoneExplosion(this);
  }

  public Player getPlayer() {
    return this.player;
  }

  private class BoneExplosion implements ItemAbility {
    private long lastTimeUsed = System.currentTimeMillis() - this.getCooldownTime();
    private SkeletonKit owner;

    public BoneExplosion(SkeletonKit owner) {
      this.owner = owner;
    }

    public String getTrigger() {
      return "Right-Click";
    }

    public String getName() {
      return "Bone Explosion";
    }

    public String getLore() {
      return "";
    }

    public SuperSmashKit getOwner() {
      return this.owner;
    }

    public Material getMaterial() {
      return Material.IRON_AXE;
    }

    public void punch() {
    }

    public void rightClick() {
      if (System.currentTimeMillis() - this.lastTimeUsed < this.getCooldownTime()) {
        this.owner.getPlayer().sendMessage("The skill is not cooled down yet");
      } else {
        this.owner.getPlayer().sendMessage("You used the skill");
        this.lastTimeUsed = System.currentTimeMillis();
      }
    }

    public void select() {
    }

    public void deselect() {
    }

    public long getCooldownTime() {
      return 10000L;
    }

    public long getLastTimeUsed() {
      return this.lastTimeUsed;
    }
  }

  public List<ItemAbility> getItemAbilities() {
    return Arrays.asList(this.boneExplosion);
  }

  public void doPunch() {
  }

  public ItemAbility getHeldItemAbility() {
    int slot = this.player.getInventory().getHeldItemSlot();
    if (slot < this.getItemAbilities().size()) {
      return this.getItemAbilities().get(slot);
    } else {
      return null;
    }
  }

  public void doRightClick() {
    ItemAbility heldItemAbility = this.getHeldItemAbility();

    if (heldItemAbility != null) {
      heldItemAbility.rightClick();
    }
  }

  public void changeHeldItem(int previousSlot, int newSlot) {
    if (previousSlot < this.getItemAbilities().size()) {
      this.getItemAbilities().get(previousSlot).deselect();
    }
    if (newSlot < this.getItemAbilities().size()) {
      this.getItemAbilities().get(newSlot).select();
    }
  }

  public Vector getDoubleJumpVelocity() {
    Location playerLocation = this.player.getLocation();

    float playerPitch = playerLocation.getPitch();
    float positivePlayerPitch = Math.abs(playerPitch);
    float newPitch = -90 + ((90 - positivePlayerPitch) * 0.6f);

    Location newPitchLocation = playerLocation.clone();
    newPitchLocation.setPitch(newPitch);
    Vector pseudoDirection = newPitchLocation.getDirection();
    Vector velocity = pseudoDirection.multiply(0.9);
    return velocity;
  }

  public void runDoubleJumpExtra() {
    for(Player p : Bukkit.getOnlinePlayers()) {
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
