package ovh.quiquelhappy.mcplugins.gemmy.economy;

import io.github.theluca98.textapi.ActionBar;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ovh.quiquelhappy.mcplugins.gemmy.drops.drops;
import ovh.quiquelhappy.mcplugins.gemmy.main;

public class death implements Listener {

    drops gem = new drops();
    Economy eco = ovh.quiquelhappy.mcplugins.gemmy.main.getEconomy();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if(main.plugin.getConfig().getBoolean("economy.death.enable")){
            Player player = event.getEntity();
            Double player_money = eco.getBalance(player.getPlayer());
            Integer percent = main.plugin.getConfig().getInt("economy.death.default");
            Double money_raw = player_money*percent*0.01;
            Long money_rounded = Math.round(money_raw);
            Integer money_final = Math.toIntExact(money_rounded);
            if((money_raw-money_rounded)<0){
                money_final = Math.toIntExact(money_rounded-1);
            }

            eco.withdrawPlayer(player, money_final);
            this.gem.createExactDrop(player, player.getLocation(), money_final);

            ActionBar bar = new ActionBar(ChatColor.translateAlternateColorCodes('&', "&4&l- "+main.plugin.getConfig().getString("economy.currency")+money_final));
            bar.send(player);
        }
    }



    static {
        if(main.plugin.getConfig().getBoolean("economy.death.enable")){
            System.out.println("[Gemmy] Default players will drop " + main.plugin.getConfig().getInt("economy.death.default") + "% of their balance when they die");
        } else {
            System.out.println("[Gemmy] Players won't drop gems when they die");
        }
    }
}
