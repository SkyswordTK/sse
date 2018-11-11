package io.github.theonlygusti.supersmashkit;

import io.github.theonlygusti.supersmashkit.Plugin;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import org.bukkit.entity.Player;
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
        Player player = (Player) sender;
        MobDisguise disguise = new MobDisguise(DisguiseType.SKELETON);
        DisguiseAPI.disguiseToAll(player, disguise);

        return true;
      } else {
        sender.sendMessage("You must be a player to use this command.");

        return true;
      }
    }
    return false;
  }
}
