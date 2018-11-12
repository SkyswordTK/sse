package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.doublejump.DoubleJump;
import io.github.theonlygusti.supersmashkit.SuperSmashKit;

import java.util.HashMap;
import java.util.function.Function;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;

import org.bukkit.entity.Player;

public class SuperSmashController {
  private static HashMap<String, Function<Player, SuperSmashKit>> kits = new HashMap<String, Function<Player, SuperSmashKit>>();
  private static HashMap<Player, SuperSmashKit> playerKits = new HashMap<Player, SuperSmashKit>();

  public static void registerKit(String id, Function<Player, SuperSmashKit> kitConstructor) {
    kits.put(id, kitConstructor);
  }

  public static void unregisterKit(String id) {
    kits.remove(id);
  }

  public static boolean exists(String kitId) {
    return kits.get(kitId) != null;
  }

  public static void enkit(Player player, String kitId) {
    Function<Player, SuperSmashKit> kitConstructor = kits.get(kitId);

    SuperSmashKit kit = kitConstructor.apply(player);

    Disguise disguise = kit.getDisguise();
    DisguiseAPI.disguiseToAll(player, disguise);
    DisguiseAPI.setViewDisguiseToggled(player, false);

    DoubleJump.set(player, kit);

    playerKits.put(player, kit);
  }

  public static void dekit(Player player) {
    DisguiseAPI.undisguiseToAll(player);
    DoubleJump.unset(player);
    playerKits.remove(player);
  }

  public static boolean isKitted(Player player) {
    return playerKits.get(player) != null;
  }
}
