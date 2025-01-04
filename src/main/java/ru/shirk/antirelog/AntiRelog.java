package ru.shirk.antirelog;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.shirk.antirelog.combat.CombatManager;
import ru.shirk.antirelog.storage.files.ConfigurationManager;

public final class AntiRelog extends JavaPlugin {

    @Getter
    private static AntiRelog instance;
    @Getter
    private static CombatManager combatManager;
    @Getter
    private static ConfigurationManager configurationManager;

    @Override
    public void onEnable() {
        instance = this;
        combatManager = new CombatManager(this);
        configurationManager = new ConfigurationManager();
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
