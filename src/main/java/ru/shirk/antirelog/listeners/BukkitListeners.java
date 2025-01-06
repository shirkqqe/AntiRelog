package ru.shirk.antirelog.listeners;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.shirk.antirelog.combat.CombatManager;

@RequiredArgsConstructor
public class BukkitListeners implements Listener {

    private final @NonNull CombatManager combatManager;

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        combatManager.startCombatSafe(damager, damaged);
    }
}
