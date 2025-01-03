package ru.shirk.antirelog.combat;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.shirk.antirelog.AntiRelog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CombatPlayer {

    private final @NonNull Player base;
    private int time;
    private final @NonNull HashSet<CombatPlayer> enemies = new HashSet<>();

    public void handleStartCombat() {
        time = 30;
        Bukkit.getScheduler().runTaskTimerAsynchronously(AntiRelog.getInstance(), () -> {
            time--;
        }, 20, 20);
    }

    public void addEnemy(@NonNull CombatPlayer combatPlayer) {
        enemies.add(combatPlayer);
    }

    public void removeEnemy(@NonNull CombatPlayer combatPlayer) {
        enemies.remove(combatPlayer);
    }

    public @NonNull List<String> buildEnemies() {
        final List<String> builded = new ArrayList<>();
        for (CombatPlayer player : enemies) {
            builded.add(player.base.getName());
        }
        return builded;
    }
}
