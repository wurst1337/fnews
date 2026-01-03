package org.fnews.auto;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fnews.util.ColorUtil;

import java.util.*;

public class AutoTitleManager {

    private final JavaPlugin plugin;
    private AutoTitleTask task;

    public AutoTitleManager(JavaPlugin plugin) {
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
        if (!cfg.isConfigurationSection("auto-titles")) return;

        long cooldown = TimeParser.toTicks(
                cfg.getString("auto-titles.cooldown", "5m")
        );

        Sound sound = Sound.valueOf(
                cfg.getString("auto-titles.sound", "ENTITY_PLAYER_LEVELUP")
        );

        int fadeIn = cfg.getInt("auto-titles.fade-in", 10);
        int stay = cfg.getInt("auto-titles.stay", 40);
        int fadeOut = cfg.getInt("auto-titles.fade-out", 10);

        List<AutoTitleTask.TitleData> titles = new ArrayList<>();

        for (String key : cfg.getConfigurationSection("auto-titles").getKeys(false)) {
            if (!key.startsWith("title_")) continue;

            String title = ColorUtil.color(
                    cfg.getString("auto-titles." + key + ".title", "")
            );
            String subtitle = ColorUtil.color(
                    cfg.getString("auto-titles." + key + ".subtitle", "")
            );

            titles.add(new AutoTitleTask.TitleData(title, subtitle));
        }

        if (!titles.isEmpty()) {
            task = new AutoTitleTask(
                    plugin, titles, sound, fadeIn, stay, fadeOut
            );
            task.runTaskTimer(plugin, cooldown, cooldown);
        }
    }
}
