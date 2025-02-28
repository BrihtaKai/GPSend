package driven.by.data.gpsend.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        List<String> res = new ArrayList<>();

        if (args.length == 1) { // First argument: "all" or "player"
            res.add("all");
            res.add("player");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("all")) { // Second argument for "all": amount
                res.add("<non-negative-amount>");
                res.add("example: 5");
            } else if (args[0].equalsIgnoreCase("player")) { // Second argument for "player": player name
                for (Player player : Bukkit.getOnlinePlayers()) {
                    res.add(player.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("player")) { // Third argument for "player": amount
            res.add("<non-negative-amount>");
            res.add("example: 5");
        }

        return res;
    }
}
