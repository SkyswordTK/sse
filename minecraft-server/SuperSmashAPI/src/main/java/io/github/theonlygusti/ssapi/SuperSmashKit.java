package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.doublejump.DoubleJumper;
import io.github.theonlygusti.ssapi.item.ItemAbility;
import io.github.theonlygusti.ssapi.passive.Passive;

import java.util.List;

import me.libraryaddict.disguise.disguisetypes.Disguise;

import org.bukkit.entity.Player;

public interface SuperSmashKit extends DoubleJumper {
  public Disguise getDisguise();
  public List<ItemAbility> getItemAbilities();
  public Player getPlayer();
  public void doPunch();
  public void doRightClick();
  public void changeHeldItem(int previousSlot, int newSlot);
  public ItemAbility getHeldItemAbility();
  public List<Passive> getPassives();
}
