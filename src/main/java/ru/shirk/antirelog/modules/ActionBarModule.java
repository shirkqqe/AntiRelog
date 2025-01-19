package ru.shirk.antirelog.modules;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class ActionBarModule {

    private final boolean enabled;
    private final @NonNull String text;
    private final @NonNull String endText;

    public ActionBarModule(@NonNull ConfigurationSection section) {
        this.enabled = section.getBoolean("enabled");
        final String textString = section.getString("text");
        final String endTextString = section.getString("endText");
        if (textString == null || endTextString == null)
            throw new RuntimeException("actionbar section is invalid, please reset settings.yml");
        this.text = textString;
        this.endText = endTextString;
    }
}
