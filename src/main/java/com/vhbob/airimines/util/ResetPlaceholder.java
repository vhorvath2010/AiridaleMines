package com.vhbob.airimines.util;

import com.vhbob.airimines.AiridaleMines;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ResetPlaceholder extends PlaceholderExpansion {


    @Override
    public String getIdentifier() {
        return "airidalemines";
    }

    @Override
    public String getAuthor() {
        return AiridaleMines.getPlugin().getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return AiridaleMines.getPlugin().getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        return AiridaleMines.getPlugin().getUntilReset(identifier);
    }

}
