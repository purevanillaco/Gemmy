package co.purevanilla.mcplugins.gemmy.cmd;

import co.purevanilla.mcplugins.gemmy.util.Drop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropAmount implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            int amount = Integer.parseInt(strings[0]);
            Drop drop = new Drop(((Player) commandSender).getPlayer().getLocation(),amount);
            drop.spawn();
        }
        return true;
    }
}
