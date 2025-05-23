package ua.tox8729.vcustomegg;

import org.bukkit.plugin.java.JavaPlugin;
import ua.tox8729.vcustomegg.commands.VCommand;
import ua.tox8729.vcustomegg.listeners.EggUseListener;
import ua.tox8729.vcustomegg.utils.ConfigUtil;

public class VCustomEgg extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigUtil.init(this);
        VCommand command = new VCommand(this);
        getCommand("vcustomeggs").setExecutor(command);
        getCommand("vcustomeggs").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new EggUseListener(), this);
        getLogger().info("Плагин запущен! Автор: Tox_8729. Версия плагина 1.1");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин отключен!");
    }

    public void reloadPlugin() {
        reloadConfig();
        ConfigUtil.init(this);
    }
}