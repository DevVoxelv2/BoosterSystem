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
        Inventory inv = Bukkit.createInventory(null, 54, "§6Booster Menü");

        UUID uuid = player.getUniqueId();

        // Break Booster
        inv.setItem(10, createBoosterItem(Material.DIAMOND_PICKAXE, BoosterType.BREAK, uuid, "§6Break-Booster", "§7Erhöht die Drop-Chance beim Abbauen"));

        // Drop Booster
        inv.setItem(12, createBoosterItem(Material.CHEST, BoosterType.DROP, uuid, "§6Drop-Booster", "§7Erhöht die Drop-Chance"));

        // Fly Booster
        inv.setItem(14, createBoosterItem(Material.FEATHER, BoosterType.FLY, uuid, "§6Fly-Booster", "§7Ermöglicht das Fliegen"));

        // Mob Booster
        inv.setItem(16, createBoosterItem(Material.ZOMBIE_HEAD, BoosterType.MOB, uuid, "§6Mob-Booster", "§7Erhöht die Drop-Chance von Mobs"));

        // XP Booster
        inv.setItem(28, createBoosterItem(Material.EXPERIENCE_BOTTLE, BoosterType.XP, uuid, "§6XP-Booster", "§7Erhöht die erhaltene Erfahrung"));

        // Shop öffnen
        ItemStack shopItem = new ItemStack(Material.EMERALD);
        ItemMeta shopMeta = shopItem.getItemMeta();
        shopMeta.setDisplayName("§aBooster Shop");
        List<String> shopLore = new ArrayList<>();
        shopLore.add("§7Klicke hier, um den Shop zu öffnen");
        shopMeta.setLore(shopLore);
        shopItem.setItemMeta(shopMeta);
        inv.setItem(40, shopItem);

        // Status anzeigen
        ItemStack statusItem = new ItemStack(Material.BOOK);
        ItemMeta statusMeta = statusItem.getItemMeta();
        statusMeta.setDisplayName("§eBooster Status");
        List<String> statusLore = new ArrayList<>();
        statusLore.add("§7Zeige alle deine Booster");
        statusMeta.setLore(statusLore);
        statusItem.setItemMeta(statusMeta);
        inv.setItem(49, statusItem);

        player.openInventory(inv);
    }

    private ItemStack createBoosterItem(Material material, BoosterType type, UUID uuid, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add(description);
        lore.add("");
        
        int count = boosterManager.getBoosterCount(uuid, type);
        boolean active = boosterManager.isBoosterActive(uuid, type);
        
        lore.add("§7Anzahl: §6" + count);
        lore.add(active ? "§aStatus: §aAKTIV" : "§7Status: §cInaktiv");
        lore.add("");
        
        if (count > 0) {
            lore.add("§aLinksklick: §7Booster aktivieren");
        } else {
            lore.add("§cDu hast keine " + type.name() + "-Booster!");
            lore.add("§7Besuche den Shop, um welche zu kaufen");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}

