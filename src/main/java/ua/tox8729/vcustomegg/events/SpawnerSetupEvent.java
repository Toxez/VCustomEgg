package ua.tox8729.vcustomegg.events;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ua.tox8729.vcustomegg.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnerSetupEvent {

    public static void handleSpawnerSetup(PlayerInteractEvent event, ConfigUtil.EggConfig eggConfig, ItemStack item) {
        if (event.getClickedBlock().getType() != Material.SPAWNER) {
            return;
        }

        event.setCancelled(true);

        EntityType selectedEntityType = null;
        Random random = new Random();

        List<ConfigUtil.MobChance> hundredPercentMobs = new ArrayList<>();
        for (ConfigUtil.MobChance mobChance : eggConfig.mobs) {
            if (mobChance.chance == 100) {
                hundredPercentMobs.add(mobChance);
            }
        }

        if (!hundredPercentMobs.isEmpty()) {
            selectedEntityType = hundredPercentMobs.get(0).entityType;
        } else {
            double totalWeight = 0;
            for (ConfigUtil.MobChance mobChance : eggConfig.mobs) {
                totalWeight += mobChance.chance;
            }

            double roll = random.nextDouble() * totalWeight;
            double cumulativeWeight = 0;
            selectedEntityType = null;
            for (ConfigUtil.MobChance mobChance : eggConfig.mobs) {
                cumulativeWeight += mobChance.chance;
                if (roll <= cumulativeWeight) {
                    selectedEntityType = mobChance.entityType;
                    break;
                }
            }

            if (selectedEntityType == null) {
                selectedEntityType = eggConfig.mobs.get(eggConfig.mobs.size() - 1).entityType;
            }
        }

        if (selectedEntityType != null) {
            CreatureSpawner spawner = (CreatureSpawner) event.getClickedBlock().getState();
            spawner.setSpawnedType(selectedEntityType);
            spawner.update();
            item.setAmount(item.getAmount() - 1);
        }
    }
}