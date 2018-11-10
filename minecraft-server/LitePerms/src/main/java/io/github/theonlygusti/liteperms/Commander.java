package io.github.theonlygusti.liteperms;

import io.github.theonlygusti.liteperms.LitePerms;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commander implements CommandExecutor {
  private final LitePerms plugin;

  public Commander(LitePerms plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equals("allow")) {
      if (args.length == 2) {
        Player player = this.plugin.getServer().getPlayerExact(args[0]);

        if (player == null) {
          return false;
        }

        PermissionAttachment attachment = LitePerms.attachments.get(player);
        attachment.setPermission(args[1], true);

        sender.sendMessage("Successfully given the permission " + args[1] + " to " + args[0]);
        return true;
      } else {
        return false;
      }
    } else if (cmd.getName().equals("disallow")) {
      if (args.length == 2) {
        Player player = this.plugin.getServer().getPlayerExact(args[0]);

        if (player == null) {
          return false;
        }

        PermissionAttachment attachment = LitePerms.attachments.get(player);
        attachment.setPermission(args[1], false);

        sender.sendMessage("Successfully taken the permission " + args[1] + " from " + args[0]);
        return true;
      } else {
        return false;
      }

    }
    return false;
  }
}
