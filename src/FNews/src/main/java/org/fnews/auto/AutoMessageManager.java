package org.fnews.auto;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fnews.util.ColorUtil;

import java.util.*;

public class AutoMessageManager {

    private final JavaPlugin plugin;
    private AutoMessageTask task;

    public AutoMessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        loadAndRun();
    }

    public void reload() {
        stop();
        loadAndRun();
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    private void loadAndRun() {
        FileConfiguration cfg = plugin.getConfig();

        if (!cfg.isConfigurationSection("auto-messages")) return;

        long cooldown = TimeParser.toTicks(
                cfg.getString("auto-messages.cooldown", "10m")
        );

        Sound sound = Sound.valueOf(
                cfg.getString("auto-messages.sound", "ENTITY_EXPERIENCE_ORB_PICKUP")
        );

        List<List<String>> messages = new ArrayList<>();

        for (String key : cfg.getConfigurationSection("auto-messages").getKeys(false)) {
            if (!key.startsWith("message_")) continue;

            List<String> block = new ArrayList<>();
            for (String line : cfg.getStringList("auto-messages." + key)) {
                block.add(ColorUtil.color(line));
            }
            messages.add(block);
        }

        if (!messages.isEmpty()) {
            task = new AutoMessageTask(plugin, messages, sound);
            task.runTaskTimer(plugin, cooldown, cooldown);
        }
    }
}
