package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.ssapi.SuperSmashController;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class KitCompleter implements TabCompleter {
  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equals("kit")) {
      if (args.length == 1) {
        List<String> completions = new ArrayList<String>();

        StringUtil.copyPartialMatches(args[0], SuperSmashController.getRegisteredKitNames(), completions);

        return completions;
      } else {
        return null;
      }
    }
    return null;
  }
}
