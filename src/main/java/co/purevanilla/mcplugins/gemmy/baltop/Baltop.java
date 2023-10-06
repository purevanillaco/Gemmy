package co.purevanilla.mcplugins.gemmy.baltop;

import co.purevanilla.mcplugins.gemmy.Main;
import com.earth2me.essentials.Essentials;
import net.essentialsx.api.v2.services.BalanceTop;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Baltop {

    public Plugin plugin;
    Essentials essentials;
    FileConfiguration data;
    File dataFile;

    public Baltop(Plugin plugin){
        this.plugin=plugin;
        this.essentials = (Essentials) this.plugin.getServer().getPluginManager().getPlugin("Essentials");
        this.dataFile = new File(this.plugin.getDataFolder(), "data.yml");
        if (!this.dataFile.exists()) {
            this.dataFile.getParentFile().mkdirs();
            this.plugin.saveResource("data.yml", false);
        }
        this.data = YamlConfiguration.loadConfiguration(this.dataFile);
    }

    public void addPoints(Player player, BigDecimal points){
        this.addPoints(player, points, false);
    }

    public void addPoints(Player player, BigDecimal points, boolean ignoreIncome){
        Main.econ.depositPlayer(player,points.floatValue());
        if(!ignoreIncome){
            final String key = "points."+player.getUniqueId();
            if(this.data.contains(key)){
                this.data.set(key, this.data.getDouble(key) + points.doubleValue());
            } else {
                this.data.set(key, points.doubleValue());
            }
        }
    }

    public BigDecimal getIncome(Player player){
        final String k = "points." + player.getUniqueId();
        if(!this.data.contains(k)) return BigDecimal.ZERO;
        return BigDecimal.valueOf(this.data.getDouble(k));
    }

    public HashMap<String, Double> showTop(Player player) throws ExecutionException, InterruptedException {
        HashMap<String, Double> top = new HashMap<>();
        // formatter
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator('.');
        DecimalFormat numberFormatter = new DecimalFormat("###,###.##", symbols);

        // Iterate through the keys in the "points" configuration section
        this.plugin.getLogger().info("computing top");
        for (String key : Objects.requireNonNull(this.data.getConfigurationSection("points")).getKeys(false)) {
            if(key.equals("uuid")) continue;

            Double value = this.data.getDouble("points." + key);

            // Check if the current value is among the top 10
            if (top.size() < 10 || value > Collections.min(top.values())) {
                // If the top map has less than 10 entries or the value is greater than the smallest value in the top map
                if (top.size() == 10) {
                    // If there are already 10 entries, remove the smallest value entry
                    String smallestKey = null;
                    for (String topKey : top.keySet()) {
                        if (smallestKey == null || top.get(topKey) < top.get(smallestKey)) {
                            smallestKey = topKey;
                        }
                    }
                    top.remove(smallestKey);
                }
                top.put(key, value);
            }
        }


        this.plugin.getLogger().info("sorting top");
        List<Map.Entry<String,Double>> sortedEntries = new ArrayList<Map.Entry<String,Double>>(top.entrySet());
        sortedEntries.sort(new Comparator<>() {
            @Override
            public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });

        this.plugin.getLogger().info("getting balance top");
        BalanceTop balanceTop = this.essentials.getBalanceTop();
        if(!balanceTop.isCacheLocked()) balanceTop.calculateBalanceTopMapAsync().join();
        Map<UUID, BalanceTop.Entry> balanceTopCache = balanceTop.getBalanceTopCache();
        MiniMessage formatter = MiniMessage.miniMessage();
        player.sendMessage(formatter.deserialize(Objects.requireNonNull(this.plugin.getConfig().getString("format.economy_header"))));

        int i = 0;
        for (BalanceTop.Entry btopEntry:balanceTopCache.values()) {
            player.sendMessage(formatter.deserialize(
                    Objects.requireNonNull(this.plugin.getConfig().getString("format.entry")),
                    Placeholder.unparsed("player", btopEntry.getDisplayName()),
                    Placeholder.unparsed("value", numberFormatter.format(btopEntry.getBalance()))
            ));
            i++;
            if(i>=10) break;
        }

        player.sendMessage(formatter.deserialize(Objects.requireNonNull(this.plugin.getConfig().getString("format.points_header"))));
        for (Map.Entry<String,Double> entry: sortedEntries) {
            player.sendMessage(formatter.deserialize(
                    Objects.requireNonNull(this.plugin.getConfig().getString("format.entry")),
                    Placeholder.unparsed("player", Objects.requireNonNull(this.plugin.getServer().getOfflinePlayer(UUID.fromString(entry.getKey())).getName())),
                    Placeholder.unparsed("value", numberFormatter.format(entry.getValue()))
            ));
        }

        return top;
    }

    public void save() throws IOException {
        this.data.save(this.dataFile);
    }
}
