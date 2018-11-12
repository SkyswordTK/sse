package io.github.theonlygusti.doublejump;

import org.bukkit.util.Vector;

public interface DoubleJumper {
  void landOnGround();
  Vector getDoubleJumpVelocity();
  void runDoubleJumpExtra();
}
