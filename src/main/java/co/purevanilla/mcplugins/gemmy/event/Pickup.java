package co.purevanilla.mcplugins.gemmy.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class Pickup extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    Player player;
    float amount;

    public Pickup(Player player, float amount){
        this.player=player;
        this.amount=amount;
    }

    public Player getPlayer() {
        return player;
    }

    public float getAmount() {
        return amount;
    }
}
