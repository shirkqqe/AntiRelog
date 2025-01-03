package ru.shirk.antirelog.combat;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager implements Listener {

    private final Map<UUID, CombatPlayer> combatPlayers = new HashMap<>();

    public @Nullable CombatPlayer getCombatPlayer(@NonNull Player player) {
        return combatPlayers.get(player.getUniqueId());
    }

    public void startCombatSafe(@NonNull Player initiator, @NonNull Player damaged) {
        CombatPlayer combatInitiator = getCombatPlayer(initiator);
        CombatPlayer combatDamaged = getCombatPlayer(damaged);
        if (combatInitiator == null) {
            combatInitiator = new CombatPlayer(initiator);
            if (combatDamaged != null) combatInitiator.addEnemy(combatDamaged);
        }
        if (combatDamaged == null) {
            combatDamaged = new CombatPlayer(damaged);
            combatDamaged.addEnemy(combatInitiator);
        }
        combatPlayers.putIfAbsent(initiator.getUniqueId(), combatInitiator);
        combatPlayers.putIfAbsent(damaged.getUniqueId(), combatDamaged);
    }

    public @NonNull Result forceStartCombat(@NonNull Player player) {
        CombatPlayer combatPlayer = getCombatPlayer(player);
        if (combatPlayer == null) {
            combatPlayer = new CombatPlayer(player);
            combatPlayers.putIfAbsent(player.getUniqueId(), combatPlayer);
            return Result.SUCCESS;
        }
        return Result.ALREADY_IN_COMBAT;
    }

    public void endCombat(@NonNull Player player) {
        CombatPlayer combatPlayer = getCombatPlayer(player);
        if (combatPlayer == null) return;
        removeEnemyAll(combatPlayer);
        combatPlayers.remove(player.getUniqueId());
    }

    private void removeEnemyAll(@NonNull CombatPlayer combatPlayer) {
        for (CombatPlayer player : combatPlayers.values()) {
            player.removeEnemy(combatPlayer);
        }
    }

    public enum Result {
        ALREADY_IN_COMBAT,
        SUCCESS;
    }
}
