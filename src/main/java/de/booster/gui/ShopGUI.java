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

        // Break Booster (Eisen-Spitzhacke)
        inv.setItem(10, createShopItem(Material.IRON_PICKAXE, BoosterType.BREAK, 
            "§6Break-Booster", "§7Erhöht die Drop-Chance beim Abbauen"));

        // Drop Booster (Truhe)
        inv.setItem(12, createShopItem(Material.CHEST, BoosterType.DROP, 
            "§6Drop-Booster", "§7Erhöht die Drop-Chance"));

        // Fly Booster (Feder)
        inv.setItem(14, createShopItem(Material.FEATHER, BoosterType.FLY, 
            "§6Fly-Booster", "§7Ermöglicht das Fliegen"));

        // Mob Booster (Zombie-Kopf)
        inv.setItem(16, createShopItem(Material.ZOMBIE_HEAD, BoosterType.MOB, 
            "§6Mob-Booster", "§7Erhöht die Drop-Chance von Mobs"));

        // XP Booster (Erfahrungsflasche)
        inv.setItem(28, createShopItem(Material.EXPERIENCE_BOTTLE, BoosterType.XP, 
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

