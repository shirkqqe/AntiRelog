package ru.shirk.antirelog.combat;

import lombok.*;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import ru.shirk.antirelog.AntiRelog;
import ru.shirk.antirelog.listeners.api.CombatTickEvent;
import ru.shirk.antirelog.modules.ModuleManager;
import ru.shirk.antirelog.modules.cooldowns.CooldownItem;
import ru.shirk.antirelog.storage.files.Configuration;
import ru.shirk.antirelog.tools.Utils;

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
    private @Nullable BukkitTask task;
    private final @NonNull ArrayList<CooldownItem> cooldownItems = new ArrayList<>();

    public void handleStartCombat() {
        time = AntiRelog.getConfigurationManager().getConfig("settings.yml").ch("combatTime");
        AntiRelog.getConfigurationManager().getConfig("settings.yml").sendMessage(base,
                "messages.startCombat");
        if (moduleManager.getTitleModule().isEnabled()) {
            base.sendTitle(
                    Utils.colorize(moduleManager.getTitleModule().getStartTitle()),
                    Utils.colorize(moduleManager.getTitleModule().getStartSubTitle()),
                    10,
                    20,
                    10
            );
        }
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(AntiRelog.getInstance(), () -> {
            if (time > 0) {
                final CombatTickEvent event = new CombatTickEvent(time, time - 1, this);
                Bukkit.getPluginManager().callEvent(event);
                showModules();
                time--;
                return;
            }
            AntiRelog.getCombatManager().endCombat(base);
        }, 0, 20);
    }

    @SuppressWarnings("deprecation")
    public void handleEndCombat() {
        base.resetCooldown();
        if (task != null) task.cancel();
        time = 0;
        clearModules();
        AntiRelog.getConfigurationManager().getConfig("settings.yml").sendMessage(base,
                "messages.endCombat");
        if (moduleManager.getActionBarModule().isEnabled()) {
            base.sendActionBar(Utils.colorize(moduleManager.getActionBarModule().getEndText()));
        }
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
            base.sendActionBar(Utils.colorize(moduleManager.getActionBarModule().getText().replace("{time}",
                    String.valueOf(time))));
        }

        if (moduleManager.getBossBarModule().isEnabled()) {
            if (bossBar == null) {
                bossBar = Bukkit.createBossBar(
                        Utils.colorize(moduleManager.getBossBarModule().getTitle().replace("{time}",
                                String.valueOf(time))),
                        moduleManager.getBossBarModule().getColor(),
                        moduleManager.getBossBarModule().getStyle()
                );
            }
            bossBar.setTitle(Utils.colorize(moduleManager.getBossBarModule().getTitle().replace("{time}",
                    String.valueOf(time))));
            bossBar.setVisible(true);
            bossBar.addPlayer(base);
            bossBar.setProgress((double) time / AntiRelog.getConfigurationManager().getConfig("settings.yml")
                    .ch("combatTime"));
        }

        if (moduleManager.getScoreboardModule().isEnabled()) {
            final TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(base.getUniqueId());
            if (tabPlayer == null) return;

            final ScoreboardManager scoreboardManager = TabAPI.getInstance().getScoreboardManager();
            if (scoreboardManager == null) return;

            final Scoreboard scoreboard = scoreboardManager.createScoreboard(
                    base.getName(),
                    moduleManager.getScoreboardModule().getTitle(),
                    moduleManager.getScoreboardModule().getLines().stream().map(line -> line
                            .replace("{enemies}", String.join("\n", buildEnemies()))
                            .replace("{time}", String.valueOf(time))
                            .replace("{player}", base.getName())
                            .replace("{ping}", String.valueOf(base.getPing()))
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

        if (moduleManager.getTitleModule().isEnabled()) {
            base.sendTitle(
                    Utils.colorize(moduleManager.getTitleModule().getEndTitle()),
                    Utils.colorize(moduleManager.getTitleModule().getEndSubTitle()),
                    10,
                    20,
                    10
            );
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
