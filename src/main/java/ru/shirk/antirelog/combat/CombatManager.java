package ru.shirk.antirelog.combat;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import ru.shirk.antirelog.AntiRelog;
import ru.shirk.antirelog.listeners.api.CombatEndEvent;
import ru.shirk.antirelog.listeners.api.CombatPreStartEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class CombatManager implements Listener {

    private final Map<UUID, CombatPlayer> combatPlayers = new HashMap<>();

    public @Nullable CombatPlayer getCombatPlayer(@NonNull Player player) {
        return combatPlayers.get(player.getUniqueId());
    }

    public void startCombatSafe(@NonNull Player initiator, @NonNull Player damaged) {
        CombatPlayer combatInitiator = getCombatPlayer(initiator);
        CombatPlayer combatDamaged = getCombatPlayer(damaged);

        if (combatInitiator == null && !initiator.hasPermission("antirelog.bypass")) {
            combatInitiator = new CombatPlayer(initiator);
        }
        if (combatDamaged == null) {
            combatDamaged = new CombatPlayer(damaged);
        }

        if (initiator.hasPermission("antirelog.bypass")) combatInitiator = null;
        if (damaged.hasPermission("antirelog.bypass")) combatDamaged = null;

        final CombatPreStartEvent event = new CombatPreStartEvent(combatInitiator, combatDamaged, CombatPreStartEvent.
                Cause.DAMAGE, AntiRelog.getConfigurationManager().getConfig("settings.yml").ch("combatTime"));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        if (combatInitiator != null) {
            combatInitiator.handleStartCombat();
            if (combatDamaged != null) combatInitiator.addEnemy(combatDamaged);
            combatPlayers.putIfAbsent(initiator.getUniqueId(), combatInitiator);
        }
        if (combatDamaged != null) {
            combatDamaged.handleStartCombat();
            if (combatInitiator != null) combatDamaged.addEnemy(combatInitiator);
            combatPlayers.putIfAbsent(damaged.getUniqueId(), combatDamaged);
        }
    }

    public @NonNull Result forceStartCombat(@NonNull Player player) {
        CombatPlayer combatPlayer = getCombatPlayer(player);
        if (combatPlayer == null) {
            combatPlayer = new CombatPlayer(player);
            combatPlayer.handleStartCombat();
            combatPlayers.putIfAbsent(player.getUniqueId(), combatPlayer);
            return Result.SUCCESS;
        }
        return Result.ALREADY_IN_COMBAT;
    }

    public void endCombat(@NonNull Player player) {
        CombatPlayer combatPlayer = getCombatPlayer(player);
        if (combatPlayer == null) return;
        Bukkit.getPluginManager().callEvent(new CombatEndEvent(combatPlayer));
        combatPlayer.handleEndCombat();
        removeEnemyAll(combatPlayer);
        combatPlayers.remove(player.getUniqueId());
    }

    private void removeEnemyAll(@NonNull CombatPlayer combatPlayer) {
        for (CombatPlayer player : combatPlayers.values()) {
            player.removeEnemy(combatPlayer);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean inCombat(@NonNull Player player) {
        return combatPlayers.containsKey(player.getUniqueId());
    }

    public void update(@NonNull CombatPlayer combatPlayer) {
        combatPlayers.replace(combatPlayer.getBase().getUniqueId(), combatPlayer);
    }

    public void endAll() {
        for (CombatPlayer combatPlayer : combatPlayers.values()) {
            if (combatPlayer == null) continue;
            combatPlayer.handleEndCombat();
        }
        combatPlayers.clear();
    }

    public enum Result {
        ALREADY_IN_COMBAT,
        SUCCESS;
    }
}
