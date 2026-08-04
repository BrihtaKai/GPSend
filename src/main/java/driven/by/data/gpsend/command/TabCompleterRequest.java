package driven.by.data.gpsend.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TabCompleterRequest implements org.bukkit.command.TabCompleter{

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            return Arrays.asList("new", "accept", "deny");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("new")) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .toList();
        }

        return List.of();
    }
}
