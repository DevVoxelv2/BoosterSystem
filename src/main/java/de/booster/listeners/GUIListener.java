package de.booster.listeners;

import de.booster.BoosterPlugin;
import de.booster.gui.BoosterGUI;
import de.booster.gui.ShopGUI;
import de.booster.managers.BoosterManager;
import de.booster.models.BoosterType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class GUIListener implements Listener {

    private final BoosterPlugin plugin;
    private final BoosterManager boosterManager;

    public GUIListener(BoosterPlugin plugin) {
        this.plugin = plugin;
        this.boosterManager = plugin.getBoosterManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals("§6Booster Menü")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            Material clicked = event.getCurrentItem().getType();
            
            // Shop öffnen
            if (clicked == Material.EMERALD) {
                new ShopGUI(plugin, player).open();
                return;
            }

            // Status anzeigen
            if (clicked == Material.BOOK) {
                player.closeInventory();
                player.performCommand("booster status");
                return;
            }

            // Booster aktivieren basierend auf Material
            BoosterType type = getBoosterTypeFromMaterial(clicked);
            if (type != null) {
                if (boosterManager.hasBooster(player.getUniqueId(), type)) {
                    if (boosterManager.activateBooster(player, type)) {
                        String message = plugin.getConfigManager().getRawMessage("booster-activated")
                                .replace("{type}", type.name());
                        player.sendMessage(plugin.getConfigManager().getMessage("prefix") + message);
                        
                        if (type == BoosterType.FLY) {
                            player.setAllowFlight(true);
                            player.setFlying(true);
                        }
                        
                        // GUI aktualisieren
                        new BoosterGUI(plugin, player).open();
                    }
                } else {
                    player.sendMessage("§cDu hast keine " + type.name() + "-Booster!");
                }
            }
        } else if (title.equals("§6Booster Shop")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            Material clicked = event.getCurrentItem().getType();
            BoosterType type = getBoosterTypeFromMaterial(clicked);
            
            if (type != null) {
                int price = plugin.getConfigManager().getShopPrice(type.name().toLowerCase());
                
                // Economy prüfen
                if (!plugin.getEconomyManager().isEnabled()) {
                    player.sendMessage("§cEconomy ist nicht verfügbar! Bitte installiere Vault und ein Economy-Plugin.");
                    return;
                }
                
                // Prüfen ob Spieler genug Geld hat
                if (!plugin.getEconomyManager().hasEnough(player, price)) {
                    player.sendMessage(plugin.getConfigManager().getMessage("not-enough-money"));
                    return;
                }
                
                // Geld abziehen
                if (!plugin.getEconomyManager().withdraw(player, price)) {
                    player.sendMessage("§cFehler beim Kauf! Bitte versuche es erneut.");
                    return;
                }
                
                // Booster geben
                boosterManager.addBooster(player.getUniqueId(), type, 1);
                
                String message = plugin.getConfigManager().getRawMessage("purchase-success")
                        .replace("{type}", type.name());
                player.sendMessage(plugin.getConfigManager().getMessage("prefix") + message);
                
                // GUI aktualisieren
                new ShopGUI(plugin, player).open();
            }
        }
    }

    private BoosterType getBoosterTypeFromMaterial(Material material) {
        switch (material) {
            case DIAMOND_PICKAXE:
                return BoosterType.BREAK;
            case CHEST:
                return BoosterType.DROP;
            case FEATHER:
                return BoosterType.FLY;
            case ZOMBIE_HEAD:
                return BoosterType.MOB;
            case EXPERIENCE_BOTTLE:
                return BoosterType.XP;
            default:
                return null;
        }
    }
}

