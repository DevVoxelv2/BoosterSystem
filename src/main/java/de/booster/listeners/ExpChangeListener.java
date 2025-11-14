package de.booster.listeners;

import de.booster.BoosterPlugin;
import de.booster.managers.BoosterManager;
import de.booster.models.BoosterType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class ExpChangeListener implements Listener {

    private final BoosterPlugin plugin;
    private final BoosterManager boosterManager;

    public ExpChangeListener(BoosterPlugin plugin) {
        this.plugin = plugin;
        this.boosterManager = plugin.getBoosterManager();
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        
        if (boosterManager.isBoosterActive(player.getUniqueId(), BoosterType.XP)) {
            double multiplier = boosterManager.getMultiplier(player.getUniqueId(), BoosterType.XP);
            int originalExp = event.getAmount();
            int newExp = (int) (originalExp * multiplier);
            event.setAmount(newExp);
        }
    }
}

