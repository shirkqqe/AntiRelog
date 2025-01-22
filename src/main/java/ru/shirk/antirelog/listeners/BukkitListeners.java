package ru.shirk.antirelog.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import ru.shirk.antirelog.combat.cooldowns.ItemsCooldownTool;
import ru.shirk.antirelog.tools.Utils;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class BukkitListeners implements Listener {

    private final @NonNull CombatManager combatManager;
    private final @NonNull ItemsCooldownTool itemsCooldownTool;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        combatManager.startCombatSafe(attacker, damaged);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (!combatManager.inCombat(player)) return;
        combatManager.endCombat(player);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (!combatManager.inCombat(player)) return;
        combatManager.endCombat(player);
        player.setHealth(0);
        Bukkit.broadcastMessage(AntiRelog.getConfigurationManager().getConfig("settings.yml")
                .c("messages.leaveInCombatBroadcast").replace("{player}", player.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (!combatManager.inCombat(player)) return;
        if (!AntiRelog.getConfigurationManager().getConfig("settings.yml").getFile()
                .getBoolean("disableCommandsInCombat") || AntiRelog.getConfigurationManager()
                .getConfig("settings.yml").getFile().getStringList("commandWhitelist").contains(event
                        .getMessage().replace("/", "").split(" ")[0])) return;
        event.setCancelled(true);
        player.sendMessage(AntiRelog.getConfigurationManager().getConfig("settings.yml")
                .c("messages.commandsDisabled"));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        if (!combatManager.inCombat(event.getPlayer())) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (Utils.isConsumeItem(event.getMaterial()) || event.getMaterial().equals(Material.ENDER_PEARL)) return;
        itemsCooldownTool.handleUseItem(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onItemConsume(PlayerItemConsumeEvent event) {
        if (!combatManager.inCombat(event.getPlayer())) return;
        itemsCooldownTool.handleUseItem(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onLaunchProjectile(final PlayerLaunchProjectileEvent event) {
        if (!combatManager.inCombat(event.getPlayer())) return;
        itemsCooldownTool.handleUseItem(event);
    }
}
