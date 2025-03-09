package driven.by.data.gpsend.command;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GpsendAdmin extends TabCompleter implements CommandExecutor {

    private final GPSend instance = GPSend.getInstance();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("gpsend.admin") && !player.isOp()) {
                player.sendMessage(ColorFormat.stringColorise("&#", GPSend.getInstance().getConfig().getString("no_permission")));
                return false;
            }
        }

        if (strings.length == 0) {
            return false;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            GPSend.getInstance().reloadConfig();
            instance.getAliasManager().gpsendAliasRegister();
            sender.sendMessage(ColorFormat.stringColorise("&#", "Config reloaded!"));
        }

        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        List<String> res = new ArrayList<>();

        if (args.length == 1 && commandSender.hasPermission("gpsend.admin")) {
            res.add("reload");
        }

        return res;
    }
}
