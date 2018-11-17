package io.github.theonlygusti.ssapi.item;

import org.bukkit.entity.Arrow;

public interface BowAbility extends ItemAbility {
  public void onShootArrow(Arrow arrow);
}

