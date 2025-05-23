package ua.tox8729.vcustomegg.utils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import ua.tox8729.vcustomegg.VCustomEgg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigUtil {
    public static VCustomEgg plugin;
    private static Map<String, EggConfig> eggs;

    public static String noAccessMessage;
    public static String usageCommandsMessage;
    public static String noPlayerMessage;
    public static String eggGivenMessage;
    public static String badEggMessage;
    public static String reloadMessage;
    public static String lowAmountMessage;
    public static String limitMessage;
    public static String badAmountMessage;

    public static void init(VCustomEgg pluginInstance) {
        plugin = pluginInstance;
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        usageCommandsMessage = String.join("\n", config.getStringList("messages.usage-commands-message"));

        noAccessMessage = HexUtil.translate(config.getString("messages.no-access-message", "&7[&#DB4444✘&7] &7У вас &#DB4444нет прав &7на выполнение этой команды!"));
        noPlayerMessage = HexUtil.translate(config.getString("messages.no-player-message", "&7[&#DB4444✘&7] &7Игрок &#DB4444✘не &7найден!"));
        eggGivenMessage = HexUtil.translate(config.getString("messages.egg-given-message", "&7[&#DB4444✘&7] &7Яйцо &#DB4444выдано &7игроку &6{player}!"));
        badEggMessage = HexUtil.translate(config.getString("messages.bad-egg-message", "&7[&#DB4444✘&7] &7Такого яйца &#DB4444✘не &7существует!"));
        reloadMessage = HexUtil.translate(config.getString("messages.reload-message", "&7[&#32CD32✔&7] &7Конфигурация &#32CD32успешно &7перезагружена!"));
        lowAmountMessage = HexUtil.translate(config.getString("messages.low-amount-message", "&7[&#DB4444✘&7] &7Количество должно быть больше &60&7!"));
        limitMessage = HexUtil.translate(config.getString("messages.limit-message", "&7[&#DB4444✘&7] &#DB4444Нельзя &7выдать больше &664 &7предметов!"));
        badAmountMessage = HexUtil.translate(config.getString("messages.bad-amount-message", "&7[&#DB4444✘&7] &7Укажите &#32CD32правильное &7количество!"));

        eggs = new HashMap<>();

        ConfigurationSection eggsSection = config.getConfigurationSection("eggs");
        if (eggsSection == null) {
            plugin.getLogger().warning("Секция 'eggs' не найдена в конфиге! Плагин не сможет работать без настроек яиц");
            return;
        }

        for (String eggId : eggsSection.getKeys(false)) {
            ConfigurationSection eggSection = eggsSection.getConfigurationSection(eggId);
            if (eggSection == null) continue;

            EggConfig eggConfig = new EggConfig();

            String materialName = eggSection.getString("material", "ZOMBIE_SPAWN_EGG");
            try {
                eggConfig.material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Материал '" + materialName + "' для яйца '" + eggId + "' не найден! Используется ZOMBIE_SPAWN_EGG.");
                eggConfig.material = Material.ZOMBIE_SPAWN_EGG;
            }

            eggConfig.name = eggSection.getString("name", "&#B481FFКастомное Яйцо");
            eggConfig.lore = eggSection.getStringList("lore");

            List<String> mobChances = eggSection.getStringList("mobs");
            eggConfig.mobs = new ArrayList<>();

            for (String mobChance : mobChances) {
                String[] parts = mobChance.split(";");
                if (parts.length != 2) continue;

                String entityName = parts[0].toUpperCase();
                double chance;
                try {
                    chance = Double.parseDouble(parts[1]);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Шанс '" + parts[1] + "' для моба '" + entityName + "' в яйце '" + eggId + "' не является числом!");
                    continue;
                }

                if (chance <= 0) continue;

                EntityType entityType;
                try {
                    entityType = EntityType.valueOf(entityName);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Моб '" + entityName + "' в яйце '" + eggId + "' не найден!");
                    continue;
                }

                if (chance > 100) {
                    plugin.getLogger().warning("Шанс '" + chance + "' для моба '" + entityName + "' в яйце '" + eggId + "' больше 100! будет выставленно 100 :(");
                    chance = 100;
                }

                eggConfig.mobs.add(new MobChance(entityType, chance));
            }

            eggConfig.enchanted = eggSection.getBoolean("enchanted", false);
            eggConfig.hideAttributes = eggSection.getBoolean("hide_attributes", false);

            if (eggConfig.mobs.isEmpty()) {
                plugin.getLogger().warning("В яйце '" + eggId + "' не найдено ни одного моба!");
                continue;
            }

            int hundredPercentCount = 0;
            for (MobChance mobChance : eggConfig.mobs) {
                if (mobChance.chance == 100) {
                    hundredPercentCount++;
                }
            }
            if (hundredPercentCount > 1) {
                plugin.getLogger().warning("В яйце '" + eggId + "' найдено " + hundredPercentCount + " мобов с шансом 100%! В одном яйце может быть только один моб с шансом 100%");
                continue;
            }

            eggs.put(eggId.toLowerCase(), eggConfig);
        }

        if (eggs.isEmpty()) {
            plugin.getLogger().warning("Не найдено ни одного яйца в конфиге! Плагин не сможет работать без настроек яиц");
        }
    }

    public static EggConfig getEggConfig(String eggId) {
        return eggs.get(eggId.toLowerCase());
    }

    public static Set<String> getEggIds() {
        return eggs.keySet();
    }

    public static class EggConfig {
        public Material material;
        public String name;
        public List<String> lore;
        public List<MobChance> mobs;
        public boolean enchanted;
        public boolean hideAttributes;
    }

    public static class MobChance {
        public EntityType entityType;
        public double chance;

        public MobChance(EntityType entityType, double chance) {
            this.entityType = entityType;
            this.chance = chance;
        }

        @Override
        public String toString() {
            return entityType + " (" + chance + "%)";
        }
    }
}