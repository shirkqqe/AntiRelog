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

import java.util.List;

@RequiredArgsConstructor
public class Commands implements CommandExecutor, TabCompleter {

    private final @NonNull CombatManager combatManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start" -> {
                if (args.length < 2) {
                    sender.sendMessage("Arg error!");
                    return true;
                }
                final Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("Player not found!");
                    return true;
                }
                final CombatManager.Result result = combatManager.forceStartCombat(player);
                sender.sendMessage(result.name());
            }
            case "reload" -> {
                AntiRelog.getConfigurationManager().reloadConfigs();
                sender.sendMessage("Success!");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return List.of();
    }
}
