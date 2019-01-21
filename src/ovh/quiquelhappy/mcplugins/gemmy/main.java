package ovh.quiquelhappy.mcplugins.gemmy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.quiquelhappy.mcplugins.gemmy.drops.blocks;
import ovh.quiquelhappy.mcplugins.gemmy.drops.mobs;
import ovh.quiquelhappy.mcplugins.gemmy.economy.death;
import ovh.quiquelhappy.mcplugins.gemmy.economy.pickup;

import java.io.File;


public class main extends JavaPlugin {
    public static Plugin plugin = null;

    private static Economy econ = null;
    private static Permission perms = null;

    public void onEnable() {
        plugin = this;

        createHeader(" ");

        System.out.println("   ____                                ");
        System.out.println("  / ___| ___ _ __ ___  _ __ ___  _   _ ");
        System.out.println(" | |  _ / _ \\ '_ ` _ \\| '_ ` _ \\| | | |");
        System.out.println(" | |_| |  __/ | | | | | | | | | | |_| |");
        System.out.println("  \\____|\\___|_| |_| |_|_| |_| |_|\\__, |");
        System.out.println("                                 |___/");

        createHeader("CONFIG");

        if ((new File("plugins" + File.separator + "Gemmy" + File.separator + "config.yml")).isFile()) {
            System.out.println("[Gemmy] Loading config");
        } else {
            System.out.println("[Gemmy] Creating config");
            this.saveDefaultConfig();
            this.getConfig().options().copyDefaults(true);
        }

        createHeader("DROPS");

        FileConfiguration config = this.getConfig();
        if (config.getBoolean("drops.enable")) {
            Bukkit.getServer().getPluginManager().registerEvents(new blocks(), this);
            Bukkit.getServer().getPluginManager().registerEvents(new mobs(), this);
        } else {
            System.out.println("[Gemmy] Drops are DISABLED");
        }

        createHeader("ECONOMY");

        if (config.getBoolean("economy.enable")) {
            System.out.println("[Gemmy] Enabling economy");

            setupPermissions();

            if (!setupEconomy() ) {
                System.out.println("[Gemmy] Vault is not installed or/and you don't have any economy plugin. Install these dependencies or disable Gemmy economy");
                getServer().getPluginManager().disablePlugin(this);
            } else {
                System.out.println("[Gemmy] Hooked into Vault");
                Bukkit.getServer().getPluginManager().registerEvents(new death(), this);;
                Bukkit.getServer().getPluginManager().registerEvents(new pickup(), this);
            }
        } else {
            System.out.println("[Gemmy] Economy is DISABLED");
        }

        createHeader("LOADING FINISHED");

    }

    private boolean createHeader(String header){
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(header);
        System.out.println(" ");
        return true;
    }



    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }



    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }


    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }


    public void onDisable() {
        System.out.println("[Gemmy] Terminating process");
    }
}
