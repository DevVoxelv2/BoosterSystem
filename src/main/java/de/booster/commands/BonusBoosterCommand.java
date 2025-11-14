package de.booster.commands;

import de.booster.BoosterPlugin;
import de.booster.managers.BoosterManager;
import de.booster.models.BoosterType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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

        // Cooldown prüfen
        if (!boosterManager.canUseBonusBooster(player.getUniqueId())) {
            long remainingMillis = boosterManager.getBonusBoosterCooldownRemaining(player.getUniqueId());
            String timeString = formatTime(remainingMillis);
            String message = plugin.getConfigManager().getRawMessage("bonusbooster-cooldown")
                    .replace("{time}", timeString);
            player.sendMessage(plugin.getConfigManager().getPrefix() + message);
            return true;
        }

        // Mögliche Booster-Typen aus Config laden
        List<BoosterType> possibleTypes = getPossibleTypes();
        
        if (possibleTypes.isEmpty()) {
            player.sendMessage("§cKeine gültigen Booster-Typen in der Config gefunden!");
            return true;
        }

        // Zufälligen Booster-Typ auswählen
        BoosterType randomType = possibleTypes.get(random.nextInt(possibleTypes.size()));
        
        // Anzahl aus Config laden
        int minAmount = plugin.getConfigManager().getBonusBoosterMinAmount();
        int maxAmount = plugin.getConfigManager().getBonusBoosterMaxAmount();
        
        // Sicherstellen, dass min <= max
        if (minAmount > maxAmount) {
            int temp = minAmount;
            minAmount = maxAmount;
            maxAmount = temp;
        }
        
        // Zufällige Anzahl zwischen min und max
        int amount = minAmount + (maxAmount > minAmount ? random.nextInt(maxAmount - minAmount + 1) : 0);
        
        boosterManager.addBooster(player.getUniqueId(), randomType, amount);
        boosterManager.setBonusBoosterUsed(player.getUniqueId());
        
        String message = plugin.getConfigManager().getRawMessage("booster-received")
                .replace("{amount}", String.valueOf(amount))
                .replace("{type}", randomType.name());
        player.sendMessage(plugin.getConfigManager().getPrefix() + message);
        player.sendMessage("§6Du hast einen Bonusbooster erhalten!");

        return true;
    }

    private List<BoosterType> getPossibleTypes() {
        List<String> configTypes = plugin.getConfigManager().getBonusBoosterPossibleTypes();
        List<BoosterType> possibleTypes = new ArrayList<>();
        
        // Wenn Liste leer ist, alle Typen verwenden
        if (configTypes == null || configTypes.isEmpty()) {
            for (BoosterType type : BoosterType.values()) {
                possibleTypes.add(type);
            }
        } else {
            // Nur die in der Config angegebenen Typen verwenden
            for (String typeStr : configTypes) {
                try {
                    BoosterType type = BoosterType.valueOf(typeStr.toUpperCase());
                    possibleTypes.add(type);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Ungültiger Booster-Typ in bonusbooster.possible-types: " + typeStr);
                }
            }
        }
        
        return possibleTypes;
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " Tag" + (days > 1 ? "e" : "");
        } else if (hours > 0) {
            return hours + " Stunde" + (hours > 1 ? "n" : "");
        } else if (minutes > 0) {
            return minutes + " Minute" + (minutes > 1 ? "n" : "");
        } else {
            return seconds + " Sekunde" + (seconds > 1 ? "n" : "");
        }
    }
}

