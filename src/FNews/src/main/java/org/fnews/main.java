package org.fnews;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class main
        extends JavaPlugin {
    private String announcementSound;

    public void onEnable() {
        this.getLogger().info(ChatColor.GREEN + "Running FNews 4.2");
        this.getLogger().info(ChatColor.GREEN + "Developer: wurst1337");
        this.getLogger().info(ChatColor.GREEN + "Running on " + this.getServerType());
        this.setupPlugin();
    }

    private String getServerType() {
        String serverType = this.getServer().getName();
        return serverType;
    }

    public void onDisable() {
        this.getLogger().info(ChatColor.RED + "Stopping FNews 4.2");
        this.getLogger().info(ChatColor.RED + "Developer: wurst1337");
        this.getLogger().info(ChatColor.RED + "Running on " + this.getServerType());
    }

    private void setupPlugin() {
        File configFile;
        File dataFolder = this.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            this.getLogger().warning("Failed to create plugin folder.");
        }
        if (!(configFile = new File(dataFolder, "config.yml")).exists()) {
            this.saveResource("config.yml", false);
        }
        this.reloadConfig();
        this.announcementSound = this.getConfig().getString("announcement-format.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }

    private String getAnnouncementFormat(CommandSender sender, String message) {
        String senderPrefix;
        String top = ChatColor.translateAlternateColorCodes((char) '&', (String) this.getConfig().getString("announcement-format.top", "&b----------------------------------------------------------"));
        String prefix = ChatColor.translateAlternateColorCodes((char) '&', (String) this.getConfig().getString("announcement-format.prefix", "&b[&eBroadcast&b]"));
        String bottom = ChatColor.translateAlternateColorCodes((char) '&', (String) this.getConfig().getString("announcement-format.bottom", "&b----------------------------------------------------------"));
        if (sender instanceof Player) {
            senderPrefix = ChatColor.translateAlternateColorCodes((char) '&', (String) this.getConfig().getString("announcement-format.message", "&7by: %player%"));
            senderPrefix = senderPrefix.replace("%player%", ((Player) sender).getName());
        } else {
            senderPrefix = ChatColor.translateAlternateColorCodes((char) '&', (String) this.getConfig().getString("announcement-format.message", "&7by: Console"));
        }
        return top + "\n" + prefix + " " + message + "\n" + senderPrefix + "\n" + bottom;
    }

    private void sendNewsMessage(CommandSender sender, String[] args) {
        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(ChatColor.translateAlternateColorCodes((char) '&', (String) arg)).append(" ");
        }
        String formattedMessage = this.getAnnouncementFormat(sender, messageBuilder.toString().trim());
        this.getServer().broadcastMessage(formattedMessage);
        for (Player onlinePlayer : this.getServer().getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.valueOf((String) this.announcementSound), 1.0f, 1.0f);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("fnews")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("fnews.use")) {
                    if (args.length > 0) {
                        this.sendNewsMessage((CommandSender) player, args);
                    } else {
                        player.sendMessage(ChatColor.RED + "Please type a /news <text> to use this feature.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please type a /news <text> to use this feature.");
                }
            } else {
                this.sendNewsMessage(sender, args);
            }
            return true;
        }
        if (label.equalsIgnoreCase("fnews-reload")) {
            if (sender.hasPermission("fnews.reload")) {
                this.setupPlugin();
                sender.sendMessage(ChatColor.GREEN + "FNEWS 4.2 and confg reloaded.");
            } else {
                sender.sendMessage(ChatColor.RED + "Error to load config or start plugin.");
            }
            return true;
        }
        return false;
    }
}