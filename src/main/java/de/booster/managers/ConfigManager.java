package de.booster.managers;

import de.booster.BoosterPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final BoosterPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(BoosterPlugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public int getBoosterDuration() {
        return config.getInt("booster-duration", 3600);
    }

    public double getMultiplier(String type) {
        return config.getDouble("multipliers." + type.toLowerCase(), 2.0);
    }

    public int getShopPrice(String type) {
        return config.getInt("shop." + type.toLowerCase() + ".price", 1000);
    }

    public String getMessage(String key) {
        String prefix = config.getString("messages.prefix", "&8[&6Booster&8] &7");
        String message = config.getString("messages." + key, "");
        String fullMessage = prefix + message;
        return ChatColor.translateAlternateColorCodes('&', fullMessage);
    }

    public String getRawMessage(String key) {
        String message = config.getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

