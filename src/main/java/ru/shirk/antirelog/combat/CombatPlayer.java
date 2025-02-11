package ru.shirk.antirelog.combat;

import lombok.*;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import ru.shirk.antirelog.AntiRelog;
import ru.shirk.antirelog.combat.cooldowns.CooldownItem;
import ru.shirk.antirelog.listeners.api.CombatTickEvent;
import ru.shirk.antirelog.modules.ModuleManager;
import ru.shirk.antirelog.tools.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CombatPlayer {

    private final @NonNull Player base;
    @Setter(AccessLevel.PUBLIC)
    private int time = 0;
    private final @NonNull ArrayList<CombatPlayer> enemies = new ArrayList<>();
    private final @NonNull ModuleManager moduleManager = AntiRelog.getModuleManager();
    private @Nullable BossBar bossBar;
    private @Nullable BukkitTask task;
    private final @NonNull ArrayList<CooldownItem> cooldownItems = new ArrayList<>();

    public void handleStartCombat() {
        if (time == 0) {
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
        }
        time = AntiRelog.getConfigurationManager().getConfig("settings.yml").ch("combatTime");
        if (task == null) {
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
            return;
        }
        showModules();
    }

    @SuppressWarnings("deprecation")
    public void handleEndCombat() {
        if (task != null) task.cancel();
        time = 0;
        clearModules();
        AntiRelog.getConfigurationManager().getConfig("settings.yml").sendMessage(base,
                "messages.endCombat");
        if (moduleManager.getActionBarModule().isEnabled()) {
            base.sendActionBar(Utils.colorize(moduleManager.getActionBarModule().getEndText()));
        }
    }

    public void setCooldownItem(@NonNull CooldownItem cooldownItem) {
        if (hasCooldown(cooldownItem.material())) {
            removeCooldownItem(cooldownItem.material());
        }
        cooldownItems.add(cooldownItem);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasCooldown(@NonNull Material material) {
        if (cooldownItems.isEmpty()) return false;
        for (CooldownItem cooldownItem : cooldownItems) {
            if (!cooldownItem.material().equals(material)) continue;
            return true;
        }
        return false;
    }

    public int getCooldown(@NonNull Material material) {
        if (cooldownItems.isEmpty()) return -1;
        for (CooldownItem cooldownItem : cooldownItems) {
            if (!cooldownItem.material().equals(material)) continue;
            return (int) Duration.between(LocalDateTime.now(), cooldownItem.endDate()).getSeconds();
        }
        return -1;
    }

    public void removeCooldownItem(@NonNull Material material) {
        if (cooldownItems.isEmpty()) return;
        final Iterator<CooldownItem> iterator = cooldownItems.iterator();
        while (iterator.hasNext()) {
            final CooldownItem cooldownItem = iterator.next();
            if (!cooldownItem.material().equals(material)) continue;
            iterator.remove();
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
                    buildLinesWithEnemies()
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

    private List<String> buildLinesWithEnemies() {
        List<String> lines = new ArrayList<>(moduleManager.getScoreboardModule().getLines().stream().map(line -> line
                .replace("{time}", String.valueOf(time))
                .replace("{player}", base.getName())
                .replace("{ping}", String.valueOf(base.getPing()))
        ).toList());

        int enemiesIndex = lines.indexOf("{enemies}");
        if (enemiesIndex == -1) return lines;
        if (enemies.isEmpty()) {
            lines.remove(enemiesIndex);
            lines.add(enemiesIndex, AntiRelog.getConfigurationManager().getConfig("settings.yml")
                    .c("noEnemies"));
            return lines;
        }
        final ArrayList<String> enemiesList = getSortedEnemyList();
        lines.remove(enemiesIndex);
        lines.addAll(enemiesIndex, enemiesList);
        return lines;
    }

    private @NonNull ArrayList<String> getSortedEnemyList() {
        final CombatPlayer[] enemies = this.enemies.toArray(new CombatPlayer[0]);
        final ArrayList<String> enemiesLines = new ArrayList<>();
        if (this.enemies.size() == 1) {
            for (CombatPlayer enemy : enemies) {
                if (enemy == null) continue;
                enemiesLines.add(AntiRelog.getConfigurationManager().getConfig("settings.yml")
                        .c("lastEnemy")
                        .replace("{player}", enemy.getBase().getName())
                        .replace("{ping}", String.valueOf(enemy.getBase().getPing()))
                        .replace("{health}", String.valueOf((int) enemy.getBase().getHealth()))
                );
            }
            return enemiesLines;
        }
        for (int i = 0; i < this.enemies.size(); i++) {
            final CombatPlayer combatPlayer = enemies[i];
            if (combatPlayer == null) continue;
            if (i == this.enemies.size() - 1) {
                enemiesLines.add(AntiRelog.getConfigurationManager().getConfig("settings.yml")
                        .c("lastEnemy")
                        .replace("{player}", combatPlayer.getBase().getName())
                        .replace("{ping}", String.valueOf(combatPlayer.getBase().getPing()))
                        .replace("{health}", String.valueOf((int) combatPlayer.getBase().getHealth()))
                );
                continue;
            }
            enemiesLines.add(AntiRelog.getConfigurationManager().getConfig("settings.yml")
                    .c("enemy")
                    .replace("{player}", combatPlayer.getBase().getName())
                    .replace("{ping}", String.valueOf(combatPlayer.getBase().getPing()))
                    .replace("{health}", String.valueOf((int) combatPlayer.getBase().getHealth())));
        }
        return enemiesLines;
    }
}
