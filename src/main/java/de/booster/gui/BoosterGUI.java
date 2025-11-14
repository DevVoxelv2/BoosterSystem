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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BoosterGUI {

    private final BoosterPlugin plugin;
    private final Player player;
    private final BoosterManager boosterManager;

    public BoosterGUI(BoosterPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.boosterManager = plugin.getBoosterManager();
    }

    public void open() {
        Inventory inv = Bukkit.createInventory(null, 54, "§cBooster §7- §aÜbersicht");

        UUID uuid = player.getUniqueId();

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

        // Übersichts-Item (Player Head) - Zeigt alle Booster-Anzahlen (Mitte oben)
        ItemStack overviewItem = plugin.getItemsAdderManager().getOverviewItem();
        if (overviewItem != null) {
            inv.setItem(4, overviewItem);
        } else {
            inv.setItem(4, createOverviewItem(uuid));
        }

        // Shop öffnen (Gold-Ingot) - Links oben
        ItemStack shopButton = plugin.getItemsAdderManager().getShopButton();
        if (shopButton == null || !plugin.getItemsAdderManager().isEnabled()) {
            // Standard-Item verwenden wenn ItemsAdder nicht aktiv oder Item nicht gefunden
            inv.setItem(0, createShopItem());
        } else {
            inv.setItem(0, shopButton);
        }

        // Booster-Items in der Mitte (Reihe 3, zentriert)
        // Break Booster
        ItemStack breakItem = plugin.getItemsAdderManager().getBoosterItem("break");
        inv.setItem(20, breakItem != null ? breakItem : createBoosterItem(Material.IRON_PICKAXE, BoosterType.BREAK, uuid, 
            "§6Break-Booster", "§7Erhöht die Drop-Chance beim Abbauen"));

        // Drop Booster
        ItemStack dropItem = plugin.getItemsAdderManager().getBoosterItem("drop");
        inv.setItem(21, dropItem != null ? dropItem : createBoosterItem(Material.IRON_INGOT, BoosterType.DROP, uuid, 
            "§6Drop-Booster", "§7Erhöht die Drop-Chance"));

        // Fly Booster
        ItemStack flyItem = plugin.getItemsAdderManager().getBoosterItem("fly");
        inv.setItem(22, flyItem != null ? flyItem : createBoosterItem(Material.FEATHER, BoosterType.FLY, uuid, 
            "§6Fly-Booster", "§7Ermöglicht das Fliegen"));

        // Mob Booster
        ItemStack mobItem = plugin.getItemsAdderManager().getBoosterItem("mob");
        inv.setItem(23, mobItem != null ? mobItem : createBoosterItem(Material.BONE, BoosterType.MOB, uuid, 
            "§6Mob-Booster", "§7Erhöht die Drop-Chance von Mobs"));

        // XP Booster
        ItemStack xpItem = plugin.getItemsAdderManager().getBoosterItem("xp");
        inv.setItem(24, xpItem != null ? xpItem : createBoosterItem(Material.EXPERIENCE_BOTTLE, BoosterType.XP, uuid, 
            "§6XP-Booster", "§7Erhöht die erhaltene Erfahrung"));

        player.openInventory(inv);
    }

    private ItemStack createOverviewItem(UUID uuid) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName("§6Anzahl der Booster:");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8─────────────────");
        
        Map<BoosterType, Integer> allBoosters = boosterManager.getAllBoosters(uuid);
        
        lore.add("§7Break-Booster: §6" + allBoosters.getOrDefault(BoosterType.BREAK, 0));
        lore.add("§7Drop-Booster: §6" + allBoosters.getOrDefault(BoosterType.DROP, 0));
        lore.add("§7Fly-Booster: §6" + allBoosters.getOrDefault(BoosterType.FLY, 0));
        lore.add("§7Mob-Booster: §6" + allBoosters.getOrDefault(BoosterType.MOB, 0));
        lore.add("§7XP-Booster: §6" + allBoosters.getOrDefault(BoosterType.XP, 0));
        
        lore.add("§8─────────────────");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBoosterItem(Material material, BoosterType type, UUID uuid, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add(description);
        lore.add("§8─────────────────");
        
        int count = boosterManager.getBoosterCount(uuid, type);
        boolean active = boosterManager.isBoosterActive(uuid, type);
        
        lore.add("§7Anzahl: §6" + count);
        lore.add(active ? "§aStatus: §aAKTIV" : "§7Status: §cInaktiv");
        lore.add("§8─────────────────");
        
        if (count > 0) {
            String typeName = getTypeName(type);
            lore.add("§aKlicke, um einen " + typeName + " zu aktivieren");
        } else {
            String typeName = getTypeName(type);
            lore.add("§cDu hast keine " + typeName + "!");
            lore.add("§7Besuche den Shop, um welche zu kaufen");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createShopItem() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Shop");
        
        List<String> lore = new ArrayList<>();
        lore.add("§8─────────────────");
        lore.add("§7Zum Shop");
        lore.add("§8─────────────────");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String getTypeName(BoosterType type) {
        switch (type) {
            case BREAK:
                return "Break-Booster";
            case DROP:
                return "Drop-Booster";
            case FLY:
                return "Fly-Booster";
            case MOB:
                return "Mob-Booster";
            case XP:
                return "XP-Booster";
            default:
                return type.name() + "-Booster";
        }
    }
}

