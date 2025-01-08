package ru.shirk.antirelog.modules;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@Getter
public class ScoreboardModule {

    private final boolean enabled;
    private final @NonNull String title;
    private final @NonNull List<String> lines;

    public ScoreboardModule(@NonNull ConfigurationSection section) {
        this.enabled = section.getBoolean("enabled");
        final String titleString = section.getString("title");
        if (titleString == null) throw new RuntimeException("scoreboard section is invalid, please reset settings.yml");
        this.title = titleString;
        this.lines = section.getStringList("board");
    }
}
