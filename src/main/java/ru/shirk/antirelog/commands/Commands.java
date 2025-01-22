package ru.shirk.antirelog.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shirk.antirelog.AntiRelog;
import ru.shirk.antirelog.combat.CombatManager;
import ru.shirk.antirelog.combat.cooldowns.ItemsCooldownTool;
import ru.shirk.antirelog.storage.files.Configuration;

import java.util.List;

@RequiredArgsConstructor
public class Commands implements CommandExecutor, TabCompleter {

    private final @NonNull CombatManager combatManager;
    private final @NonNull Configuration config = AntiRelog.getConfigurationManager().getConfig("settings.yml");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission("antirelog.admin")) {
            sender.sendMessage(config.c("messages.perm"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(config.c("messages.help"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start" -> {
                if (args.length < 2) {
                    sender.sendMessage(config.c("messages.help"));
                    return true;
                }
                final Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(config.c("messages.playerNotFound"));
                    return true;
                }
                final CombatManager.Result result = combatManager.forceStartCombat(player);
                sender.sendMessage(result == CombatManager.Result.SUCCESS ? config.c("messages.forceCombatStart")
                        .replace("{player}", player.getName()) : config.c("messages.playerAlreadyInCombat"));
            }
            case "end" -> {
                if (args.length < 2) {
                    sender.sendMessage(config.c("messages.help"));
                    return true;
                }
                final Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(config.c("messages.playerNotFound"));
                    return true;
                }
                combatManager.endCombat(player);
                sender.sendMessage(config.c("messages.forceCombatEnd").replace("{player}", args[1]));
            }
            case "reload" -> {
                AntiRelog.getConfigurationManager().reloadConfigs();
                ItemsCooldownTool.reload();
                sender.sendMessage(config.c("messages.reloaded"));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission("antirelog.admin")) return List.of();
        if (args.length == 1) {
            return List.of("start", "end", "reload");
        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
