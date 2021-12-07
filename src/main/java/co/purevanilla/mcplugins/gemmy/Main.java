package co.purevanilla.mcplugins.gemmy;

import co.purevanilla.mcplugins.gemmy.cmd.DropAmount;
import co.purevanilla.mcplugins.gemmy.cmd.MoneyRain;
import co.purevanilla.mcplugins.gemmy.util.Harvest;
import co.purevanilla.mcplugins.gemmy.event.Money;
import co.purevanilla.mcplugins.gemmy.util.Settings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;


public class Main extends JavaPlugin {

    public static Settings settings;
    public static Plugin plugin;

    public static int playerPlacedIndex = 0;
    public static List<Block> playerPlaced = new ArrayList<Block>();
    public static HashMap<Location, Harvest> expectedReplants = new HashMap<Location, Harvest>();

    public static Economy econ = null;

    @Override
    public void onEnable() {
        super.onEnable();

        plugin=this;
        saveDefaultConfig();
        settings=new Settings(this.getConfig());

        if (!setupEconomy() ) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getServer().getPluginManager().registerEvents(new Money(), this);
            this.getLogger().log(Level.INFO,"enabled drops");
        }

        MoneyRain moneyRainManager = new MoneyRain();
        this.getCommand("moneyrain").setExecutor(moneyRainManager);
        this.getCommand("gemdrop").setExecutor(new DropAmount());
        moneyRainManager.startChecker();

        // remove temporal

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
}
