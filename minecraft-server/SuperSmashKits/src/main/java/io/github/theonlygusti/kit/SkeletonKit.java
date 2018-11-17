package io.github.theonlygusti.kit;

import io.github.theonlygusti.ssapi.SuperSmashKit;
import io.github.theonlygusti.ssapi.item.ItemAbility;
import io.github.theonlygusti.ssapi.passive.Passive;
import io.github.theonlygusti.kit.item.OverchargeableBow;

import java.util.Arrays;
import java.util.List;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SkeletonKit implements SuperSmashKit {
  private Player player;
  private BoneExplosion boneExplosion;
  private RopedArrow ropedArrow;
  private Heal heal;

  public SkeletonKit(Player player) {
    this.player = player;
    this.boneExplosion = new BoneExplosion(this);
    this.ropedArrow = new RopedArrow(this);
    this.heal = new Heal(this);
  }

  public Player getPlayer() {
    return this.player;
  }

  private class RopedArrow extends OverchargeableBow {
    long lastTimeUsed = System.currentTimeMillis() - this.getCooldownTime();
    public RopedArrow(SkeletonKit owner) {
      super(owner);
    }

    public void afterShootArrow(Arrow arrow) {
    }

    public void select() {
    }

    public void punch() {
    }

    public SkeletonKit getOwner() {
      return (SkeletonKit) this.owner;
    }

    public String getName() {
      return "Roped Arrow";
    }

    public String getLore() {
      return "";
    }

    public long getCooldownTime() {
      return 5000L;
    }

    public long getLastTimeUsed() {
      return this.lastTimeUsed;
    }

    public long getTicksBetweenClicks() {
      return 7;
    }

    public int getMaximumOverchargeClicks() {
      return 5;
    }
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
    return Arrays.asList((ItemAbility) this.boneExplosion, (ItemAbility) this.ropedArrow);
  }

  private class Heal implements Passive {
    private SkeletonKit owner;

    public Heal(SkeletonKit owner) {
      this.owner = owner;
    }

    public BukkitRunnable getRunnable() {
      Heal passive = this;
      return new BukkitRunnable() {
        @Override
        public void run() {
          passive.getOwner().getPlayer().sendMessage("§sHealing hearts§r");
        }
      };
    }

    public String getName() {
      return "Heal";
    }

    public String getDescription() {
      return "Skeleton regeneration";
    }

    public Boolean shouldStart() {
      return true;
    }

    public SkeletonKit getOwner() {
      return owner;
    }

    public long getPeriod() {
      return 20L;
    }

    public void stop() {
    }
  }

  public List<Passive> getPassives() {
    return Arrays.asList(this.heal);
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
    MobDisguise skeletonDisguise = new MobDisguise(DisguiseType.SKELETON);
    return skeletonDisguise;
  }
}
