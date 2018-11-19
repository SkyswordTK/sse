package io.github.theonlygusti.kit;

import io.github.theonlygusti.effect.ItemExplosionEvent;
import io.github.theonlygusti.effect.PlayableSound;
import io.github.theonlygusti.ssapi.SuperSmashController;
import io.github.theonlygusti.ssapi.SuperSmashKit;
import io.github.theonlygusti.ssapi.item.ItemAbility;
import io.github.theonlygusti.ssapi.passive.Passive;
import io.github.theonlygusti.kit.item.OverchargeableBow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SkeletonKit implements SuperSmashKit {
  private Player player;
  private BoneExplosion boneExplosion;
  private RopedArrow ropedArrow;
  private Barrage barrage;
  private ReplenishArrows replenishArrows;
  private Heal heal;

  public SkeletonKit(Player player) {
    this.player = player;
    this.boneExplosion = new BoneExplosion(this);
    this.ropedArrow = new RopedArrow(this);
    this.heal = new Heal(this);
    this.barrage = new Barrage(this);
    this.replenishArrows = new ReplenishArrows(this);
  }

  public Player getPlayer() {
    return this.player;
  }

  private class RopedArrow extends OverchargeableBow {
    long lastTimeUsed = System.currentTimeMillis() - this.getCooldownTime();
    private HashSet<Arrow> arrows = new HashSet<Arrow>();

    public RopedArrow(SkeletonKit owner) {
      super(owner);
    }

    public void afterShootArrow(Arrow arrow) {
      this.getOwner().barrage.doBarrage(arrow, this.overchargeClicks);
      this.overchargeClicks = 0;
    }

    public void select() {
    }

    public void punch() {
      if (System.currentTimeMillis() - this.lastTimeUsed >= this.getCooldownTime()) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setVelocity(player.getLocation().getDirection().multiply(2.4));
        this.arrows.add(arrow);
        this.lastTimeUsed = System.currentTimeMillis();
      }
    }

    @EventHandler
    public void onRopedArrowLand(ProjectileHitEvent event) {
      if (event.getEntity() instanceof Arrow) {
        Arrow arrow = (Arrow) event.getEntity();
        if (arrows.remove(arrow)) {
          Player shooter = this.getOwner().getPlayer();
          Vector direction = arrow.getLocation().toVector().subtract(shooter.getLocation().toVector()).normalize();
          double mult = 0.4 + arrow.getVelocity().length() / 3d;
          Vector trajectory = direction.multiply(mult);
          trajectory.setY(trajectory.getY() + 0.6 * mult);
          if (trajectory.getY() > 1.2 * mult)
            trajectory.setY(1.2 * mult);
          if (shooter.isOnGround())
            trajectory.setY(trajectory.getY() + 0.2);
          shooter.setVelocity(trajectory);
          arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_ARROW_HIT, 2.5f, 0.5f);
        }
      }
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
    private final double range = 10;
    private final double knockbackMultiplier = 2.5;
    private final PlayableSound sound = new PlayableSound(Sound.ENTITY_SKELETON_HURT, 2f, 1.2f);
    private final int numberOfBonesToSpawn = 48;
    private final ItemStack boneItemStack = new ItemStack(Material.BONE);
    private final long boneLifespan = 40L;

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
      Player player = this.getOwner().getPlayer();
      if (System.currentTimeMillis() - this.lastTimeUsed >= this.getCooldownTime()) {
        for(Player other : this.getOwner().getPlayer().getWorld().getPlayers()){
          if (!player.equals(other) && SuperSmashController.isKitted(other)) {
            double distance = player.getLocation().distance(other.getLocation());
            if (distance < range) {
              // magic number galore, will fix
              double damage = 6 * (1 - (distance / range));
              double knockback = damage < 2 ? 2 : damage;
              knockback = Math.log10(knockback) * knockbackMultiplier;
              Vector trajectory = other.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
              trajectory.multiply(0.6 * knockback);
              double speed = 0.2 + trajectory.length() * 0.8;
              trajectory.normalize().multiply(speed).setY(Math.abs(0.2 * knockback));
              if (trajectory.getY() > 0.4 + (0.04 * knockback)) {
                trajectory.setY(0.4 + (0.04 * knockback));
              }
              if (other.isOnGround()) {
                trajectory.setY(trajectory.getY() + 0.2);
              }
              other.setVelocity(trajectory);
            }
          }
        }
        (new ItemExplosionEvent(this.getOwner().getPlayer().getLocation().add(0, 0.5, 0),
                                numberOfBonesToSpawn, 0.8, this.sound, this.boneItemStack,
                                this.boneLifespan)).callEvent();
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

  private class Barrage implements Passive {
    private SkeletonKit owner;
    private int numberOfExtraArrows;
    private Arrow tracer;
    private Boolean shouldStart = false;

    public Barrage(SkeletonKit owner) {
      this.owner = owner;
    }

    public String getName() {
      return "Barrage";
    }

    public String getDescription() {
      return "Charge the bow to fire a volley of arrows";
    }

    public void doBarrage(Arrow tracer, int numberOfExtraArrows) {
      this.numberOfExtraArrows = numberOfExtraArrows;
      this.tracer = tracer;
      this.shouldStart = true;
    }

    public void stop() {
    }

    public Long getPeriod() {
      return 1L;
    }

    public Boolean shouldStart() {
      return this.shouldStart;
    }

    public BukkitRunnable getRunnable() {
      this.shouldStart = false;
      Barrage instance = this;
      int[] arrowCounter = new int[] {0};
      Player player = instance.getOwner().getPlayer();
      return new BukkitRunnable() {
        @Override
        public void run() {
          if (arrowCounter[0] == instance.numberOfExtraArrows) {
            this.cancel();
            return;
          }
          double arrowSpeed = tracer.getVelocity().length();
          Vector random = new Vector((Math.random()-0.5)/10, (Math.random()-0.5)/10, (Math.random()-0.5)/10);
          Arrow arrow = (Arrow) player.launchProjectile(Arrow.class);
          arrow.setVelocity(player.getLocation().getDirection().add(random).multiply(3));
          player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
          arrowCounter[0]++;
        }
      };
    }

    public SkeletonKit getOwner() {
      return this.owner;
    }
  }

  public void shootBow(Arrow arrow) {
    this.ropedArrow.onShootArrow(arrow);
  }

  public List<ItemAbility> getItemAbilities() {
    return Arrays.asList((ItemAbility) this.boneExplosion, (ItemAbility) this.ropedArrow);
  }

  private class ReplenishArrows implements Passive {
    private SkeletonKit owner;
    private int slot = 2;
    private int maxArrows = 3;

    public ReplenishArrows(SkeletonKit owner) {
      this.owner = owner;
    }

    public SkeletonKit getOwner() {
      return this.owner;
    }

    public Long getPeriod() {
      return 60L;
    }

    public void stop() {
    }

    public String getName() {
      return "Quiver";
    }

    public String getDescription() {
      return "Skeleton gets arrows";
    }

    public BukkitRunnable getRunnable() {
      ReplenishArrows instance = this;
      return new BukkitRunnable() {
        @Override
        public void run() {
          if (instance.getOwner().getPlayer().isDead()) {
            this.cancel();
          } else {
            ItemStack currentSlotContents = instance.getOwner().getPlayer().getInventory().getItem(instance.slot);
            if (currentSlotContents == null || currentSlotContents.getAmount() < instance.maxArrows) {
              ItemStack newSlotContents;
              if (currentSlotContents == null) {
                newSlotContents = new ItemStack(Material.ARROW);
              } else {
                int currentAmount = currentSlotContents.getAmount();
                newSlotContents = new ItemStack(Material.ARROW, currentAmount + 1);
              }
              instance.getOwner().getPlayer().getInventory().setItem(instance.slot, newSlotContents);
              instance.getOwner().getPlayer().playSound(instance.getOwner().getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5F, 1.0F);
            }
          }
        }
      };
    }

    public Boolean shouldStart() {
      return !this.getOwner().getPlayer().isDead();
    }
  }

  private class Heal implements Passive {
    private SkeletonKit owner;
    private float healthPerSecond = 0.25f;

    public Heal(SkeletonKit owner) {
      this.owner = owner;
    }

    public BukkitRunnable getRunnable() {
      Heal passive = this;
      return new BukkitRunnable() {
        @Override
        public void run() {
          if (passive.getOwner().getPlayer().isDead()) {
            this.cancel();
          } else if (passive.getOwner().getPlayer().getHealth() < passive.getOwner().getPlayer().getMaxHealth() - passive.healthPerSecond) {
            passive.getOwner().getPlayer().setHealth(passive.getOwner().getPlayer().getHealth() + passive.healthPerSecond);
          } else {
            passive.getOwner().getPlayer().setHealth(passive.getOwner().getPlayer().getMaxHealth());
          }
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
      return !this.getOwner().getPlayer().isDead();
    }

    public SkeletonKit getOwner() {
      return owner;
    }

    public Long getPeriod() {
      return 20L;
    }

    public void stop() {
    }
  }

  public List<Passive> getPassives() {
    return Arrays.asList((Passive) this.heal,
                         (Passive) this.replenishArrows,
                         (Passive) this.barrage);
  }

  public void doPunch() {
    ItemAbility heldItemAbility = this.getHeldItemAbility();

    if (heldItemAbility != null) {
      heldItemAbility.punch();
    }
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
    this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);

    this.player.setAllowFlight(false);
  }

  public void landOnGround() {
    this.player.setAllowFlight(true);
  }

  public Disguise getDisguise() {
    MobDisguise skeletonDisguise = new MobDisguise(DisguiseType.SKELETON);
    skeletonDisguise.getWatcher().setArmor(new ItemStack[] {
      new ItemStack(Material.AIR),
      new ItemStack(Material.AIR),
      new ItemStack(Material.AIR),
      new ItemStack(Material.AIR)
    });
    return skeletonDisguise;
  }
}
