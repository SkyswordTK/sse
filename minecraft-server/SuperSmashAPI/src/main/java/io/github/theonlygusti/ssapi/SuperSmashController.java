package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.doublejump.DoubleJump;
import io.github.theonlygusti.ssapi.SuperSmashKit;
import io.github.theonlygusti.ssapi.item.ItemAbility;
import io.github.theonlygusti.ssapi.passive.Passive;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import org.javatuples.Pair;

public class SuperSmashController {
  private static HashMap<String, Function<Player, SuperSmashKit>> kits = new HashMap<String, Function<Player, SuperSmashKit>>();
  private static HashMap<Player, SuperSmashKit> playerKits = new HashMap<Player, SuperSmashKit>();
  private static HashMap<Player, Pair<ItemStack[], ItemStack[]>> playerInventories = new HashMap<Player, Pair<ItemStack[], ItemStack[]>>();
  private static HashMap<Passive, Boolean> wasPassiveStarted = new HashMap<Passive, Boolean>();
  private static HashMap<Passive, BukkitTask> passiveTasks = new HashMap<Passive, BukkitTask>();

  public static void registerKit(String id, Function<Player, SuperSmashKit> kitConstructor) {
    kits.put(id, kitConstructor);
  }

  public static void unregisterKit(String id) {
    kits.remove(id);
  }

  public static boolean exists(String kitId) {
    return kits.get(kitId) != null;
  }

  public static List<String> getRegisteredKitNames() {
    return new ArrayList<String>(kits.keySet());
  }

  public static List<SuperSmashKit> getPlayerKits() {
    return new ArrayList<SuperSmashKit>(playerKits.values());
  }

  public static void enkit(Player player, String kitId) {
    if (isKitted(player)) {
      dekit(player);
    }
    Function<Player, SuperSmashKit> kitConstructor = kits.get(kitId);
    SuperSmashKit kit = kitConstructor.apply(player);
    Disguise disguise = kit.getDisguise().setKeepDisguiseOnPlayerDeath(true);
    DisguiseAPI.disguiseToAll(player, disguise);
    DisguiseAPI.setViewDisguiseToggled(player, false);
    List<ItemAbility> itemAbilities = kit.getItemAbilities();
    // store the player inventory so it can be restored when they are dekitted
    playerInventories.put(player, Pair.with(player.getInventory().getContents(), player.getInventory().getArmorContents()));
    player.getInventory().clear();
    for (int i = 0; i < itemAbilities.size(); i++) {
      ItemAbility itemAbility = itemAbilities.get(i);
      ItemStack itemStack = new ItemStack(itemAbility.getMaterial());
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setUnbreakable(true);
      itemMeta.setDisplayName("§e§l" + itemAbility.getTrigger() + "§r §f-§r " + "§a§l" + itemAbility.getName() + "§r");
      itemMeta.setLore(Arrays.asList(itemAbility.getLore()));
      itemStack.setItemMeta(itemMeta);
      player.getInventory().setItem(i, itemStack);
    }
    DoubleJump.set(player, kit);
    playerKits.put(player, kit);
  }

  public static void dekit(Player player) {
    DisguiseAPI.undisguiseToAll(player);
    player.getInventory().clear();
    Pair<ItemStack[], ItemStack[]> playerInventory = playerInventories.get(player);
    player.getInventory().setContents(playerInventory.getValue0());
    player.getInventory().setArmorContents(playerInventory.getValue1());
    playerInventories.remove(player);
    for (Passive passive : get(player).getPassives()) {
      passive.stop();
      passiveTasks.get(passive).cancel();
      passiveTasks.remove(passive);
      wasPassiveStarted.remove(passive);
    }
    DoubleJump.unset(player);
    playerKits.remove(player);
  }

  public static boolean isKitted(Player player) {
    return playerKits.get(player) != null;
  }

  public static SuperSmashKit get(Player player) {
    return playerKits.get(player);
  }

  public static Boolean getWasPassiveStarted(Passive passive) {
    Boolean wasStarted = wasPassiveStarted.get(passive);

    if (wasStarted == null) {
      SuperSmashController.wasPassiveStarted.put(passive, false);
      wasStarted = false;
    }

    return wasStarted;
  }

  public static Plugin getPlugin() {
    return Plugin.plugin;
  }

  public static void startPassive(Passive passive, Plugin plugin) {
    passiveTasks.put(passive, passive.getRunnable().runTaskTimer(plugin, 0L, passive.getPeriod()));
    wasPassiveStarted.put(passive, true);
  }

  public static void toggleWasPassiveStarted(Passive passive) {
    wasPassiveStarted.put(passive, !getWasPassiveStarted(passive));
  }
}
