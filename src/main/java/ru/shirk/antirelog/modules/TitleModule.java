package ru.shirk.antirelog.modules;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class TitleModule {

    private final boolean enabled;
    private final @NonNull String startTitle;
    private final @NonNull String startSubTitle;
    private final @NonNull String endTitle;
    private final @NonNull String endSubTitle;

    public TitleModule(@NonNull ConfigurationSection section) {
        this.enabled = section.getBoolean("enabled");
        final String startTitle = section.getString("startTitle");
        final String endTitle = section.getString("endTitle");
        if (startTitle == null || endTitle == null)
            throw new RuntimeException("title section is invalid, please reset settings.yml");
        this.startTitle = startTitle.split("\\^")[0];
        this.startSubTitle = startTitle.split("\\^")[1] == null ? "" : startTitle.split("\\^")[1];
        this.endTitle = endTitle.split("\\^")[0];
        this.endSubTitle = endTitle.split("\\^")[1] == null ? "" : endTitle.split("\\^")[1];
    }
}
