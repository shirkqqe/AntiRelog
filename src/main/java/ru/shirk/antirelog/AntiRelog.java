package ru.shirk.antirelog;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiRelog extends JavaPlugin {

    @Getter
    private static AntiRelog instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
