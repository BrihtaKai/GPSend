package driven.by.data.gpsend.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {

        List<String> res = new ArrayList<>();

        if (args.length == 1) { // First argument: "all", "player", or "reload"
            res.add("all");
            res.add("player");
            if (commandSender.hasPermission("gpsend.admin")) {
                res.add("reload");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("all")) { // Second argument for "all": amount
                res.add("<amount>");
            } else if (args[0].equalsIgnoreCase("player")) { // Second argument for "player": player name
                for (Player player : Bukkit.getOnlinePlayers()) {
                    res.add(player.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("player")) { // Third argument for "player": amount
            res.add("<amount>");
        }

        return res;
    }
}
