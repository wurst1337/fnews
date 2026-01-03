package org.fnews.util;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.ChatColor;

public class LinkUtil {

    public static BaseComponent[] parse(String text) {

        TextComponent component = new TextComponent();
        component.setColor(ChatColor.WHITE);

        String[] parts = text.split(" ");

        for (String part : parts) {
            if (part.startsWith("http://") || part.startsWith("https://")) {

                TextComponent link = new TextComponent(part + " ");
                link.setColor(ChatColor.AQUA);
                link.setUnderlined(true);
                link.setClickEvent(
                        new ClickEvent(ClickEvent.Action.OPEN_URL, part)
                );
                link.setHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder("Open link").color(ChatColor.GRAY).create()
                        )
                );
                component.addExtra(link);

            } else {
                TextComponent normal = new TextComponent(
                        ChatColor.translateAlternateColorCodes('&', part + " ")
                );
                component.addExtra(normal);
            }
        }

        return new BaseComponent[]{component};
    }
}
