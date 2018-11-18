package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.ssapi.SuperSmashController;
import io.github.theonlygusti.ssapi.passive.Passive;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Querier implements CommandExecutor {
  private Plugin plugin;

  public Querier(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equals("ssc")) {
      if (args.length >= 1) {
        if (args[0].equals("passives")) {
          for (Passive passive : SuperSmashController.getRunningPassives()) {
            sender.sendMessage(passive.getName());
            sender.sendMessage(passive.getOwner().getPlayer().getName());
          }
        } else if (args[0].equals("huh")) {
          return false;
        } else {
          return false;
        }
      } else {
        return false;
      }
    }
    return false;
  }
}
