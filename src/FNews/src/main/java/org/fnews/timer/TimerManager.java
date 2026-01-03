package org.fnews.timer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerManager {

    private final JavaPlugin plugin;

    public TimerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Запустить таймер на указанное количество секунд
    public void startTimer(long seconds) {
        TimerTask task = new TimerTask(seconds);
        task.runTaskTimer(plugin, 0, 20); // тик = 1 секунда
    }

    // Внутренний класс для задачи таймера
    private class TimerTask extends BukkitRunnable {

        private long remaining;
        private final BossBar bar;
        private final Sound alertSound;
        private final long alertSeconds;

        public TimerTask(long seconds) {
            this.remaining = seconds;

            // --- Конфиг ---
            String colorStr = plugin.getConfig().getString("timer-bar.color", "GREEN");
            String styleStr = plugin.getConfig().getString("timer-bar.style", "SOLID");
            String soundStr = plugin.getConfig().getString("timer-bar.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
            this.alertSeconds = plugin.getConfig().getLong("timer-bar.alert-seconds", 5);

            // Цвет и стиль боссбара
            BarColor color;
            BarStyle style;
            try { color = BarColor.valueOf(colorStr.toUpperCase()); } catch (Exception e) { color = BarColor.GREEN; }
            try { style = BarStyle.valueOf(styleStr.toUpperCase()); } catch (Exception e) { style = BarStyle.SOLID; }

            // Звук alert (без ошибки присвоения)
            Sound tempSound;
            try { tempSound = Sound.valueOf(soundStr.toUpperCase()); } catch (Exception e) { tempSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP; }
            alertSound = tempSound;

            // Создание боссбара
            bar = Bukkit.createBossBar("Таймер: " + remaining + " сек", color, style);
            bar.setProgress(1.0);

            // Добавляем всех онлайн игроков
            for (Player p : Bukkit.getOnlinePlayers()) bar.addPlayer(p);
        }

        @Override
        public void run() {
            if (remaining <= 0) {
                bar.removeAll();
                this.cancel();
                return;
            }

            // Прогресс боссбара
            double progress = (double) remaining / (double) (remaining + 1);
            bar.setProgress(progress);
            bar.setTitle("Таймер: " + remaining + " сек");

            // Если осталось alertSeconds или меньше — проигрываем звук
            if (remaining <= alertSeconds) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), alertSound, 1f, 1f);
                }
            }

            remaining--;
        }
    }
}
