package ovh.quiquelhappy.mcplugins.gemmy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.quiquelhappy.mcplugins.gemmy.drops.blocks;
import ovh.quiquelhappy.mcplugins.gemmy.drops.mobs;

import java.io.File;


public class main extends JavaPlugin {
    public static Plugin plugin = null;

    public void onEnable() {
        plugin = this;
        if ((new File("plugins" + File.separator + "Gemmy" + File.separator + "config.yml")).isFile()) {
            System.out.println("[Gemmy] Loading config");
        } else {
            System.out.println("[Gemmy] Creating config");
            this.saveDefaultConfig();
            this.getConfig().options().copyDefaults(true);
        }

        FileConfiguration config = this.getConfig();
        if (config.getBoolean("drops.enable")) {
            Bukkit.getServer().getPluginManager().registerEvents(new blocks(), this);
            Bukkit.getServer().getPluginManager().registerEvents(new mobs(), this);
        } else {
            System.out.println("[Gemmy] Drops are DISABLED");
        }

    }

    public void onDisable() {
        System.out.println("[Gemmy] Terminating process");
    }
}
