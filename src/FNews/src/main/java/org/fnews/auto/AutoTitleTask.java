package org.fnews.auto;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AutoTitleTask extends BukkitRunnable {

    public static class TitleData {
        public final String title;
        public final String subtitle;

        public TitleData(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    private final JavaPlugin plugin;
    private final List<TitleData> titles;
    private final Sound sound;
    private final int fadeIn, stay, fadeOut;
    private int index = 0;

    public AutoTitleTask(JavaPlugin plugin,
                         List<TitleData> titles,
                         Sound sound,
                         int fadeIn,
                         int stay,
                         int fadeOut) {

        this.plugin = plugin;
        this.titles = titles;
        this.sound = sound;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void run() {
        TitleData data = titles.get(index);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(data.title, data.subtitle, fadeIn, stay, fadeOut);
            p.playSound(p.getLocation(), sound, 1f, 1f);
        }

        index = (index + 1) % titles.size();
    }
}
