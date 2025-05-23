package ua.tox8729.vcustomegg.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ua.tox8729.vcustomegg.events.MobSpawnEvent;
import ua.tox8729.vcustomegg.events.SpawnerSetupEvent;
import ua.tox8729.vcustomegg.utils.ConfigUtil;
import ua.tox8729.vcustomegg.utils.EggUtil;

public class EggUseListener implements Listener {

    @EventHandler
    public void onEggUse(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        ItemStack item = event.getItem();
        if (!EggUtil.isCustomEgg(item)) {
            return;
        }

        String eggId = EggUtil.getEggId(item);
        ConfigUtil.EggConfig eggConfig = ConfigUtil.getEggConfig(eggId);
        if (eggConfig == null) {
            return;
        }

        if (event.getClickedBlock().getType() == Material.SPAWNER) {
            SpawnerSetupEvent.handleSpawnerSetup(event, eggConfig, item);
        } else {
            MobSpawnEvent.handleMobSpawn(event, eggConfig, item);
        }
    }
}