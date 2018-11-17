package io.github.theonlygusti.kit.item;

import io.github.theonlygusti.ssapi.SuperSmashKit;
import io.github.theonlygusti.ssapi.item.ItemAbility;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

public abstract class OverchargeableBow implements ItemAbility {
  protected BukkitTask overchargeTask;
  // MCP's decompiled 1.8.8 minecraft shows it takes 22 ticks to charge a bow.
  protected long ticksToFullyCharge = 22L;
  protected int overchargeClicks = 0;
  protected Player player;

  public OverchargeableBow(Player player) {
    this.player = player;
  }

  public abstract int getMaximumOverchargeClicks();

  public abstract long getTicksBetweenClicks();

  protected int countItems(Inventory inventory, Material item) {
    int count = 0;
    int size = inventory.getSize();
    for (int slot = 0; slot < size; slot++) {
      ItemStack is = inventory.getItem(slot);
      if (is == null) continue;
      if (is.getType() == item) {
        count += is.getAmount();
      }
    }
    return count;
  }

  protected void setPlayerExp(Player player, float xp) {
    if (xp < 0.0F) {
      player.setExp(0.0F);
    } else if (xp > 1.0F) {
      player.setExp(1.0F);
    } else {
      player.setExp(xp);
    }
  }

  @Override
  public void rightClick() {
    if (this.overchargeTask != null) {
      this.overchargeTask.cancel();
    }
    if (countItems(this.player.getInventory(), Material.ARROW) > 0) {
      final float experienceAddedPerTick = 1.0F / this.getMaximumOverchargeClicks();
      OverchargeableBow instance = this;
      // because the BukkitRunnable is only allowed to reference
      // effectively-final local variables.
      int[] clickCounter = new int[] { 0 };
      this.overchargeTask = new BukkitRunnable() {
        @Override
        public void run() {
          if (clickCounter[0] < instance.getMaximumOverchargeClicks()) {
            for(Player p : Bukkit.getOnlinePlayers()){
              p.playSound(instance.player.getLocation(), Sound.CLICK, 1.0F, 1.0F + instance.player.getExp());
            }
            instance.setPlayerExp(instance.player, instance.player.getExp() + experienceAddedPerTick);
            instance.overchargeClicks++;
            clickCounter[0]++;
          } else {
            this.cancel();
            return;
          }
        }
      }.runTaskTimer(JavaPlugin.getPlugin(this.class), this.ticksToFullyCharge, this.getTicksBetweenClicks());
    }
  }

  public void onShootArrow(Arrow arrow) {
    if (this.overchargeTask != null) {
      this.overchargeTask.cancel();
    }
    this.setPlayerExp(this.player, 0.0F);
    this.afterShootArrow(arrow);
    this.overchargeClicks = 0;
  }

  public abstract void afterShootArrow(Arrow arrow);

  public void deselect() {
    if (this.overchargeTask != null) {
      this.overchargeTask.cancel();
    }
    this.setPlayerExp(this.player, 0.0F);
    this.overchargeClicks = 0;
  }

  public String getTrigger() {
    return "Left-Click";
  }

  public abstract String getName();
  public abstract String getLore();
  public abstract SuperSmashKit getOwner();
  public Material getMaterial() {
    return Material.BOW;
  }
  public abstract void punch();
  public abstract long getCooldownTime();
  public abstract long getLastTimeUsed();
  public abstract void select();
}
