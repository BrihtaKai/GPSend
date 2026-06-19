package driven.by.data.gpsend.command;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.MessageUtils;
import driven.by.data.gpsend.utils.SendingHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GpsendCommand implements CommandExecutor {

    private final GPSend instance = GPSend.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Allow reload from console
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player)) {
                instance.reloadConfig();
                return false;
            }
            if (!sender.hasPermission("gpsend.admin")) {
                return false;
            }

            instance.reloadConfig();
            instance.getAliasManager().gpsendAliasRegister();
            sender.sendMessage("Config reloaded!");
            if (instance.getConfig().getInt("claimblocks_type") == 0) {
                Bukkit.getLogger().warning("You are using claimblock type 0 (TOTAL CLAIMBLOCKS) which is not recommended!");
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            instance.getServer().getLogger().warning("[GPSend] Only players can use this command!");
            return false;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("gpsend.send") && !player.isOp()) {
            MessageUtils.sendMessage(player, "no_permission", true, null);
            return false;
        }

        int argLength = args.length;
        if (argLength == 0) {
            instance.getGuiManager().getChoosingGUI().open(player);
            return false;
        } else {
            String type = args[0].toLowerCase();
            switch (type) {
                case "player": {
                    if (argLength == 1) {
                        instance.getGuiManager().getPlayerListGUI().open(player, 0);
                        return true;
                    }
                    if (argLength == 2) {
                        String playerName = args[1];
                        instance.getGuiManager().getAmountGUI().open(player, playerName);
                        return true;
                    }
                    if (argLength >= 3) {
                        try {
                            String playerName = args[1];
                            int amount = Integer.parseInt(args[2]);
                            if (amount <= 0) {
                                // Negative amount is not allowed
                                MessageUtils.sendMessage(player, "invalid_amount", true, null);
                                return true;
                            }
                            SendingHandler.handleSending(player, playerName, amount, true);
                        } catch (NumberFormatException e) {
                            MessageUtils.sendMessage(player, "invalid_amount", true, null);
                        }
                        return true;
                    }
                    break;
                }
                case "all": {
                    if (!player.hasPermission("gpsend.sendall") && !player.isOp()) {
                        MessageUtils.sendMessage(player, "no_permission", true, null);
                        return false;
                    }

                    if (argLength == 1) {
                        instance.getGuiManager().getAmountGUI().open(player, instance.getConfig().getString("all_mode_name"));
                        return true;
                    }
                    if (argLength >= 2) {
                        try {
                            int amount = Integer.parseInt(args[1]);
                            if (amount <= 0) {
                                // Negative amount is not allowed
                                MessageUtils.sendMessage(player, "invalid_amount", true, null);
                                return true;
                            }
                            SendingHandler.handleAllSending(player, amount);
                        } catch (NumberFormatException e) {
                            MessageUtils.sendMessage(player, "invalid_amount", true, null);
                        }
                        return true;
                    }
                    break;
                }
                default: {
                    MessageUtils.sendMessage(player, "invalid_type", true, null);
                    break;
                }
            }
        }

        return false;
    }
}
