package org.fnews.timer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class TimerManager {

    private final JavaPlugin plugin;

    private TimerTask activeTimer = null;
    private final Set<Player> trackedPlayers = new HashSet<>();

    public TimerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean startTimer(long seconds) {
        if (activeTimer != null) return false;

        activeTimer = new TimerTask(seconds);
        activeTimer.runTaskTimer(plugin, 0, 20);
        return true;
    }

    public boolean pauseTimer() {
        if (activeTimer == null || activeTimer.paused) return false;
        activeTimer.paused = true;
        return true;
    }

    public boolean resumeTimer() {
        if (activeTimer == null || !activeTimer.paused) return false;
        activeTimer.paused = false;
        return true;
    }

    public boolean stopTimer() {
        if (activeTimer == null) return false;
        activeTimer.cancel();
        activeTimer.bar.removeAll();
        activeTimer = null;
        return true;
    }

    public void addPlayer(Player player) {
        trackedPlayers.add(player);
        if (activeTimer != null) activeTimer.addPlayer(player);
    }

    private class TimerTask extends BukkitRunnable {

        private long remaining;
        private final BossBar bar;
        private final Sound alertSound;
        private final long alertSeconds;
        private boolean paused = false;

        public TimerTask(long seconds) {
            this.remaining = seconds;

            String colorStr = plugin.getConfig().getString("timer-bar.color", "GREEN");
            String styleStr = plugin.getConfig().getString("timer-bar.style", "SOLID");
            String soundStr = plugin.getConfig().getString("timer-bar.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
            this.alertSeconds = plugin.getConfig().getLong("timer-bar.alert-seconds", 5);

            BarColor color;
            BarStyle style;
            try { color = BarColor.valueOf(colorStr.toUpperCase()); } catch (Exception e) { color = BarColor.GREEN; }
            try { style = BarStyle.valueOf(styleStr.toUpperCase()); } catch (Exception e) { style = BarStyle.SOLID; }

            Sound tempSound;
            try { tempSound = Sound.valueOf(soundStr.toUpperCase()); } catch (Exception e) { tempSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP; }
            alertSound = tempSound;

            bar = Bukkit.createBossBar(formatTime(remaining), color, style);
            bar.setProgress(1.0);

            for (Player p : Bukkit.getOnlinePlayers()) {
                bar.addPlayer(p);
                trackedPlayers.add(p); // чтобы потом новые игроки тоже получили
            }

            for (Player p : trackedPlayers) bar.addPlayer(p);
        }

        public void addPlayer(Player player) {
            bar.addPlayer(player);
            if (activeTimer != null) activeTimer.bar.addPlayer(player);
        }

        @Override
        public void run() {
            if (paused) return;

            if (remaining <= 0) {
                bar.removeAll();
                activeTimer = null;
                this.cancel();
                return;
            }

            double progress = (double) remaining / (double) (remaining + 1);
            bar.setProgress(progress);
            bar.setTitle(formatTime(remaining));

            if (remaining <= alertSeconds) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), alertSound, 1f, 1f);
                }
            }

            remaining--;
        }
    }

    private String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append(hours == 1 ? " hour " : " hours ");
        if (minutes > 0) sb.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        sb.append(seconds).append(seconds == 1 ? " second" : " seconds");
        return sb.toString();
    }
}
