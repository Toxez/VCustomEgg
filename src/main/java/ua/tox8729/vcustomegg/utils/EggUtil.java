package ua.tox8729.vcustomegg.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

public class EggUtil {

    private static final String EGG_ID_KEY = "vcustomegg_egg_id";

    public static ItemStack createCustomEgg(String eggId) {
        ConfigUtil.EggConfig eggConfig = ConfigUtil.getEggConfig(eggId);
        if (eggConfig == null) return null;

        ItemStack egg = new ItemStack(eggConfig.material);
        ItemMeta meta = egg.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(HexUtil.translate(eggConfig.name));
            meta.setLore(eggConfig.lore.stream().map(HexUtil::translate).collect(Collectors.toList()));
            meta.setCustomModelData(1001);
            meta.getPersistentDataContainer().set(
                    getNamespacedKey(EGG_ID_KEY),
                    org.bukkit.persistence.PersistentDataType.STRING,
                    eggId
            );
            if (eggConfig.enchanted) {
                meta.addEnchant(Enchantment.LUCK, 1, true);
            }
            if (eggConfig.hideAttributes) {
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            }
            egg.setItemMeta(meta);
        }

        return egg;
    }

    public static void giveCustomEgg(org.bukkit.entity.Player player, String eggId, int amount) {
        ItemStack egg = createCustomEgg(eggId);
        if (egg != null) {
            egg.setAmount(amount);
            player.getInventory().addItem(egg);
        }
    }

    public static void giveCustomEgg(org.bukkit.entity.Player player, String eggId) {
        giveCustomEgg(player, eggId, 1);
    }

    public static boolean isCustomEgg(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null &&
                meta.hasCustomModelData() &&
                meta.getCustomModelData() == 1001;
    }

    public static String getEggId(ItemStack item) {
        if (!isCustomEgg(item)) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(
                getNamespacedKey(EGG_ID_KEY),
                org.bukkit.persistence.PersistentDataType.STRING
        );
    }

    public static org.bukkit.NamespacedKey getNamespacedKey(String key) {
        return new org.bukkit.NamespacedKey(ConfigUtil.plugin, key);
    }
}