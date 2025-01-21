package ru.shirk.antirelog.listeners;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.shirk.antirelog.AntiRelog;
import ru.shirk.antirelog.combat.CombatManager;
import ru.shirk.antirelog.modules.cooldowns.ItemsCooldownTool;
import ru.shirk.antirelog.tools.Utils;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class BukkitListeners implements Listener {

    private final @NonNull CombatManager combatManager;
    private final @NonNull ItemsCooldownTool itemsCooldownTool;

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        combatManager.startCombatSafe(attacker, damaged);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (!combatManager.inCombat(player)) return;
        combatManager.endCombat(player);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (!combatManager.inCombat(player)) return;
        combatManager.endCombat(player);
        player.setHealth(0);
        Bukkit.broadcastMessage(AntiRelog.getConfigurationManager().getConfig("settings.yml")
                .c("messages.leaveInCombatBroadcast").replace("{player}", player.getName()));
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (!combatManager.inCombat(player)) return;
        if (!AntiRelog.getConfigurationManager().getConfig("settings.yml").getFile()
                .getBoolean("disableCommandsInCombat")) return;
        event.setCancelled(true);
        player.sendMessage(AntiRelog.getConfigurationManager().getConfig("settings.yml")
                .c("messages.commandsDisabled"));
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!combatManager.inCombat(event.getPlayer())) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (Utils.isConsumeItem(event.getMaterial())) return;
        itemsCooldownTool.handleUseItem(event);
    }

    @EventHandler
    private void onItemConsume(PlayerItemConsumeEvent event) {
        if (!combatManager.inCombat(event.getPlayer())) return;
        itemsCooldownTool.handleUseItem(event);
    }
}
