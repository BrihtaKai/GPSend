package driven.by.data.gpsend.command;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.gui.AmountGUI;
import driven.by.data.gpsend.gui.ChoosingGUI;
import driven.by.data.gpsend.gui.PlayerListGUI;
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
            instance.getAliasManager().gprequestAliasRegister();
            sender.sendMessage("Config reloaded!");
            if (instance.getConfig().getInt("claimblocks_type") == 0) {
                Bukkit.getLogger().warning(
                        "You are using claimblock type 0 (TOTAL CLAIMBLOCKS) which is not recommended!"
                );
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            instance.getServer().getLogger()
                    .warning("[GPSend] Only players can use this command!");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("gpsend.send") && !player.isOp()) {
            MessageUtils.sendMessage(player, "errors.no_permission", true, null);
            return false;
        }

        if (args.length == 0) {
            new ChoosingGUI(player).open();
            return true;
        }

        String type = args[0].toLowerCase();

        switch (type) {
            case "player": {
                if (args.length == 1) {
                    new PlayerListGUI(player, (v, target) ->
                            new AmountGUI(v, target.getName(),
                                    (vv, amt) -> vv.performCommand("gpsend player " + target.getName() + " " + amt)
                            ).open()
                    ).open();
                    return true;
                }
                if (args.length == 2) {
                    String playerName = args[1];
                    new AmountGUI(player, playerName,
                            (v, amt) -> v.performCommand("gpsend player " + playerName + " " + amt)
                    ).open();
                    return true;
                }
                try {
                    String playerName = args[1];
                    int amount = Integer.parseInt(args[2]);
                    if (amount <= 0) {
                        MessageUtils.sendMessage(
                                player,
                                "errors.invalid_amount",
                                true,
                                null
                        );
                        return true;
                    }
                    SendingHandler.handleSending(
                            player,
                            playerName,
                            amount,
                            true
                    );
                } catch (NumberFormatException e) {
                    MessageUtils.sendMessage(
                            player,
                            "errors.invalid_amount",
                            true,
                            null
                    );
                }
                return true;
            }
            case "all": {
                if (!player.hasPermission("gpsend.sendall") && !player.isOp()) {
                    MessageUtils.sendMessage(
                            player,
                            "errors.no_permission",
                            true,
                            null
                    );
                    return false;
                }
                if (args.length == 1) {
                    new AmountGUI(
                            player,
                            instance.getConfig().getString("all_mode_name"),
                            (v, amt) -> v.performCommand("gpsend all " + amt)
                    ).open();
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[1]);
                    if (amount <= 0) {
                        MessageUtils.sendMessage(
                                player,
                                "errors.invalid_amount",
                                true,
                                null
                        );
                        return true;
                    }
                    SendingHandler.handleAllSending(
                            player,
                            amount
                    );
                } catch (NumberFormatException e) {
                    MessageUtils.sendMessage(
                            player,
                            "errors.invalid_amount",
                            true,
                            null
                    );
                }
                return true;
            }
            default: {
                MessageUtils.sendMessage(
                        player,
                        "send.invalid_target_type",
                        true,
                        null
                );
                return true;
            }
        }
    }
}