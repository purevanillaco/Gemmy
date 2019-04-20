package ovh.quiquelhappy.mcplugins.gemmy.drops;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ovh.quiquelhappy.mcplugins.gemmy.main;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class drops {
    public drops() {
    }

    public Integer createDrop(Player player, Location location, Integer min, Integer max) {
        Random r = new Random();
        int Low = min + 1;
        int High = max + 1;
        int Result = r.nextInt(High - Low) + Low;

        if(Result>0){
            createExactDrop(player, location, Result);
        }

        return Result;
    }

    public void createExactDrop(Player player, Location location, Integer quantity) {

        String sound=main.plugin.getConfig().getString("drops.sound");
        String particle=main.plugin.getConfig().getString("drops.particle");
        Timer timer = new Timer();
        int begin = 1;
        int timeInterval;

        if(quantity<=64){
            timeInterval=50;
        } else if(quantity>64&&quantity<=320){
            timeInterval=15;
        } else {
            timeInterval=1;
        }

        timer.schedule(new TimerTask() {
            int counter = 1;
            @Override
            public void run() {
                if (counter >= quantity){
                    timer.cancel();
                }
                if(main.plugin.getConfig().getBoolean("drops.sound-enabled")){
                    location.getWorld().playSound(player.getLocation(), Sound.valueOf(sound), 10.0F, 1.0F);
                }
                if(main.plugin.getConfig().getBoolean("drops.particle-enabled")){
                    location.getWorld().spawnParticle(Particle.valueOf(particle), location.getX(), location.getY(), location.getZ(), 10);
                }
                counter++;
            }
        }, begin, timeInterval);

        Integer stackQuantity=(int) Math.floor(quantity/64);

        if (quantity>stackQuantity*64){
            ItemStack drop = new ItemStack(Material.getMaterial(main.plugin.getConfig().getString("drops.gem")));
            drop.setAmount(quantity-stackQuantity*64);
            ItemMeta dropmeta = drop.getItemMeta();

            if(main.plugin.getConfig().getBoolean("economy.enable") && main.plugin.getConfig().getBoolean("economy.enchant")){
                dropmeta.addEnchant(Enchantment.LUCK, 1,true);
            }

            dropmeta.setDisplayName(main.plugin.getConfig().getString("economy.currency"));
            drop.setItemMeta(dropmeta);
            location.getWorld().dropItem(location, drop);

            if(quantity-stackQuantity*64>0){


                for(int i = 0; i < stackQuantity; ++i){

                    drop.setAmount(64);
                    location.getWorld().dropItem(location, drop);

                }

            }
        }

    }
}
