package driven.by.data.gpsend.command;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.MessageUtils;
import driven.by.data.gpsend.utils.SendingHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GpsendCommand implements CommandExecutor {

    private final GPSend plugin = GPSend.getInstance();
    private final boolean placeholderAPIInstalled = plugin.placeholderAPIInstalled;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Allow reload from console
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player) && !(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
                return false;
            }
            plugin.reloadConfig();
            plugin.getAliasManager().gpsendAliasRegister();
            sender.sendMessage(MessageUtils.stringColorise("&#", "Config reloaded!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getServer().getLogger().warning("[GPSend] Only players can use this command!");
            return false;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("gpsend.send") && !player.isOp()) {
            if (placeholderAPIInstalled) {
                player.sendMessage(MessageUtils.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("no_permission"))));
            } else {
                player.sendMessage(MessageUtils.stringColorise("&#", plugin.getConfig().getString("no_permission")));
            }
            return false;
        }

        int argLength = args.length;
        if (argLength == 0) {
            plugin.getGuiManager().getChoosingGUI().open(player);
            return false;
        } else {
            String type = args[0].toLowerCase();
            switch (type) {
                case "player": {
                    if (argLength == 1) {
                        plugin.getGuiManager().getPlayerListGUI().open(player, 0);
                        return true;
                    }
                    if (argLength == 2) {
                        String playerName = args[1];
                        plugin.getGuiManager().getAmountGUI().open(player, playerName);
                        return true;
                    }
                    if (argLength >= 3) {
                        try {
                            String playerName = args[1];
                            int amount = Integer.parseInt(args[2]);
                            if (amount <= 0) {
                                // Negative amount is not allowed
                                if (placeholderAPIInstalled) {
                                    player.sendMessage(MessageUtils.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("invalid_amount"))));
                                } else {
                                    player.sendMessage(MessageUtils.stringColorise("&#", plugin.getConfig().getString("invalid_amount")));
                                }
                                return true;
                            }
                            SendingHandler.handleSending(player, playerName, amount, true);
                        } catch (NumberFormatException e) {
                            if (placeholderAPIInstalled) {
                                player.sendMessage(MessageUtils.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("invalid_amount"))));
                            } else {
                                player.sendMessage(MessageUtils.stringColorise("&#", plugin.getConfig().getString("invalid_amount")));
                            }
                        }
                        return true;
                    }
                    break;
                }
                case "all": {
                    if (!player.hasPermission("gpsend.sendall") && !player.isOp()) {
                        if (placeholderAPIInstalled) {
                            player.sendMessage(MessageUtils.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("no_permission"))));
                        } else {
                            player.sendMessage(MessageUtils.stringColorise("&#", plugin.getConfig().getString("no_permission")));
                        }
                        return false;
                    }

                    if (argLength == 1) {
                        plugin.getGuiManager().getAmountGUI().open(player, plugin.getConfig().getString("all_mode_name"));
                        return true;
                    }
                    if (argLength >= 2) {
                        try {
                            int amount = Integer.parseInt(args[1]);
                            if (amount <= 0) {
                                // Negative amount is not allowed
                                if (placeholderAPIInstalled) {
                                    player.sendMessage(MessageUtils.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("invalid_amount"))));
                                } else {
                                    player.sendMessage(MessageUtils.stringColorise("&#", plugin.getConfig().getString("invalid_amount")));
                                }
                                return true;
                            }
                            SendingHandler.handleAllSending(player, amount);
                        } catch (NumberFormatException e) {
                            if (placeholderAPIInstalled) {
                                player.sendMessage(MessageUtils.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("invalid_amount"))));
                            } else {
                                player.sendMessage(MessageUtils.stringColorise("&#", plugin.getConfig().getString("invalid_amount")));
                            }
                        }
                        return true;
                    }
                    break;
                }
                default: {
                    if (placeholderAPIInstalled) {
                        player.sendMessage(MessageUtils.stringColorise("&#", PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("invalid_type"))));
                    } else {
                        player.sendMessage(MessageUtils.stringColorise("&#", plugin.getConfig().getString("invalid_type")));
                    }
                    break;
                }
            }
        }

        return false;
    }
}
