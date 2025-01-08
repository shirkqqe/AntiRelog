package ru.shirk.antirelog.modules;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class ActionBarModule {

    private final boolean enabled;
    private final @NonNull String text;

    public ActionBarModule(@NonNull ConfigurationSection section) {
        this.enabled = section.getBoolean("enabled");
        final String textString = section.getString("text");
        if (textString == null) throw new RuntimeException("actionbar section is invalid, please reset settings.yml");
        this.text = textString;
    }
}
