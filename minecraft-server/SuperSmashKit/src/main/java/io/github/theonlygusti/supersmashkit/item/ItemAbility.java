package io.github.theonlygusti.supersmashkit.item;

import io.github.theonlygusti.supersmashkit.SuperSmashKit;

import org.bukkit.Material;

public interface ItemAbility {
  public String getTrigger();
  public String getName();
  public String getLore();
  public SuperSmashKit getOwner();
  public Material getMaterial();
  public void punch();
  public void rightClick();
  /**
   * @return  The milliseconds.
   * @see     
   */
  public long getCooldownTime();
  public long getLastTimeUsed();
}

