package ru.shirk.antirelog.combat;

import lombok.*;
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
    @Setter(AccessLevel.PUBLIC)
    private int time;
    private final @NonNull HashSet<CombatPlayer> enemies = new HashSet<>();

    public void handleStartCombat() {
        time = 30;
        Bukkit.getScheduler().runTaskTimerAsynchronously(AntiRelog.getInstance(), (task) -> {
            if (time > 0) {
                time--;
                return;
            }
            handleEndCombat();
            task.cancel();
        }, 20, 20);
    }

    public void handleEndCombat() {
        time = 0;
    }

    public void addEnemy(@NonNull CombatPlayer combatPlayer) {
        enemies.add(combatPlayer);
    }

    public void removeEnemy(@NonNull CombatPlayer combatPlayer) {
        enemies.remove(combatPlayer);
    }

    public @NonNull List<String> buildEnemies() {
        final List<String> build = new ArrayList<>();
        for (CombatPlayer player : enemies) {
            build.add(player.base.getName());
        }
        return build;
    }
}
