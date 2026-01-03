package org.fnews.auto;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.fnews.util.LinkUtil;

import java.util.List;

public class AutoMessageTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final List<List<String>> messages;
    private final Sound sound;
    private int index = 0;

    public AutoMessageTask(JavaPlugin plugin, List<List<String>> messages, Sound sound) {
        this.plugin = plugin;
        this.messages = messages;
        this.sound = sound;
    }

    @Override
    public void run() {
        List<String> msg = messages.get(index);

        for (String line : msg) {
            Bukkit.spigot().broadcast(LinkUtil.parse(line));
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, 1f, 1f);
        }

        index = (index + 1) % messages.size();
    }
}
