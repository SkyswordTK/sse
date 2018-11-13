package io.github.theonlygusti.kit;

import io.github.theonlygusti.kit.SkeletonKit;
import io.github.theonlygusti.ssapi.SuperSmashController;

import org.bukkit.plugin.java.JavaPlugin;

public final class KitLoader extends JavaPlugin {
  @Override
  public void onEnable() {
    SuperSmashController.registerKit("skeleton", SkeletonKit::new);
  }
}
