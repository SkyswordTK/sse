package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.supersmashkit.SuperSmashController;

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
        if (args.length != 1) {
          return false;
        }

        if (!SuperSmashController.exists(args[0])) {
          sender.sendMessage("§cThat kit is not registered§r");
          return true;
        }

        Player player = (Player) sender;

        SuperSmashController.enkit(player, args[0]);
        sender.sendMessage("�aYour kit has been applied.");

        return true;
      } else {
        sender.sendMessage("You must be a player to use this command.");

        return true;
      }
    } else if (cmd.getName().equals("dekit")) {
      if (sender instanceof Player) {
        if (args.length != 0) {
          return false;
        }

        Player player = (Player) sender;

        if (!SuperSmashController.isKitted(player)) {
          sender.sendMessage("§cYou do not have a kit§r");
          return true;
        }

        SuperSmashController.dekit(player);

        return true;
      } else {
        sender.sendMessage("You must be a player to use this command.");

        return true;
      }
    }
    return false;
  }
}
