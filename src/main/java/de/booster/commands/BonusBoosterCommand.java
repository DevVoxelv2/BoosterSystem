package de.booster.commands;

import de.booster.BoosterPlugin;
import de.booster.managers.BoosterManager;
import de.booster.models.BoosterType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class BonusBoosterCommand implements CommandExecutor {

    private final BoosterPlugin plugin;
    private final BoosterManager boosterManager;
    private final Random random;

    public BonusBoosterCommand(BoosterPlugin plugin) {
        this.plugin = plugin;
        this.boosterManager = plugin.getBoosterManager();
        this.random = new Random();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("booster.bonus")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        // Zufälligen Booster-Typ auswählen
        BoosterType[] types = BoosterType.values();
        BoosterType randomType = types[random.nextInt(types.length)];
        
        // 1-3 Booster geben
        int amount = random.nextInt(3) + 1;
        
        boosterManager.addBooster(player.getUniqueId(), randomType, amount);
        
        String message = plugin.getConfigManager().getRawMessage("booster-received")
                .replace("{amount}", String.valueOf(amount))
                .replace("{type}", randomType.name());
        player.sendMessage(plugin.getConfigManager().getMessage("prefix") + message);
        player.sendMessage("§6Du hast einen Bonusbooster erhalten!");

        return true;
    }
}

