package ru.shirk.antirelog.tools;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.shirk.antirelog.AntiRelog;

import java.util.HashMap;
import java.util.Map;

public class ItemsCooldownTool {

    private final @NonNull Map<Material, Integer> cooldowns = new HashMap<>();

    public ItemsCooldownTool() {
        final ConfigurationSection section = AntiRelog.getConfigurationManager().getConfig("settings.yml").getFile()
                .getConfigurationSection("cooldowns");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            if (key == null) continue;
            final Material material = Material.getMaterial(key);
            if (material == null) continue;
            cooldowns.put(material, section.getInt(key));
        }
    }

    public void handleUseItem(@NonNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getEquipment() == null) return;
        final Material material = player.getEquipment().getItemInMainHand().getType();
        if (!cooldowns.containsKey(material)) return;
        if (player.hasCooldown(material)) {
            event.setCancelled(true);
            return;
        }
        player.setCooldown(material, cooldowns.get(material));
    }
}
