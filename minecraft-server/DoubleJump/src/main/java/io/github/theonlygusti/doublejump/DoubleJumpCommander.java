package io.github.theonlygusti.doublejump;

import io.github.theonlygusti.doublejump.DoubleJump;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoubleJumpCommander implements CommandExecutor {
  private final DoubleJump plugin;

  public DoubleJumpCommander(DoubleJump plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equals("doublejump")) {
      if (args.length == 1) {
          Player player = this.plugin.getServer().getPlayerExact(args[0]);

          if (player == null) {
            sender.sendMessage("§cThe player " + args[0] + " was not found online.§r");
            return false;
          }

          if (DoubleJump.get(player) == null) {
            DoubleJump.set(player, new ClassicDoubleJumper(player));
            player.sendMessage("§a§oYou can now double jump!§r");
          } else {
            DoubleJump.unset(player);
            player.sendMessage("§c§oYou can no longer double jump.§r");
          }
          return true;
      } else if (args.length == 0) {
        if (sender instanceof Player) {
          Player player = (Player) sender;

          if (DoubleJump.get(player) == null) {
            DoubleJump.set(player, new ClassicDoubleJumper(player));
            player.sendMessage("§a§oYou can now double jump!§r");
          } else {
            DoubleJump.unset(player);
            player.sendMessage("§c§oYou can no longer double jump.§r");
          }
          return true;
        } else {
          sender.sendMessage("§cThis command can only be used on players. Maybe you are looking for§r\n    §c§ldoublejump <player>§r");
          return false;
        }
      } else {
        sender.sendMessage("§cUsage: /doublejump [player]§r");
        return false;
      }
    }
    return false;
  }
}
