package ru.shirk.antirelog.modules;

import lombok.Getter;
import lombok.NonNull;
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
    @Getter
    private final @NonNull TitleModule titleModule;

    public ModuleManager(@NonNull ConfigurationManager configurationManager) {
        this.configuration = configurationManager.getConfig("settings.yml");
        final ConfigurationSection scoreboardSection = configuration.getFile().getConfigurationSection("scoreboard");
        final ConfigurationSection bossBarSection = configuration.getFile().getConfigurationSection("bossbar");
        final ConfigurationSection actionbarSection = configuration.getFile().getConfigurationSection("actionbar");
        final ConfigurationSection titleSection = configuration.getFile().getConfigurationSection("title");

        if (scoreboardSection == null) {
            throw new IllegalStateException("scoreboard section is null, please reset settings.yml");
        }
        if (bossBarSection == null) {
            throw new IllegalStateException("bossbar section is null, please reset settings.yml");
        }
        if (actionbarSection == null) {
            throw new IllegalStateException("actionbar section is null, please reset settings.yml");
        }
        if (titleSection == null) {
            throw new IllegalStateException("title section is null, please reset settings.yml");
        }

        scoreboardModule = new ScoreboardModule(scoreboardSection);
        bossBarModule = new BossBarModule(bossBarSection);
        actionBarModule = new ActionBarModule(actionbarSection);
        titleModule = new TitleModule(titleSection);
    }
}
