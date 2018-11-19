package io.github.theonlygusti.ssapi.item;

import io.github.theonlygusti.ssapi.SuperSmashKit;

import org.bukkit.Material;
import org.bukkit.event.Listener;

public interface ItemAbility extends Listener {
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
  public void select();
  public void deselect();
}

