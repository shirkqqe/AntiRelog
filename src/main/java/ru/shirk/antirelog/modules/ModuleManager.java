package ru.shirk.antirelog.modules;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import ru.shirk.antirelog.storage.files.Configuration;
import ru.shirk.antirelog.storage.files.ConfigurationManager;

public class ModuleManager {

    private final @NonNull Configuration configuration;
    @Getter
    private final @NonNull ScoreboardModule scoreboardModule;
    @Getter
    private final @NonNull BossBarModule bossBarModule;
    @Getter
    private final @NonNull ActionBarModule actionBarModule;

    public ModuleManager(@NonNull ConfigurationManager configurationManager) {
        this.configuration = configurationManager.getConfig("settings.yml");
        final ConfigurationSection scoreboardSection = configuration.getFile().getConfigurationSection("scoreboard");
        final ConfigurationSection bossBarSection = configuration.getFile().getConfigurationSection("bossbar");
        final ConfigurationSection actionbarSection = configuration.getFile().getConfigurationSection("actionbar");

        if (scoreboardSection == null) {
            throw new IllegalStateException("scoreboard section is null, please reset settings.yml");
        }
        if (bossBarSection == null) {
            throw new IllegalStateException("bossbar section is null, please reset settings.yml");
        }
        if (actionbarSection == null) {
            throw new IllegalStateException("actionbar section is null, please reset settings.yml");
        }

        scoreboardModule = new ScoreboardModule(scoreboardSection);
        bossBarModule = new BossBarModule(bossBarSection);
        actionBarModule = new ActionBarModule(actionbarSection);
    }
}
