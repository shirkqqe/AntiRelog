package ru.shirk.antirelog;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.shirk.antirelog.combat.CombatManager;

public final class AntiRelog extends JavaPlugin {

    @Getter
    private static AntiRelog instance;
    @Getter
    private static CombatManager combatManager;

    @Override
    public void onEnable() {
        instance = this;
        combatManager = new CombatManager(this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
