package co.purevanilla.mcplugins.gemmy.cmd;

import co.purevanilla.mcplugins.gemmy.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class Baltop implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            Main.settings.baltop.plugin.getServer().getScheduler().runTaskAsynchronously(Main.settings.baltop.plugin, () -> {
                try {
                    Main.settings.baltop.showTop((Player) commandSender);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return true;
    }
}
