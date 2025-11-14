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
        Inventory inv = Bukkit.createInventory(null, 54, "§6Booster Shop");

        // Break Booster
        inv.setItem(10, createShopItem(Material.DIAMOND_PICKAXE, BoosterType.BREAK, "§6Break-Booster", "§7Erhöht die Drop-Chance beim Abbauen"));

        // Drop Booster
        inv.setItem(12, createShopItem(Material.CHEST, BoosterType.DROP, "§6Drop-Booster", "§7Erhöht die Drop-Chance"));

        // Fly Booster
        inv.setItem(14, createShopItem(Material.FEATHER, BoosterType.FLY, "§6Fly-Booster", "§7Ermöglicht das Fliegen"));

        // Mob Booster
        inv.setItem(16, createShopItem(Material.ZOMBIE_HEAD, BoosterType.MOB, "§6Mob-Booster", "§7Erhöht die Drop-Chance von Mobs"));

        // XP Booster
        inv.setItem(28, createShopItem(Material.EXPERIENCE_BOTTLE, BoosterType.XP, "§6XP-Booster", "§7Erhöht die erhaltene Erfahrung"));

        player.openInventory(inv);
    }

    private ItemStack createShopItem(Material material, BoosterType type, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add(description);
        lore.add("");
        
        int price = plugin.getConfigManager().getShopPrice(type.name().toLowerCase());
        lore.add("§7Preis: §6" + price + " Coins");
        lore.add("");
        lore.add("§aLinksklick: §7Kaufen");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}

