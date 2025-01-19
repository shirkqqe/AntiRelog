package ru.shirk.antirelog;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.shirk.antirelog.combat.CombatManager;
import ru.shirk.antirelog.commands.Commands;
import ru.shirk.antirelog.listeners.BukkitListeners;
import ru.shirk.antirelog.modules.ModuleManager;
import ru.shirk.antirelog.storage.files.ConfigurationManager;

import java.io.File;
import java.util.Objects;

public final class AntiRelog extends JavaPlugin {

    @Getter
    private static AntiRelog instance;
    @Getter
    private static CombatManager combatManager;
    @Getter
    private static ConfigurationManager configurationManager;
    @Getter
    private static ModuleManager moduleManager;

    @Override
    public void onEnable() {
        if (!this.getServer().getPluginManager().isPluginEnabled("TAB")) {
            this.getLogger().severe("Плагин не может работать без TAB, " +
                    "пожалуйста установите этот плагин для корректной работы.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        instance = this;
        combatManager = new CombatManager();
        configurationManager = new ConfigurationManager();
        loadConfigs();
        moduleManager = new ModuleManager(configurationManager);
        Objects.requireNonNull(this.getServer().getPluginCommand("antirelog")).setExecutor(new Commands(combatManager));
        Objects.requireNonNull(this.getServer().getPluginCommand("antirelog")).setTabCompleter(new Commands(combatManager));
        Bukkit.getPluginManager().registerEvents(new BukkitListeners(combatManager), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void loadConfigs() {
        try {
            if (!(new File(getDataFolder(), "settings.yml")).exists()) {
                getConfigurationManager().createFile("settings.yml");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
