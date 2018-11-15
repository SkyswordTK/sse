package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.ssapi.SuperSmashController;

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
    }
    return false;
  }
}
