package co.purevanilla.mcplugins.gemmy;

import co.purevanilla.mcplugins.gemmy.cmd.Baltop;
import co.purevanilla.mcplugins.gemmy.cmd.DropAmount;
import co.purevanilla.mcplugins.gemmy.cmd.MoneyRain;
import co.purevanilla.mcplugins.gemmy.transaction.TransactionListener;
import co.purevanilla.mcplugins.gemmy.util.Harvest;
import co.purevanilla.mcplugins.gemmy.listener.Money;
import co.purevanilla.mcplugins.gemmy.util.Settings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;


public class Main extends JavaPlugin {

    public static Settings settings;
    public static Plugin plugin;

    public static int playerPlacedIndex = 0;
    public static List<Block> playerPlaced = new ArrayList<Block>();
    public static HashMap<Location, Harvest> expectedReplants = new HashMap<Location, Harvest>();
    public static Economy econ = null;
    public TransactionListener listener;

    @Override
    public void onEnable() {
        super.onEnable();
        plugin=this;

        saveDefaultConfig();
        settings=new Settings(this.getConfig(), this);

        if (!setupEconomy() ) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            try {
                this.listener = new TransactionListener(this);
                getServer().getPluginManager().registerEvents(this.listener, this);
            } catch (SQLException | IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            getServer().getPluginManager().registerEvents(new Money(this.listener), this);
            this.getLogger().log(Level.INFO,"enabled drops");
        }

        MoneyRain moneyRainManager = new MoneyRain();
        Objects.requireNonNull(this.getCommand("moneyrain")).setExecutor(moneyRainManager);
        Objects.requireNonNull(this.getCommand("gemdrop")).setExecutor(new DropAmount());
        Objects.requireNonNull(this.getCommand("balancetop")).setExecutor(new Baltop());
        moneyRainManager.startChecker();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        try {
            Main.settings.baltop.save();
            this.listener.flush();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
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
