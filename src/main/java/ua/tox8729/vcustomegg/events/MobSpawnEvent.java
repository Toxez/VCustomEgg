package ua.tox8729.vcustomegg.events;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ua.tox8729.vcustomegg.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobSpawnEvent {

    public static void handleMobSpawn(PlayerInteractEvent event, ConfigUtil.EggConfig eggConfig, ItemStack item) {
        Random random = new Random();
        EntityType selectedEntityType;

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

        Location spawnLocation = event.getClickedBlock().getLocation().add(0.5, 0, 0.5);
        BlockFace face = event.getBlockFace();

        if (face == BlockFace.UP) {
            spawnLocation.add(0, 1, 0);
        } else if (face == BlockFace.DOWN) {
            spawnLocation.add(0, -1, 0);
        } else {
            Vector direction = face.getDirection();
            spawnLocation.add(direction.getX(), 0, direction.getZ());
        }

        event.setCancelled(true);

        if (selectedEntityType != null) {
            Entity entity = event.getPlayer().getWorld().spawnEntity(spawnLocation, selectedEntityType);

            if (entity instanceof Zombie) {
                ((Zombie) entity).setBaby(false);
            } else if (entity instanceof Ageable) {
                ((Ageable) entity).setAdult();
            }

            entity.setCustomName(null);
            entity.setCustomNameVisible(false);

            item.setAmount(item.getAmount() - 1);
        }
    }
}