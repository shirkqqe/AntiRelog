package ru.shirk.antirelog.storage.files;

import ru.shirk.antirelog.AntiRelog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;

public class ConfigurationManager {

    private final HashMap<String, Configuration> configs = new HashMap<>();

    public Configuration getConfig(String name) {
        if (this.configs.containsKey(name)) {
            return this.configs.get(name);
        }
        Configuration config = new Configuration(AntiRelog.getInstance(), name);
        this.configs.put(name, config);
        return config;
    }

    public void reloadConfigs() {
        for (Configuration c : configs.values()) c.reload();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void createFile(String name) {
        InputStream is = null;
        OutputStream os = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            is = classLoader.getResourceAsStream(name.replace('\\', '/'));


            if (is == null) {
                AntiRelog.getInstance().getSLF4JLogger().warn("FAILED TO CREATE PLUGIN CONFIG! [No source file in the plugin jar]");
            } else {
                AntiRelog.getInstance().getDataFolder().mkdirs();
                os = Files.newOutputStream(new File(AntiRelog.getInstance().getDataFolder(), name).toPath());
                byte[] buffer = new byte[99];
                int length = 0;
                length = is.read(buffer);
                while (length > 0) {
                    os.write(buffer, 0, length);
                    length = is.read(buffer);
                }
            }
        } catch (IOException ex) {
            AntiRelog.getInstance().getSLF4JLogger().warn("FAILED TO CREATE PLUGIN CONFIG! [IOException 1]");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                AntiRelog.getInstance().getSLF4JLogger().warn("FAILED TO CREATE PLUGIN CONFIG! [IOException 2]");
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                AntiRelog.getInstance().getSLF4JLogger().warn("FAILED TO CREATE PLUGIN CONFIG! [IOException 3]");
            }
        }
    }
}
