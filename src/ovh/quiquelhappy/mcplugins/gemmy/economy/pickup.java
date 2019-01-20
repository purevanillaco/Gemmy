package ovh.quiquelhappy.mcplugins.gemmy.economy;

import io.github.theluca98.textapi.ActionBar;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import ovh.quiquelhappy.mcplugins.gemmy.main;

public class pickup implements Listener {

    Economy eco = ovh.quiquelhappy.mcplugins.gemmy.main.getEconomy();

    @EventHandler
    public void PickupItem(EntityPickupItemEvent e) {
        if(e.getEntity().getType()== EntityType.PLAYER){
            Player player = (Player)e.getEntity();
            if(e.getItem().getItemStack().getType()== Material.getMaterial(main.plugin.getConfig().getString("drops.gem"))){
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Integer quantity = e.getItem().getItemStack().getAmount();
                                Integer quantityToAdd= quantity*main.plugin.getConfig().getInt("economy.value");
                                player.getInventory().remove(Material.getMaterial(main.plugin.getConfig().getString("drops.gem")));
                                eco.depositPlayer(((Player) e.getEntity()).getPlayer(), quantityToAdd);
                                ActionBar bar = new ActionBar(ChatColor.translateAlternateColorCodes('&', "&a&l+ "+main.plugin.getConfig().getString("economy.currency")+quantityToAdd));
                                bar.send(player);
                            }
                        },
                        1
                );
            }
        }
    }



    static {
        System.out.println("[Gemmy] Each gem has a value of " + main.plugin.getConfig().getInt("economy.value") + "economy units");
    }

}
