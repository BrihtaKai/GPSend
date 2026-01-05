package driven.by.data.gpsend.command;

import driven.by.data.gpsend.utils.ColorFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Gprequest extends CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            plugin.getServer().getLogger().warning("[GPSend] Only players can use this command!");
            return false;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("gpsend.request") && !player.isOp()) {
            if (placeholderAPIInstalled) {
                player.sendMessage(ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("no_permission"))));
            } else {
                player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("no_permission")));
            }
            return false;
        }

        if (args[0] != null && args[1] != null) {
            if (placeholderAPIInstalled) {
                player.sendMessage(ColorFormat.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("request_args_empty"))));
            } else {
                player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("request_args_empty")));
            }
        }

        Player target = Bukkit.getServer().



        return false;
    }

}
