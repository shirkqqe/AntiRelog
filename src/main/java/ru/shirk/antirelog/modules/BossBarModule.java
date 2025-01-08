package ru.shirk.antirelog.modules;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class BossBarModule {

    private final boolean enabled;
    private final @NonNull String title;
    private final @NonNull BarColor color;
    private final @NonNull BarStyle style;

    public BossBarModule(@NonNull ConfigurationSection section) {
        this.enabled = section.getBoolean("enabled");
        final String titleString = section.getString("title");
        final String colorString = section.getString("color");
        final String styleString = section.getString("style");
        if (titleString == null || colorString == null || styleString == null)
            throw new RuntimeException("bossbar section is invalid, please reset settings.yml");
        this.title = titleString;
        this.color = BarColor.valueOf(colorString);
        this.style = BarStyle.valueOf(styleString);
    }
}
