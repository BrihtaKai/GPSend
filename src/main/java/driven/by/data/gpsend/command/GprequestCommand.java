package driven.by.data.gpsend.command;

import driven.by.data.gpsend.GPSend;
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
                    "invalid_usage",
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
                            "no_permission",
                            true,
                            null
                    );
                    return true;
                }


                if (args.length < 3) {
                    MessageUtils.sendMessage(
                            player,
                            "invalid_usage",
                            true,
                            null
                    );
                    return true;
                }


                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    MessageUtils.sendMessage(
                            player,
                            "player_not_found",
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
                            "invalid_amount",
                            true,
                            null
                    );

                    return true;
                }


                if (amount <= 0) {

                    MessageUtils.sendMessage(
                            player,
                            "invalid_amount",
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
                            "request_already_involved",
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
                            "request_not_found",
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
                            "request_not_found",
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