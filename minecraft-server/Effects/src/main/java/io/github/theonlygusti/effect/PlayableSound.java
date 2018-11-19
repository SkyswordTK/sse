package io.github.theonlygusti.effect;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayableSound {
  public Sound sound;
  public float volume;
  public float pitch;

  /**
   * Builds a playable sound.
   *
   * @param sound the bukkit sound that will be played.
   * @param volume the volume at which the sound will be played.
   * @param pitch the pitch at which the sound will be played.
   */
  public PlayableSound(Sound sound, float volume, float pitch) {
    this.sound = sound;
    this.volume = volume;
    this.pitch = pitch;
  }

  public void playFromPlayer(Player player) {
    player.getWorld().playSound(player.getLocation(), this.sound, this.volume, this.pitch);
  }

  public void playToPlayer(Player player) {
    player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
  }
}
