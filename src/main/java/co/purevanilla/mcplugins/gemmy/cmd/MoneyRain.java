package co.purevanilla.mcplugins.gemmy.cmd;

import co.purevanilla.mcplugins.gemmy.Main;
import co.purevanilla.mcplugins.gemmy.util.Drop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MoneyRain implements CommandExecutor {

    List<Player> playerList = new ArrayList<>();

    public void startChecker() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            @Override
            public void run() {
                if(!playerList.isEmpty()){
                    for (Player player:playerList) {
                        try{
                            new Drop(player).spawn();
                        } catch(NullPointerException err){
                            playerList.remove(player);
                        }
                    }
                }
            }
        },0,Main.settings.getCondenseBreakpoint()/2);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(sender.hasPermission("gemmy.moneyrain")){

                if(this.playerList.contains((Player) sender)){
                    this.playerList.remove((Player) sender);
                    sender.sendMessage(ChatColor.YELLOW + "Stopped money rain");
                } else {
                    this.playerList.add((Player) sender);
                    sender.sendMessage(ChatColor.GREEN + "Do /moneyrain again to make it stop");
                }

            } else {
                sender.sendMessage(ChatColor.RED + "You don't have enough permission");
            }

        } else {
            sender.sendMessage("You need to be a player in order to execute this command");
        }

        return true;
    }
}
