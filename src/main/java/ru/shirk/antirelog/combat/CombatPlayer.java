package ru.shirk.antirelog.combat;

import lombok.*;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.shirk.antirelog.AntiRelog;
import ru.shirk.antirelog.modules.ModuleManager;
import ru.shirk.antirelog.storage.files.Configuration;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CombatPlayer {

    private final @NonNull Player base;
    @Setter(AccessLevel.PUBLIC)
    private int time;
    private final @NonNull ArrayList<CombatPlayer> enemies = new ArrayList<>();
    private final @NonNull ModuleManager moduleManager = AntiRelog.getModuleManager();
    private @Nullable BossBar bossBar;

    public void handleStartCombat() {
        time = 30;
        Bukkit.getScheduler().runTaskTimerAsynchronously(AntiRelog.getInstance(), (task) -> {
            if (time > 0) {
                showModules();
                time--;
                return;
            }
            handleEndCombat();
            task.cancel();
        }, 20, 20);
    }

    public void handleEndCombat() {
        time = 0;
        clearModules();
    }

    public void addEnemy(@NonNull CombatPlayer combatPlayer) {
        if (enemies.contains(combatPlayer)) return;
        enemies.add(combatPlayer);
    }

    public void removeEnemy(@NonNull CombatPlayer combatPlayer) {
        enemies.remove(combatPlayer);
    }

    @SuppressWarnings("deprecation")
    private void showModules() {
        if (moduleManager.getActionBarModule().isEnabled()) {
            base.sendActionBar(moduleManager.getActionBarModule().getText());
        }

        if (moduleManager.getBossBarModule().isEnabled()) {
            if (bossBar == null) {
                bossBar = Bukkit.createBossBar(
                        moduleManager.getBossBarModule().getTitle().replace("{time}", String.valueOf(time)),
                        moduleManager.getBossBarModule().getColor(),
                        moduleManager.getBossBarModule().getStyle()
                );
            }
            bossBar.setVisible(true);
            bossBar.addPlayer(base);
            bossBar.setProgress((double) time / 30);
        }

        if (moduleManager.getScoreboardModule().isEnabled()) {
            final TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(base.getUniqueId());
            if (tabPlayer == null) return;

            final ScoreboardManager scoreboardManager = TabAPI.getInstance().getScoreboardManager();
            if (scoreboardManager == null) return;

            final Scoreboard scoreboard = scoreboardManager.createScoreboard(
                    base.getName(),
                    moduleManager.getScoreboardModule().getTitle(),
                    moduleManager.getScoreboardModule().getLines().stream().map(
                            line -> line.replace("{enemies}", String.join("\n", buildEnemies()))
                    ).toList()
            );
            scoreboardManager.showScoreboard(tabPlayer, scoreboard);
        }
    }

    private void clearModules() {
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(true);
            bossBar = null;
        }

        final TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(base.getUniqueId());
        if (tabPlayer == null) return;
        final ScoreboardManager scoreboardManager = TabAPI.getInstance().getScoreboardManager();
        if (scoreboardManager == null) return;
        scoreboardManager.resetScoreboard(tabPlayer);
    }

    public @NonNull List<String> buildEnemies() {
        final Configuration configuration = AntiRelog.getConfigurationManager().getConfig("settings.yml");
        if (enemies.isEmpty()) return List.of(configuration.c("noEnemies"));
        final List<String> build = new ArrayList<>();
        for (int i = 0; i < enemies.size(); i++) {
            final CombatPlayer combatPlayer = enemies.get(i);
            if (combatPlayer == null) continue;
            if (i == enemies.size() - 1) {
                build.add(configuration.c("lastEnemy")
                        .replace("{player}", combatPlayer.base.getName())
                        .replace("{ping}", String.valueOf(combatPlayer.base.getPing()))
                        .replace("{health}", String.valueOf((int) combatPlayer.base.getHealth()))
                );
                continue;
            }
            build.add(configuration.c("enemy")
                    .replace("{player}", combatPlayer.base.getName())
                    .replace("{ping}", String.valueOf(combatPlayer.base.getPing()))
                    .replace("{health}", String.valueOf((int) combatPlayer.base.getHealth()))
            );
        }
        return build;
    }
}
