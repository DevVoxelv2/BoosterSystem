package de.booster.listeners;

import de.booster.BoosterPlugin;
import de.booster.managers.BoosterManager;
import de.booster.models.BoosterType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Random;

public class BlockBreakListener implements Listener {

    private final BoosterPlugin plugin;
    private final BoosterManager boosterManager;
    private final Random random;

    public BlockBreakListener(BoosterPlugin plugin) {
        this.plugin = plugin;
        this.boosterManager = plugin.getBoosterManager();
        this.random = new Random();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (boosterManager.isBoosterActive(player.getUniqueId(), BoosterType.BREAK)) {
            double multiplier = boosterManager.getMultiplier(player.getUniqueId(), BoosterType.BREAK);
            
            // Originale Drops
            Collection<ItemStack> drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand());
            
            // Zusätzliche Drops basierend auf Multiplikator
            for (ItemStack drop : drops) {
                int extraAmount = (int) ((multiplier - 1.0) * drop.getAmount());
                if (extraAmount > 0 && random.nextDouble() < (multiplier - 1.0)) {
                    ItemStack extra = drop.clone();
                    extra.setAmount(extraAmount);
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), extra);
                }
            }
        }
    }
}

