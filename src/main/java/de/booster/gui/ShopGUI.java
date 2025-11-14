package de.booster.gui;

import de.booster.BoosterPlugin;
import de.booster.managers.BoosterManager;
import de.booster.models.BoosterType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private final BoosterPlugin plugin;
    private final Player player;
    private final BoosterManager boosterManager;

    public ShopGUI(BoosterPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.boosterManager = plugin.getBoosterManager();
    }

    public void open() {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Booster §7- §aShop");

        // Glass-Panes als Hintergrund
        ItemStack glass = plugin.getItemsAdderManager().getGlassPane();
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        
        // Alle Slots mit Glass füllen
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, glass.clone());
        }

        // Guthaben-Anzeige (Mitte oben)
        ItemStack balanceItem = plugin.getItemsAdderManager().getBalanceItem();
        if (balanceItem == null || !plugin.getItemsAdderManager().isEnabled()) {
            inv.setItem(4, createBalanceItem());
        } else {
            inv.setItem(4, balanceItem);
        }

        // Zurück-Button (Links oben)
        ItemStack backItem = plugin.getItemsAdderManager().getBackButton();
        if (backItem == null || !plugin.getItemsAdderManager().isEnabled()) {
            inv.setItem(0, createBackItem());
        } else {
            inv.setItem(0, backItem);
        }

        // Booster-Items in einer Reihe zentriert (Reihe 3)
        // Break Booster
        ItemStack breakItem = plugin.getItemsAdderManager().getBoosterItem("break");
        inv.setItem(19, breakItem != null ? breakItem : createShopItem(Material.IRON_PICKAXE, BoosterType.BREAK, 
            "§6Break-Booster", "§7Erhöht die Drop-Chance beim Abbauen"));

        // Drop Booster
        ItemStack dropItem = plugin.getItemsAdderManager().getBoosterItem("drop");
        inv.setItem(20, dropItem != null ? dropItem : createShopItem(Material.CHEST, BoosterType.DROP, 
            "§6Drop-Booster", "§7Erhöht die Drop-Chance"));

        // Fly Booster
        ItemStack flyItem = plugin.getItemsAdderManager().getBoosterItem("fly");
        inv.setItem(21, flyItem != null ? flyItem : createShopItem(Material.FEATHER, BoosterType.FLY, 
            "§6Fly-Booster", "§7Ermöglicht das Fliegen"));

        // Mob Booster
        ItemStack mobItem = plugin.getItemsAdderManager().getBoosterItem("mob");
        inv.setItem(22, mobItem != null ? mobItem : createShopItem(Material.ZOMBIE_HEAD, BoosterType.MOB, 
            "§6Mob-Booster", "§7Erhöht die Drop-Chance von Mobs"));

        // XP Booster
        ItemStack xpItem = plugin.getItemsAdderManager().getBoosterItem("xp");
        inv.setItem(23, xpItem != null ? xpItem : createShopItem(Material.EXPERIENCE_BOTTLE, BoosterType.XP, 
            "§6XP-Booster", "§7Erhöht die erhaltene Erfahrung"));

        player.openInventory(inv);
    }

    private ItemStack createBalanceItem() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Dein Guthaben");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8─────────────────");
        
        double balance = plugin.getEconomyManager().getBalance(player);
        lore.add("§7Guthaben: §6" + plugin.getEconomyManager().format(balance));
        
        lore.add("§8─────────────────");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§7Zurück");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8─────────────────");
        lore.add("§7Zurück zur Übersicht");
        lore.add("§8─────────────────");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createShopItem(Material material, BoosterType type, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add(description);
        lore.add("§8─────────────────");
        
        int price = plugin.getConfigManager().getShopPrice(type.name().toLowerCase());
        double balance = plugin.getEconomyManager().getBalance(player);
        int duration = plugin.getConfigManager().getBoosterDuration();
        double multiplier = plugin.getConfigManager().getMultiplier(type.name().toLowerCase());
        
        // Dauer in Stunden/Minuten umrechnen
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        String durationText;
        if (hours > 0) {
            durationText = hours + " Stunde" + (hours > 1 ? "n" : "");
            if (minutes > 0) {
                durationText += " " + minutes + " Minute" + (minutes > 1 ? "n" : "");
            }
        } else {
            durationText = minutes + " Minute" + (minutes > 1 ? "n" : "");
        }
        
        lore.add("§7Preis: §6" + plugin.getEconomyManager().format(price));
        lore.add("§7Dauer: §6" + durationText);
        lore.add("§7Multiplikator: §6" + multiplier + "x");
        lore.add("§8─────────────────");
        
        if (balance >= price) {
            lore.add("§aZum Kaufen anklicken");
        } else {
            double missing = price - balance;
            lore.add("§cDu hast nicht genug Geld!");
            lore.add("§7Fehlend: §c" + plugin.getEconomyManager().format(missing));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}

