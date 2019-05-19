package ovh.quiquelhappy.mcplugins.gemmy.drops;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ovh.quiquelhappy.mcplugins.gemmy.main;

public class blocks implements Listener {
    static drops gem = new drops();
    ArrayList<Location> bannedLocations = new ArrayList();

    public blocks() {
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Material block = event.getBlock().getType();
        Player player = event.getPlayer();
        if (!this.bannedLocations.contains(location) && this.blockList().contains(block)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(event.getPlayer().getLocation().getWorld().getBlockAt(event.getBlock().getLocation()).getType() == Material.AIR){
                        gem.createDrop(player, location, main.plugin.getConfig().getInt("drops.blocks." + block.toString() + ".min"), main.plugin.getConfig().getInt("drops.blocks." + block.toString() + ".max"));
                    } else {
                        TextComponent msg = new TextComponent("§e§lGEMS §7You can only get gems from blocks in unprotected areas: "+(event.getPlayer().getLocation().getWorld().getBlockAt(event.getBlock().getLocation()).getType()).toString());
                        Objects.requireNonNull(player).spigot().sendMessage(msg);
                    }
                }
            }.runTaskLater(main.plugin, 1);
        }

    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        Location location = e.getBlock().getLocation();
        Material block = e.getBlock().getType();
        if (this.bannedLocations.size() >= main.plugin.getConfig().getInt("drops.blackListing.blocks")) {
            this.bannedLocations.clear();
            this.bannedLocations.add(location);
        }

        if (this.blockList().contains(block)) {
            this.bannedLocations.add(location);
        }

    }

    public List<Material> blockList() {
        List<Material> blockList = new ArrayList();
        blockList.clear();
        ConfigurationSection sec = main.plugin.getConfig().getConfigurationSection("drops.blocks");
        Iterator var3 = sec.getKeys(false).iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            blockList.add(Material.getMaterial(key));
        }

        return blockList;
    }

    static {
        List<Material> blockList = new ArrayList();
        ConfigurationSection sec = main.plugin.getConfig().getConfigurationSection("drops.blocks");
        Iterator var2 = sec.getKeys(false).iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            blockList.add(Material.getMaterial(key));
        }

        System.out.println("[Gemmy] Loading " + blockList.size() + " block drops");
    }
}