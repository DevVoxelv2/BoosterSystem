package de.booster.managers;

import de.booster.BoosterPlugin;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderManager {

    private final BoosterPlugin plugin;
    private boolean enabled;

    public ItemsAdderManager(BoosterPlugin plugin) {
        this.plugin = plugin;
        setupItemsAdder();
    }

    private void setupItemsAdder() {
        if (plugin.getServer().getPluginManager().getPlugin("ItemsAdder") == null) {
            enabled = false;
            return;
        }

        if (!plugin.getConfigManager().isItemsAdderEnabled()) {
            enabled = false;
            return;
        }

        enabled = true;
        plugin.getLogger().info("ItemsAdder-Unterstützung aktiviert!");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ItemStack getItem(String namespace) {
        if (!enabled || namespace == null || namespace.isEmpty()) {
            return null;
        }

        try {
            ItemStack item = ItemsAdder.getCustomItem(namespace);
            if (item != null) {
                return item;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Fehler beim Laden von ItemsAdder-Item: " + namespace + " - " + e.getMessage());
        }

        return null;
    }

    public ItemStack getItemOrDefault(String namespace, Material defaultMaterial) {
        ItemStack item = getItem(namespace);
        if (item != null) {
            return item;
        }
        return new ItemStack(defaultMaterial);
    }

    public ItemStack getGlassPane() {
        String namespace = plugin.getConfigManager().getItemsAdderItem("glass");
        return getItemOrDefault(namespace, Material.GRAY_STAINED_GLASS_PANE);
    }

    public ItemStack getOverviewItem() {
        String namespace = plugin.getConfigManager().getItemsAdderItem("overview");
        return getItem(namespace);
    }

    public ItemStack getShopButton() {
        String namespace = plugin.getConfigManager().getItemsAdderItem("shop-button");
        return getItemOrDefault(namespace, Material.GOLD_INGOT);
    }

    public ItemStack getBackButton() {
        String namespace = plugin.getConfigManager().getItemsAdderItem("back-button");
        return getItemOrDefault(namespace, Material.ARROW);
    }

    public ItemStack getBalanceItem() {
        String namespace = plugin.getConfigManager().getItemsAdderItem("balance");
        return getItemOrDefault(namespace, Material.GOLD_INGOT);
    }

    public ItemStack getBoosterItem(String type) {
        String namespace = plugin.getConfigManager().getItemsAdderItem(type);
        return getItem(namespace);
    }
}

