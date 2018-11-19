package io.github.theonlygusti.effect;

import io.github.theonlygusti.effect.PlayableSound;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemExplosionEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  private boolean cancelled = false;

  private Location location;
  private int particles;
  private double velocityMultiplier;
  private PlayableSound playableSound;
  private ItemStack itemStack;
  private long keepAlive;

  public ItemExplosionEvent(Location location, int particles, double velocityMultiplier, PlayableSound playableSound, ItemStack itemStack, long keepAlive) {
    this.location = location;
    this.particles = particles;
    this.velocityMultiplier = velocityMultiplier;
    this.playableSound = playableSound;
    this.itemStack = itemStack;
    this.keepAlive = keepAlive;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public Location getLocation() {
    return this.location;
  }

  public int getParticles() {
    return this.particles;
  }

  public double getVelocityMultiplier() {
    return this.velocityMultiplier;
  }

  public PlayableSound getPlayableSound() {
    return this.playableSound;
  }

  public ItemStack getItemStack() {
    return this.itemStack;
  }

  public long getKeepAlive() {
    return this.keepAlive;
  }

  public void setItemStack(ItemStack itemStack) {
    this.itemStack = itemStack;
  }
}
