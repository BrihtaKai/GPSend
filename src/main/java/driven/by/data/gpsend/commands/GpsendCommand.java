package driven.by.data.gpsend.commands;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.ColorFormat;
import driven.by.data.gpsend.utils.PlayerStatusManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GpsendCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        GPSend plugin = GPSend.get();

        /*
        Command structure:
        /gpsend <PLAYER|ALL>
                            -> case PLAYER:: <PLAYER_NAME> <NUMBER_OF_BLOCKS>
                            -> case ALL:: <NUMBER_OF_BLOCKS>

         If args are missing, open GUI (number of args:: case PLAYER = 2, case ALL = 1)
        */
        // CHECK IF SENDER IS PLAYER
        if (!(sender instanceof Player)) {
            // SENDER IS NOT PLAYER :: SENDE ERROR
            plugin.getServer().getLogger().warning("[GPSend] Only players can use this command!");
            return false;
        }
        Player player = (Player) sender;


        int argLength = args.length;
        if (argLength == 0) {
            // OPEN TYPE CHOOSING GUI
            plugin.getServer().getLogger().warning("[GPSend] choosing gui");
            return false;
        } else {
            String type = args[0];
            switch (type.toLowerCase()) {
                case "player": {
                    PlayerStatusManager.setPlayerStatus(player.getUniqueId(), "type", "player");

                    String playerName = args[1];
                    int amount = Integer.parseInt(args[2]);
                    // TYPE IS PLAYER CHECK ARGS LENGTH
                    if (argLength == 1) {
                        // PLAYER NAME IS MISSING :: OPEN PLAYER SELECT GUI
                        plugin.getServer().getLogger().warning("[GPSend] players-select gui");
                    } else if (argLength == 2) {
                        // NUMBER OF CLAIMBLOCKS IS MISSING :: OPEN AMOUNT-GUI
                        plugin.getServer().getLogger().warning("[GPSend] amount gui");
                    } else if (argLength == 3) {
                        // ALL ARGS ARE ABSENT :: EXECUTE CLAIMBLOCKS GIVE
                        plugin.getServer().getLogger().warning("[GPSend] giving");
                    }
                    break;
                }
                case "all": {
                    PlayerStatusManager.setPlayerStatus(player.getUniqueId(), "type", "player");

                    int amount = Integer.parseInt(args[1]);
                    if (argLength == 1) {
                        // NUMBER OF CLAIMBLOCKS IS MISSING :: OPEN AMOUNT-GUI
                        plugin.getServer().getLogger().warning("[GPSend] amount gui");
                    } else if (argLength == 2) {
                        // ALL ARGS ARE ABSENT :: EXECUTE CLAIMBLOCKS GIVE
                        plugin.getServer().getLogger().warning("[GPSend] giving");
                    }
                    break;
                }
                default: {
                    // TYPE IS INVALID SEND ERROR
                    player.sendMessage(ColorFormat.stringColorise("&#", plugin.getConfig().getString("invalid_type")));
                    break;
                }
            }
        }

        return false;
    }

}
