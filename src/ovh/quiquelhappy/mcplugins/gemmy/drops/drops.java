package ovh.quiquelhappy.mcplugins.gemmy.drops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ovh.quiquelhappy.mcplugins.gemmy.main;

import java.util.Random;

public class drops {
    public drops() {
    }

    public Integer createDrop(Player player, Location location, Integer min, Integer max) {
        Random r = new Random();
        ItemStack drop = new ItemStack(Material.getMaterial(main.plugin.getConfig().getString("drops.gem")));
        int Low = min + 1;
        int High = max + 1;
        int Result = r.nextInt(High - Low) + Low;
        if (Result > 0 && Result < 64) {
            drop.setAmount(Result);
            location.getWorld().playSound(player.getLocation(), Sound.valueOf(main.plugin.getConfig().getString("drops.sound")), 10.0F, 1.0F);
            location.getWorld().spawnParticle(Particle.valueOf(main.plugin.getConfig().getString("drops.particle")), location.getX(), location.getY(), location.getZ(), 10);
            location.getWorld().dropItem(location, drop);
        }

        return Result;
    }
}
