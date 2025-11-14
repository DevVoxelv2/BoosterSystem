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
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        // Alle Slots mit Glass füllen
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, glass);
        }

        // Booster-Items zentriert in Reihe 2 und 3
        // Break Booster (Eisen-Spitzhacke) - Reihe 2, Mitte
        inv.setItem(11, createShopItem(Material.IRON_PICKAXE, BoosterType.BREAK, 
            "§6Break-Booster", "§7Erhöht die Drop-Chance beim Abbauen"));

        // Drop Booster (Truhe) - Reihe 2, Mitte
        inv.setItem(13, createShopItem(Material.CHEST, BoosterType.DROP, 
            "§6Drop-Booster", "§7Erhöht die Drop-Chance"));

        // Fly Booster (Feder) - Reihe 2, Mitte
        inv.setItem(15, createShopItem(Material.FEATHER, BoosterType.FLY, 
            "§6Fly-Booster", "§7Ermöglicht das Fliegen"));

        // Mob Booster (Zombie-Kopf) - Reihe 3, Mitte
        inv.setItem(20, createShopItem(Material.ZOMBIE_HEAD, BoosterType.MOB, 
            "§6Mob-Booster", "§7Erhöht die Drop-Chance von Mobs"));

        // XP Booster (Erfahrungsflasche) - Reihe 3, Mitte
        inv.setItem(22, createShopItem(Material.EXPERIENCE_BOTTLE, BoosterType.XP, 
            "§6XP-Booster", "§7Erhöht die erhaltene Erfahrung"));

        player.openInventory(inv);
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
        
        lore.add("§7Preis: §6" + plugin.getEconomyManager().format(price));
        lore.add("§7Dein Guthaben: §6" + plugin.getEconomyManager().format(balance));
        lore.add("§8─────────────────");
        
        if (balance >= price) {
            lore.add("§aZum Kaufen anklicken");
        } else {
            lore.add("§cDu hast nicht genug Geld!");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}

