package de.booster.listeners;

import de.booster.BoosterPlugin;
import de.booster.managers.BoosterManager;
import de.booster.models.BoosterType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class EntityDeathListener implements Listener {

    private final BoosterPlugin plugin;
    private final BoosterManager boosterManager;
    private final Random random;

    public EntityDeathListener(BoosterPlugin plugin) {
        this.plugin = plugin;
        this.boosterManager = plugin.getBoosterManager();
        this.random = new Random();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        
        if (killer == null) {
            return;
        }

        // Drop Booster
        if (boosterManager.isBoosterActive(killer.getUniqueId(), BoosterType.DROP)) {
            double multiplier = boosterManager.getMultiplier(killer.getUniqueId(), BoosterType.DROP);
            
            Collection<ItemStack> drops = event.getDrops();
            for (ItemStack drop : new ArrayList<>(drops)) {
                if (random.nextDouble() < (multiplier - 1.0)) {
                    ItemStack extra = drop.clone();
                    drops.add(extra);
                }
            }
        }

        // Mob Booster
        if (boosterManager.isBoosterActive(killer.getUniqueId(), BoosterType.MOB)) {
            double multiplier = boosterManager.getMultiplier(killer.getUniqueId(), BoosterType.MOB);
            
            Collection<ItemStack> drops = event.getDrops();
            for (ItemStack drop : new ArrayList<>(drops)) {
                if (random.nextDouble() < (multiplier - 1.0)) {
                    ItemStack extra = drop.clone();
                    drops.add(extra);
                }
            }
        }
    }
}

