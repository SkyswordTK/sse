package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.ssapi.SuperSmashController;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commander implements CommandExecutor {
  private Plugin plugin;

  public Commander(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equals("kit")) {
      if (args.length == 1) {
        if (sender instanceof Player) {
          Player player = (Player) sender;
          if (player.hasPermission("ssapi.kitcmd.self")) {
            if (!SuperSmashController.exists(args[0])) {
              player.sendMessage("§cThe §f§l"  + args[0] + "§r §ckit has not been registered§r");
              return true;
            }
            
            SuperSmashController.enkit(player, args[0]);
            player.sendMessage("§aYou have been given the §r§e§l" + args[0] + "§r §akit§r");
            return true;
          } else {
            player.sendMessage("§cYou do not have permission to give yourself a kit with this command§r");
            return true;
          }
        } else {
          sender.sendMessage("You must be a player to use this command.");

          return true;
        }
      } else if (args.length == 2) {
        if (sender.hasPermission("ssapi.kitcmd.other")) {
          if (!SuperSmashController.exists(args[0])) {
            sender.sendMessage("§cThe §f§l"  + args[0] + "§r §ckit has not been registered§r");
            return true;
          }

          Player target = this.plugin.getServer().getPlayerExact(args[1]);

          if (target == null) {
            sender.sendMessage("§cThe player with username " + args[1] + " could not be found on the server§r");
            return true;
          }
          
          SuperSmashController.enkit(target, args[0]);
          sender.sendMessage("§r§e§l" + args[1] + "§r §ahas been given the §r§e§l" + args[0] + "§r §akit§r");
          target.sendMessage("§aYou have been given the §r§e§l" + args[0] + "§r §akit§r");
          return true;
        } else {
          sender.sendMessage("§cYou do not have permission to give other players a kit with this command§r");
          return true;
        }
      } else {
        return false;
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
