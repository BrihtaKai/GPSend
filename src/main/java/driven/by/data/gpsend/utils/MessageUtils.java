package driven.by.data.gpsend.utils;

import driven.by.data.gpsend.GPSend;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageUtils {

    public static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
    private static final GPSend instance = GPSend.getInstance();

    public static String stringColorise(String startTag, String message) {
        Matcher matcher = null;
        StringBuffer buffer = null;
        if (Bukkit.getServer().getVersion().contains("1.16") || Bukkit.getServer().getVersion().contains("1.17") || Bukkit.getServer().getVersion().contains("1.18") || Bukkit.getServer().getVersion().contains("1.19") || Bukkit.getServer().getVersion().contains("1.20") || Bukkit.getServer().getVersion().contains("1.21")) {
            final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})");
            matcher = hexPattern.matcher(message);
            buffer = new StringBuffer(message.length() + 4 * 8);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                        + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                        + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                        + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                );
            }
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
    // USED TO COLORIZE STRING LISTS
    public static List<String> listColorise(String startTag, List<String> messages) {
        return messages.stream()
                .map(message -> stringColorise(startTag, message))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param player
     * @param arg
     * @param isKey if true, then arg will be used as config key
     */
    public static void sendMessage(Player player, String arg, boolean isKey) {
        String message = isKey ? GPSend.getInstance().getConfig().getString(arg) : arg;
        if (instance.placeholderAPIInstalled) message = PlaceholderAPI.setPlaceholders(player, message);
        player.sendMessage(stringColorise("&#", message));
    }

}
