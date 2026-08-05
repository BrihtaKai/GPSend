package driven.by.data.gpsend.utils;

import driven.by.data.gpsend.GPSend;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageUtils {

    private static final GPSend instance = GPSend.getInstance();
    public static final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    /**
     * Used to colorize strings with HEX and ChatColor.
     * Version checking(1.13+) dropped in 2.1.2
     *
     * @param startTag
     * @param message
     * @return
     */
    public static String stringColorise(String startTag, String message) {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * Used to colorize string lists
     * Version checking(1.13+) dropped in 2.1.2
     *
     * @param startTag
     * @param messages
     * @return new list with colorized strings
     */
    public static List<String> listColorise(String startTag, List<String> messages) {
        return messages.stream()
                .map(message -> stringColorise(startTag, message))
                .collect(Collectors.toList());
    }


    /**
     * Sends a message to a player with placeholders and replacements.
     *
     * @param player
     * @param arg
     * @param isKey if true, then arg will be used as config key
     * @param replacers map of placeholders and replacements
     */
    public static void sendMessage(Player player, String arg, boolean isKey, Map<String, String> replacers) {
        player.sendMessage(getProcessedMessage(player, arg, isKey, replacers));
    }

    /**
     * Returns a processed message with placeholders and replacements.
     *
     * @param player
     * @param arg
     * @param isKey if true, then arg will be used as config key
     * @param replacers map of placeholders and replacements
     * @return
     */
    public static String getProcessedMessage(Player player, String arg, boolean isKey, Map<String, String> replacers) {
        String message = isKey ? GPSend.getInstance().getConfig().getString(arg) : arg;
        if (message == null) return " <!" + arg + "!> ";
        if (instance.placeholderAPIInstalled) message = PlaceholderAPI.setPlaceholders(player, message);
        if (replacers != null) {
            for (Map.Entry<String, String> entry : replacers.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
        }
        return stringColorise("&#", message);
    }

}
