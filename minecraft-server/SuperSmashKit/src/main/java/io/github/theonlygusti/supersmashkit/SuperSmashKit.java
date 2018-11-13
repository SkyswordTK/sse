package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.doublejump.DoubleJumper;
import io.github.theonlygusti.supersmashkit.item.ItemAbility;

import java.util.List;

import me.libraryaddict.disguise.disguisetypes.Disguise;

import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;

public interface SuperSmashKit extends DoubleJumper {
  public Disguise getDisguise();
  public List<ItemAbility> getItemAbilities();
  public Player getPlayer();
  public void doPunch();
  public void doRightClick();
  public void changeHeldItem(int previousSlot, int newSlot);
  public ItemAbility getHeldItemAbility();
  //public List<PassiveAbility> getPassives();
  //public ItemStack[] getArmorContents();
}
