package ru.shirk.antirelog.tools;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static @NonNull String colorize(String from) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

        for (Matcher matcher = pattern.matcher(from); matcher.find(); matcher = pattern.matcher(from)) {
            String hexCode = from.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();

            for (char c : ch) {
                builder.append("&").append(c);
            }

            from = from.replace(hexCode, builder.toString());
        }

        return ChatColor.translateAlternateColorCodes('&', from);
    }

    public static boolean isConsumeItem(@NonNull Material material) {
        if (!material.isItem()) return false;
        switch (material) {
            case GOLDEN_APPLE, APPLE, ENDER_PEARL, EGG, GOLDEN_CARROT, CARROT, COOKED_SALMON, COOKED_BEEF,
                 COOKED_CHICKEN,
                 COOKED_COD, COOKED_MUTTON, COOKED_PORKCHOP, COOKED_RABBIT, BAKED_POTATO, POTATO, BEETROOT,
                 BEETROOT_SOUP,
                 BREAD, COD, SUSPICIOUS_STEW, MUSHROOM_STEW, RABBIT_STEW, CHORUS_FRUIT, DRIED_KELP, MELON_SLICE,
                 PUMPKIN_PIE, BEEF, CHICKEN, MUTTON, PORKCHOP, RABBIT, SWEET_BERRIES, COOKIE, HONEY_BOTTLE, PUFFERFISH,
                 ROTTEN_FLESH, SPIDER_EYE, POTION, EXPERIENCE_BOTTLE, POISONOUS_POTATO, ENCHANTED_GOLDEN_APPLE -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
