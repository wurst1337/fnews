package org.fnews;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fnews.auto.AutoMessageManager;
import org.fnews.auto.AutoTitleManager;
import org.fnews.timer.TimerManager;


public class FNews extends JavaPlugin {

    private String announcementSound;
    private AutoMessageManager autoMessageManager;
    private AutoTitleManager autoTitleManager;
    private TimerManager timerManager;

    @Override
    public void onEnable() {

        getLogger().info("Running FNews 5.0.b");

        saveDefaultConfig();

        autoMessageManager = new AutoMessageManager(this);
        autoTitleManager = new AutoTitleManager(this);
        timerManager = new TimerManager(this);

        reloadInternal();

        autoMessageManager.start();
        autoTitleManager.start();

    }



    @Override
    public void onDisable() {
        if (autoMessageManager != null) autoMessageManager.stop();

        getLogger().info(ChatColor.GREEN + "Running FNews 5.0.b");
        getLogger().info(ChatColor.GREEN + "Developer: MCFireStudio Team");
        if (autoTitleManager != null) autoTitleManager.stop();
    }

    private void reloadInternal() {
        reloadConfig();
        announcementSound = getConfig().getString(
                "announcement-format.sound",
                "ENTITY_EXPERIENCE_ORB_PICKUP"
        );
    }

    private String getAnnouncementFormat(CommandSender sender, String message) {
        String top = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("announcement-format.top"));
        String prefix = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("announcement-format.prefix"));
        String bottom = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("announcement-format.bottom"));

        String by;
        if (sender instanceof Player) {
            by = getConfig().getString("announcement-format.message")
                    .replace("%player%", sender.getName());
        } else {
            by = getConfig().getString("announcement-format.message")
                    .replace("%player%", "Console");
        }
        by = ChatColor.translateAlternateColorCodes('&', by);

        return top + "\n" + prefix + " " + message + "\n" + by + "\n" + bottom;
    }

    private void sendNewsMessage(CommandSender sender, String[] args) {
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(ChatColor.translateAlternateColorCodes('&', s)).append(" ");
        }

        String formatted = getAnnouncementFormat(sender, msg.toString().trim());
        Bukkit.broadcastMessage(formatted);

        Sound sound = Sound.valueOf(announcementSound);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, 1f, 1f);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (label.equalsIgnoreCase("fnews")) {
            if (!(sender instanceof Player) || sender.hasPermission("fnews.use")) {
                if (args.length > 0) {
                    sendNewsMessage(sender, args);
                } else {
                    sender.sendMessage(ChatColor.RED + "Use: /fnews <text>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "No permission.");
            }
            return true;
        }

        if (label.equalsIgnoreCase("fnews-reload")) {
            if (sender.hasPermission("fnews.reload")) {

                reloadInternal();

                autoMessageManager.reload();
                autoTitleManager.reload();

                sender.sendMessage(ChatColor.GREEN + "FNews reloaded.");
            }
            return true;
        }

        if (label.equalsIgnoreCase("timer")) {
            if (!sender.hasPermission("fnews.timer")) return true;

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Please, use /timer <time>, example: /timer 5m");
                return true;
            }

            long seconds = parseTime(args[0]);
            if (seconds <= 0) {
                sender.sendMessage(ChatColor.RED + "Error: Unknown time format");
                return true;
            }

            if (!timerManager.startTimer(seconds)) {
                sender.sendMessage(ChatColor.RED + "Timer is alredy started!");
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Timer on " + args[0] + " is started!");
            return true;
        }

        if (label.equalsIgnoreCase("timer") && args.length == 1 && args[0].equalsIgnoreCase("pause")) {
            if (timerManager.pauseTimer()) sender.sendMessage(ChatColor.YELLOW + "Timer on pause!");
            else sender.sendMessage(ChatColor.RED + "We don't found any started timers!");
            return true;
        }

        if (label.equalsIgnoreCase("timer") && args.length == 1 && args[0].equalsIgnoreCase("resume")) {
            if (timerManager.resumeTimer()) sender.sendMessage(ChatColor.GREEN + "Timer is resumed!");
            else sender.sendMessage(ChatColor.RED + "We don't found any started timers!");
            return true;
        }

        if (label.equalsIgnoreCase("timer") && args.length == 1 && args[0].equalsIgnoreCase("stop")) {
            if (timerManager.stopTimer()) sender.sendMessage(ChatColor.RED + "Timer is stopped!");
            else sender.sendMessage(ChatColor.RED + "We don't found any started timers!");
            return true;
        }


        return false;
    }

    private long parseTime(String input) {
        input = input.toLowerCase();
        try {
            if (input.endsWith("s")) return Long.parseLong(input.replace("s", ""));
            if (input.endsWith("m")) return Long.parseLong(input.replace("m", "")) * 60;
            if (input.endsWith("h")) return Long.parseLong(input.replace("h", "")) * 3600;
            return Long.parseLong(input); // по умолчанию секунды
        } catch (Exception e) {
            return -1;
        }
    }
}
