package ua.tox8729.vcustomegg.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ua.tox8729.vcustomegg.VCustomEgg;
import ua.tox8729.vcustomegg.utils.ConfigUtil;
import ua.tox8729.vcustomegg.utils.EggUtil;
import ua.tox8729.vcustomegg.utils.HexUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VCommand implements CommandExecutor, TabCompleter {

    private final VCustomEgg plugin;

    public VCommand(VCustomEgg plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("vcustomeggs.give") && !sender.hasPermission("vcustomeggs.reload")) {
            sender.sendMessage(HexUtil.translate(ConfigUtil.noAccessMessage));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(HexUtil.translate(ConfigUtil.usageCommandsMessage));
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 3 || args.length > 4) {
                sender.sendMessage(HexUtil.translate(ConfigUtil.usageCommandsMessage));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(HexUtil.translate(ConfigUtil.noPlayerMessage));
                return true;
            }

            String eggId = args[2].toLowerCase();
            ConfigUtil.EggConfig eggConfig = ConfigUtil.getEggConfig(eggId);

            if (eggConfig == null) {
                sender.sendMessage(HexUtil.translate(ConfigUtil.badEggMessage));
                return true;
            }

            int amount = 1;
            if (args.length == 4) {
                try {
                    amount = Integer.parseInt(args[3]);
                    if (amount <= 0) {
                        sender.sendMessage(HexUtil.translate(ConfigUtil.lowAmountMessage));
                        return true;
                    }
                    if (amount > 64) {
                        sender.sendMessage(HexUtil.translate(ConfigUtil.limitMessage));
                        amount = 64;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(HexUtil.translate(ConfigUtil.badAmountMessage));
                    return true;
                }
            }

            EggUtil.giveCustomEgg(target, eggId, amount);
            sender.sendMessage(HexUtil.translate(ConfigUtil.eggGivenMessage.replace("{player}", target.getName())));
        } else if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPlugin();
            sender.sendMessage(HexUtil.translate(ConfigUtil.reloadMessage));
        } else {
            sender.sendMessage(HexUtil.translate(ConfigUtil.usageCommandsMessage));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("vcustomeggs.give")) {
                completions.add("give");
            }
            if (sender.hasPermission("vcustomeggs.reload")) {
                completions.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give") && sender.hasPermission("vcustomeggs.give")) {
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give") && sender.hasPermission("vcustomeggs.give")) {
            completions.addAll(ConfigUtil.getEggIds());
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give") && sender.hasPermission("vcustomeggs.give")) {
            completions.addAll(Arrays.asList("1", "16", "32", "64"));
        }

        return completions.stream()
                .filter(completion -> completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}