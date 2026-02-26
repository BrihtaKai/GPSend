package driven.by.data.gpsend.command;

import driven.by.data.gpsend.GPSend;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;

import java.lang.reflect.Field;

public class AliasManager {

    private final GPSend instance = GPSend.getInstance();

    public void gpsendAliasRegister() {
        PluginCommand gpsend = instance.getCommand("gpsend");

        if (instance.getConfig().getBoolean("separate_commands")) return;

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            instance.getConfig().getStringList("command_alias").forEach(alias -> {
                commandMap.register(alias, "GPSend", gpsend);
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
