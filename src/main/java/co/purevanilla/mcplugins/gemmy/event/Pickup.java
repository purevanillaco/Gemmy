package co.purevanilla.mcplugins.gemmy.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class Pickup extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
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
