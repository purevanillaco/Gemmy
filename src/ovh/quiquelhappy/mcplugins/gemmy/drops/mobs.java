package ovh.quiquelhappy.mcplugins.gemmy.drops;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ovh.quiquelhappy.mcplugins.gemmy.main;

public class mobs implements Listener {
    drops gem = new drops();
    ArrayList<UUID> bannedMobs = new ArrayList();

    public mobs() {
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == SpawnReason.SPAWNER) {
            e.getEntity().getUniqueId();
            if (this.bannedMobs.size() >= main.plugin.getConfig().getInt("drops.blackListing.mobs")) {
                this.bannedMobs.clear();
                this.bannedMobs.add(e.getEntity().getUniqueId());
            } else {
                this.bannedMobs.add(e.getEntity().getUniqueId());
            }
        }

    }

    @EventHandler
    public void mobKillCheck(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null && !this.bannedMobs.contains(e.getEntity().getUniqueId())) {
            Location location = e.getEntity().getLocation();
            EntityType mob = e.getEntity().getType();
            Player player = e.getEntity().getKiller().getPlayer();
            if (this.mobList().contains(mob)) {
                this.gem.createDrop(player, location, main.plugin.getConfig().getInt("drops.mobs." + mob.toString() + ".min"), main.plugin.getConfig().getInt("drops.mobs." + mob.toString() + ".max"));
            }
        }

    }

    public List<EntityType> mobList() {
        List<EntityType> mobList = new ArrayList();
        mobList.clear();
        ConfigurationSection sec = main.plugin.getConfig().getConfigurationSection("drops.mobs");
        Iterator var3 = sec.getKeys(false).iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            mobList.add(EntityType.valueOf(key));
        }

        return mobList;
    }

    static {
        List<EntityType> mobList = new ArrayList();
        ConfigurationSection sec = main.plugin.getConfig().getConfigurationSection("drops.mobs");
        Iterator var2 = sec.getKeys(false).iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            mobList.add(EntityType.valueOf(key));
        }

        System.out.println("[Gemmy] Loading " + mobList.size() + " mob drops");
    }
}
