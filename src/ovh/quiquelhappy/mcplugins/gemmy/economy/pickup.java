package ovh.quiquelhappy.mcplugins.gemmy.economy;

import io.github.theluca98.textapi.ActionBar;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
            if(e.getItem().getItemStack().getType()== Material.getMaterial(main.plugin.getConfig().getString("drops.gem"))){
                if(main.plugin.getConfig().getBoolean("economy.enchant")){
                    if(e.getItem().getItemStack().containsEnchantment(Enchantment.LUCK)){
                        addQuantityFromEvenet(e);
                    }
                } else {
                    addQuantityFromEvenet(e);
                }
            }
        }
    }

    public void addQuantityFromEvenet(EntityPickupItemEvent e){
        Player player = (Player)e.getEntity();
        Integer quantity = e.getItem().getItemStack().getAmount();
        Integer quantityToAdd= quantity*main.plugin.getConfig().getInt("economy.value");
        eco.depositPlayer(((Player) e.getEntity()).getPlayer(), quantityToAdd);
        ActionBar bar = new ActionBar(ChatColor.translateAlternateColorCodes('&', "&a&l+ "+main.plugin.getConfig().getString("economy.currency")+quantityToAdd));
        bar.send(player);
        e.getItem().getItemStack().setAmount(0);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        player.getInventory().remove(Material.getMaterial(main.plugin.getConfig().getString("drops.gem")));
                    }
                },
                1
        );
    }



    static {
        System.out.println("[Gemmy] Each gem has a value of " + main.plugin.getConfig().getInt("economy.value") + "economy units");
    }

}
