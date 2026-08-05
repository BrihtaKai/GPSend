package driven.by.data.gpsend.command;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.gui.AmountGUI;
import driven.by.data.gpsend.gui.PlayerListGUI;
import driven.by.data.gpsend.request.Request;
import driven.by.data.gpsend.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GprequestCommand implements CommandExecutor {

    private final GPSend instance = GPSend.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            MessageUtils.sendMessage(
                    player,
                    "request.invalid_usage",
                    true,
                    null
            );
            return true;
        }


        switch (args[0].toLowerCase()) {

            case "new": {

                if (!player.hasPermission("gprequest.new")) {
                    MessageUtils.sendMessage(
                            player,
                            "errors.no_permission",
                            true,
                            null
                    );
                    return true;
                }


                if (args.length < 3) {
                    if (args.length == 1) {
                        new PlayerListGUI(player, (viewer, target) ->
                                new AmountGUI(viewer, target.getName(),
                                        (v, amt) -> v.performCommand("gprequest new " + target.getName() + " " + amt)
                                ).open()
                        ).open();
                    } else if (args.length == 2) {
                        new AmountGUI(player, args[1],
                                (v, amt) -> v.performCommand("gprequest new " + args[1] + " " + amt)
                        ).open();
                    }
                    return true;
                }


                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    MessageUtils.sendMessage(
                            player,
                            "errors.player_not_found",
                            true,
                            null
                    );
                    return true;
                }


                int amount;

                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {

                    MessageUtils.sendMessage(
                            player,
                            "errors.invalid_amount",
                            true,
                            null
                    );

                    return true;
                }


                if (amount <= 0) {

                    MessageUtils.sendMessage(
                            player,
                            "errors.invalid_amount",
                            true,
                            null
                    );

                    return true;
                }


                boolean created = instance.getRequestManager()
                        .newRequest(player, target, amount);

                if (!created) {
                    MessageUtils.sendMessage(
                            player,
                            "request.already_active",
                            true,
                            null
                    );
                }

                return true;
            }


            case "accept": {

                Request request = instance.getRequestManager()
                        .getRequestForTarget(player);


                if (request == null) {

                    MessageUtils.sendMessage(
                            player,
                            "request.no_pending",
                            true,
                            null
                    );

                    return true;
                }


                instance.getRequestManager()
                        .solveRequest(request, true);

                return true;
            }


            case "deny": {

                Request request = instance.getRequestManager()
                        .getRequestForTarget(player);


                if (request == null) {

                    MessageUtils.sendMessage(
                            player,
                            "request.no_pending",
                            true,
                            null
                    );

                    return true;
                }


                instance.getRequestManager()
                        .solveRequest(request, false);

                return true;
            }


            default: {

                MessageUtils.sendMessage(
                        player,
                        "invalid_usage",
                        true,
                        null
                );

                return true;
            }
        }
    }
}