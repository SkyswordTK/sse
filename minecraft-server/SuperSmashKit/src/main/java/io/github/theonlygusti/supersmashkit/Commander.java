package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.doublejump.DoubleJump;
import io.github.theonlygusti.supersmashkit.Plugin;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commander implements CommandExecutor {
  private final Plugin plugin;

  public Commander(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equals("kit")) {
      if (sender instanceof Player) {
        if (args.length != 1) {
          return false;
        }

        Player player = (Player) sender;
        BiFunction<Player, Plugin, SuperSmashKit> kitConstructor = Plugin.kits.get(args[0]);

        if (kitConstructor == null) {
          sender.sendMessage("§cThat kit is not registered§r");
          return true;
        }

        SuperSmashKit kit = kitConstructor.apply(player, this.plugin);

        Disguise disguise = kit.getDisguise();
        DisguiseAPI.disguiseToAll(player, disguise);
        DisguiseAPI.setViewDisguiseToggled(player, false);

        DoubleJump.set(player, kit);

        return true;
      } else {
        sender.sendMessage("You must be a player to use this command.");

        return true;
      }
    }
    return false;
  }
}
