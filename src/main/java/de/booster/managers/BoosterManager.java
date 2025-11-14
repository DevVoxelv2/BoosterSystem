package de.booster.managers;

import de.booster.BoosterPlugin;
import de.booster.models.BoosterType;
import de.booster.models.PlayerBooster;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BoosterManager {

    private final BoosterPlugin plugin;
    private final Map<UUID, Map<BoosterType, Integer>> playerBoosters;
    private final Map<UUID, Map<BoosterType, Long>> activeBoosters;
    private final File dataFile;

    public BoosterManager(BoosterPlugin plugin) {
        this.plugin = plugin;
        this.playerBoosters = new HashMap<>();
        this.activeBoosters = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        
        loadData();
        startBoosterCheckTask();
    }

    public void addBooster(UUID uuid, BoosterType type, int amount) {
        Map<BoosterType, Integer> boosters = playerBoosters.getOrDefault(uuid, new HashMap<>());
        boosters.put(type, boosters.getOrDefault(type, 0) + amount);
        playerBoosters.put(uuid, boosters);
    }

    public void removeBooster(UUID uuid, BoosterType type, int amount) {
        Map<BoosterType, Integer> boosters = playerBoosters.getOrDefault(uuid, new HashMap<>());
        int current = boosters.getOrDefault(type, 0);
        boosters.put(type, Math.max(0, current - amount));
        playerBoosters.put(uuid, boosters);
    }

    public void setBooster(UUID uuid, BoosterType type, int amount) {
        Map<BoosterType, Integer> boosters = playerBoosters.getOrDefault(uuid, new HashMap<>());
        boosters.put(type, Math.max(0, amount));
        playerBoosters.put(uuid, boosters);
    }

    public int getBoosterCount(UUID uuid, BoosterType type) {
        return playerBoosters.getOrDefault(uuid, new HashMap<>()).getOrDefault(type, 0);
    }

    public Map<BoosterType, Integer> getAllBoosters(UUID uuid) {
        return playerBoosters.getOrDefault(uuid, new HashMap<>());
    }

    public boolean hasBooster(UUID uuid, BoosterType type) {
        return getBoosterCount(uuid, type) > 0;
    }

    public boolean activateBooster(Player player, BoosterType type) {
        UUID uuid = player.getUniqueId();
        
        if (!hasBooster(uuid, type)) {
            return false;
        }

        removeBooster(uuid, type, 1);
        
        Map<BoosterType, Long> active = activeBoosters.getOrDefault(uuid, new HashMap<>());
        long endTime = System.currentTimeMillis() + (plugin.getConfigManager().getBoosterDuration() * 1000L);
        active.put(type, endTime);
        activeBoosters.put(uuid, active);
        
        return true;
    }

    public boolean isBoosterActive(UUID uuid, BoosterType type) {
        Map<BoosterType, Long> active = activeBoosters.getOrDefault(uuid, new HashMap<>());
        Long endTime = active.get(type);
        
        if (endTime == null) {
            return false;
        }
        
        if (System.currentTimeMillis() > endTime) {
            active.remove(type);
            return false;
        }
        
        return true;
    }

    public double getMultiplier(UUID uuid, BoosterType type) {
        if (isBoosterActive(uuid, type)) {
            return plugin.getConfigManager().getMultiplier(type.name().toLowerCase());
        }
        return 1.0;
    }

    public void giveAllOnlinePlayers(BoosterType type, int amount) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            addBooster(player.getUniqueId(), type, amount);
        }
    }

    private void startBoosterCheckTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID uuid : new HashSet<>(activeBoosters.keySet())) {
                Map<BoosterType, Long> active = activeBoosters.get(uuid);
                active.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getValue());
                
                if (active.isEmpty()) {
                    activeBoosters.remove(uuid);
                }
            }
        }, 0L, 20L * 60L); // Jede Minute prüfen
    }

    private void loadData() {
        if (!dataFile.exists()) {
            return;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        
        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            Map<BoosterType, Integer> boosters = new HashMap<>();
            
            for (String typeStr : config.getConfigurationSection(key).getKeys(false)) {
                try {
                    BoosterType type = BoosterType.valueOf(typeStr.toUpperCase());
                    int amount = config.getInt(key + "." + typeStr);
                    boosters.put(type, amount);
                } catch (IllegalArgumentException e) {
                    // Ungültiger Typ, überspringen
                }
            }
            
            playerBoosters.put(uuid, boosters);
        }
    }

    public void saveAllData() {
        FileConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<UUID, Map<BoosterType, Integer>> entry : playerBoosters.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<BoosterType, Integer> boosterEntry : entry.getValue().entrySet()) {
                config.set(uuidStr + "." + boosterEntry.getKey().name().toLowerCase(), boosterEntry.getValue());
            }
        }
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Fehler beim Speichern der Daten: " + e.getMessage());
        }
    }
}

