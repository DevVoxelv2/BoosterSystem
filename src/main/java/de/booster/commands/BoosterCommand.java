package de.booster.commands;

import de.booster.BoosterPlugin;
import de.booster.gui.BoosterGUI;
import de.booster.gui.ShopGUI;
import de.booster.managers.BoosterManager;
import de.booster.models.BoosterType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BoosterCommand implements CommandExecutor, TabCompleter {

    private final BoosterPlugin plugin;
    private final BoosterManager boosterManager;

    public BoosterCommand(BoosterPlugin plugin) {
        this.plugin = plugin;
        this.boosterManager = plugin.getBoosterManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // /booster - GUI öffnen
            if (!player.hasPermission("booster.use")) {
                player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }
            new BoosterGUI(plugin, player).open();
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "status":
                handleStatus(player);
                break;
            case "buy":
                if (!player.hasPermission("booster.buy")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                new ShopGUI(plugin, player).open();
                break;
            case "update":
                handleUpdate(player);
                break;
            case "send":
                if (args.length < 3) {
                    player.sendMessage("§cVerwendung: /booster send <Spieler> <Anzahl>");
                    return true;
                }
                handleSend(player, args[1], args[2]);
                break;
            case "break":
                if (!player.hasPermission("booster.break")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                handleActivate(player, BoosterType.BREAK);
                break;
            case "drop":
                if (!player.hasPermission("booster.drop")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                handleActivate(player, BoosterType.DROP);
                break;
            case "fly":
                if (!player.hasPermission("booster.fly")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                handleActivate(player, BoosterType.FLY);
                break;
            case "mob":
                if (!player.hasPermission("booster.mob")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                handleActivate(player, BoosterType.MOB);
                break;
            case "xp":
                if (!player.hasPermission("booster.xp")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                handleActivate(player, BoosterType.XP);
                break;
            case "add":
                if (!player.hasPermission("booster.admin")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                if (args.length < 4) {
                    player.sendMessage("§cVerwendung: /booster add <Spieler> <Anzahl> <Type>");
                    return true;
                }
                handleAdd(player, args[1], args[2], args[3]);
                break;
            case "giveall":
                if (!player.hasPermission("booster.admin")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage("§cVerwendung: /booster giveall <Anzahl> <Type>");
                    return true;
                }
                handleGiveAll(player, args[1], args[2]);
                break;
            case "remove":
                if (!player.hasPermission("booster.admin")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                if (args.length < 4) {
                    player.sendMessage("§cVerwendung: /booster remove <Spieler> <Anzahl> <Type>");
                    return true;
                }
                handleRemove(player, args[1], args[2], args[3]);
                break;
            case "set":
                if (!player.hasPermission("booster.admin")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                if (args.length < 4) {
                    player.sendMessage("§cVerwendung: /booster set <Spieler> <Anzahl> <Type>");
                    return true;
                }
                handleSet(player, args[1], args[2], args[3]);
                break;
            case "see":
                if (!player.hasPermission("booster.admin")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage("§cVerwendung: /booster see <Spieler> <Type>");
                    return true;
                }
                handleSee(player, args[1], args[2]);
                break;
            case "reload":
            case "rl":
                if (!player.hasPermission("booster.admin")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                handleReload(player);
                break;
            default:
                player.sendMessage("§cUnbekannter Subcommand! Verwende /booster für Hilfe.");
                break;
        }

        return true;
    }

    private void handleStatus(Player player) {
        UUID uuid = player.getUniqueId();
        player.sendMessage("§8========== §6Booster Status §8==========");
        
        for (BoosterType type : BoosterType.values()) {
            int count = boosterManager.getBoosterCount(uuid, type);
            boolean active = boosterManager.isBoosterActive(uuid, type);
            String status = active ? "§aAKTIV" : "§7Inaktiv";
            player.sendMessage("§6" + type.name() + ": §7" + count + " §8| " + status);
        }
        
        player.sendMessage("§8=====================================");
    }

    private void handleUpdate(Player player) {
        // Aktualisiert die Booster-Anzeige
        handleStatus(player);
        player.sendMessage(plugin.getConfigManager().getMessage("booster-updated"));
    }

    private void handleSend(Player sender, String targetName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return;
        }

        // Hier könnte man einen speziellen "Send"-Booster implementieren
        // Für jetzt geben wir einen zufälligen Booster
        BoosterType type = BoosterType.values()[(int) (Math.random() * BoosterType.values().length)];
        boosterManager.addBooster(target.getUniqueId(), type, amount);
        
        String message = plugin.getConfigManager().getRawMessage("booster-received")
                .replace("{amount}", String.valueOf(amount))
                .replace("{type}", type.name());
        target.sendMessage(plugin.getConfigManager().getPrefix() + message);
        sender.sendMessage("§aDu hast " + target.getName() + " " + amount + " " + type.name() + "-Booster gesendet!");
    }

    private void handleActivate(Player player, BoosterType type) {
        if (boosterManager.activateBooster(player, type)) {
            String message = plugin.getConfigManager().getRawMessage("booster-activated")
                    .replace("{type}", type.name());
            player.sendMessage(plugin.getConfigManager().getPrefix() + message);
            
            if (type == BoosterType.FLY) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        } else {
            String message = plugin.getConfigManager().getRawMessage("no-boosters")
                    .replace("{type}", type.name());
            player.sendMessage(plugin.getConfigManager().getPrefix() + message);
        }
    }

    private void handleAdd(Player sender, String targetName, String amountStr, String typeStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return;
        }

        BoosterType type = BoosterType.fromString(typeStr);
        if (type == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-type"));
            return;
        }

        boosterManager.addBooster(target.getUniqueId(), type, amount);
        sender.sendMessage("§aDu hast " + target.getName() + " " + amount + " " + type.name() + "-Booster gegeben!");
    }

    private void handleGiveAll(Player sender, String amountStr, String typeStr) {
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return;
        }

        BoosterType type = BoosterType.fromString(typeStr);
        if (type == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-type"));
            return;
        }

        boosterManager.giveAllOnlinePlayers(type, amount);
        sender.sendMessage("§aDu hast allen Online-Spielern " + amount + " " + type.name() + "-Booster gegeben!");
    }

    private void handleRemove(Player sender, String targetName, String amountStr, String typeStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return;
        }

        BoosterType type = BoosterType.fromString(typeStr);
        if (type == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-type"));
            return;
        }

        boosterManager.removeBooster(target.getUniqueId(), type, amount);
        sender.sendMessage("§aDu hast " + target.getName() + " " + amount + " " + type.name() + "-Booster entfernt!");
    }

    private void handleSet(Player sender, String targetName, String amountStr, String typeStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return;
        }

        BoosterType type = BoosterType.fromString(typeStr);
        if (type == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-type"));
            return;
        }

        boosterManager.setBooster(target.getUniqueId(), type, amount);
        sender.sendMessage("§aDu hast " + target.getName() + "s " + type.name() + "-Booster auf " + amount + " gesetzt!");
    }

    private void handleSee(Player sender, String targetName, String typeStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        BoosterType type = BoosterType.fromString(typeStr);
        if (type == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-type"));
            return;
        }

        int count = boosterManager.getBoosterCount(target.getUniqueId(), type);
        sender.sendMessage("§a" + target.getName() + " hat " + count + " " + type.name() + "-Booster.");
    }

    private void handleReload(Player player) {
        plugin.getConfigManager().reloadConfig();
        player.sendMessage(plugin.getConfigManager().getMessage("config-reloaded"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("status", "buy", "update", "send", "break", "drop", "fly", "mob", "xp",
                    "add", "giveall", "remove", "set", "see", "reload", "rl");
            for (String sub : subCommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("send") || args[0].equalsIgnoreCase("add") || 
                args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set") || 
                args[0].equalsIgnoreCase("see")) {
                // Spieler-Namen vorschlagen
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(p.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("giveall")) {
                // Anzahl vorschlagen
                completions.add("1");
                completions.add("5");
                completions.add("10");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("send")) {
                completions.add("1");
                completions.add("5");
                completions.add("10");
            } else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || 
                       args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("giveall")) {
                // Booster-Typen vorschlagen
                for (BoosterType type : BoosterType.values()) {
                    if (type.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                        completions.add(type.name().toLowerCase());
                    }
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || 
                args[0].equalsIgnoreCase("set")) {
                // Booster-Typen vorschlagen
                for (BoosterType type : BoosterType.values()) {
                    if (type.name().toLowerCase().startsWith(args[3].toLowerCase())) {
                        completions.add(type.name().toLowerCase());
                    }
                }
            }
        }

        return completions;
    }
}

