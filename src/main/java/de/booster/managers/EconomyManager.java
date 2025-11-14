package de.booster.managers;

import de.booster.BoosterPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private final BoosterPlugin plugin;
    private Economy economy;
    private boolean enabled;

    public EconomyManager(BoosterPlugin plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    private void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault wurde nicht gefunden! Economy-Funktionen sind deaktiviert.");
            enabled = false;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("Keine Economy gefunden! Economy-Funktionen sind deaktiviert.");
            enabled = false;
            return;
        }

        economy = rsp.getProvider();
        enabled = true;
        plugin.getLogger().info("Economy erfolgreich verbunden: " + economy.getName());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasEnough(Player player, double amount) {
        if (!enabled) {
            return false;
        }
        return economy.has(player, amount);
    }

    public boolean withdraw(Player player, double amount) {
        if (!enabled) {
            return false;
        }
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public double getBalance(Player player) {
        if (!enabled) {
            return 0.0;
        }
        return economy.getBalance(player);
    }

    public String format(double amount) {
        if (!enabled) {
            return String.valueOf(amount);
        }
        return economy.format(amount);
    }

    public String getCurrencyName() {
        if (!enabled) {
            return "Coins";
        }
        return economy.currencyNameSingular();
    }
}

