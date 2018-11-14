package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.ssapi.SuperSmashController;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commander implements CommandExecutor {
  public Commander() {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equals("kit")) {
      if (sender instanceof Player) {
        Player player = (Player) sender;
        if (args.length == 1) {
          if (!SuperSmashController.exists(args[0])) {
            sender.sendMessage("§cThat kit is not registered§r");
            return true;
          }

          SuperSmashController.enkit(player, args[0]);
          sender.sendMessage("§aYou have been given the §r§e§l" + args[0] + "§r §akit§r");

          return true;
        } else {
          return false;
        }
      } else {
        sender.sendMessage("You must be a player to use this command.");

        return true;
      }
    } else if (cmd.getName().equals("dekit")) {
      if (sender instanceof Player) {
        Player player = (Player) sender;
        if (args.length == 0) {
          if (!SuperSmashController.isKitted(player)) {
            sender.sendMessage("§cYou do not have a kit§r");
            return true;
          }

          SuperSmashController.dekit(player);
          sender.sendMessage("§6§lYou no longer have a kit§r");

          return true;
        } else {
          return false;
        }
      } else {
        sender.sendMessage("You must be a player to use this command.");

        return true;
      }
    }
    return false;
  }
}
