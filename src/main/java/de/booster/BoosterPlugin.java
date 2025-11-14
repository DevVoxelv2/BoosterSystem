package de.booster;

import de.booster.commands.BoosterCommand;
import de.booster.commands.BonusBoosterCommand;
import de.booster.listeners.BlockBreakListener;
import de.booster.listeners.EntityDeathListener;
import de.booster.listeners.ExpChangeListener;
import de.booster.listeners.GUIListener;
import de.booster.managers.BoosterManager;
import de.booster.managers.ConfigManager;
import de.booster.managers.EconomyManager;
import de.booster.managers.ItemsAdderManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BoosterPlugin extends JavaPlugin {

    private static BoosterPlugin instance;
    private BoosterManager boosterManager;
    private ConfigManager configManager;
    private EconomyManager economyManager;
    private ItemsAdderManager itemsAdderManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Config laden
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        // Manager initialisieren
        boosterManager = new BoosterManager(this);
        economyManager = new EconomyManager(this);
        itemsAdderManager = new ItemsAdderManager(this);
        
        // Commands registrieren
        getCommand("booster").setExecutor(new BoosterCommand(this));
        getCommand("bonusbooster").setExecutor(new BonusBoosterCommand(this));
        
        // Listener registrieren
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new ExpChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        
        getLogger().info("BoosterPlugin wurde erfolgreich geladen!");
    }

    @Override
    public void onDisable() {
        if (boosterManager != null) {
            boosterManager.saveAllData();
        }
        getLogger().info("BoosterPlugin wurde entladen!");
    }

    public static BoosterPlugin getInstance() {
        return instance;
    }

    public BoosterManager getBoosterManager() {
        return boosterManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public ItemsAdderManager getItemsAdderManager() {
        return itemsAdderManager;
    }
}

