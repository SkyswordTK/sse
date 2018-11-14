package io.github.theonlygusti.ssapi.passive;

import org.bukkit.scheduler.BukkitRunnable;

public interface Passive {
  public String getName();
  public String getDescription();
  /**
   * Should cancel itself.
   */
  public BukkitRunnable getRunnable();
  /**
   * This is a toggle.
   */
  public Boolean shouldStart();
}
