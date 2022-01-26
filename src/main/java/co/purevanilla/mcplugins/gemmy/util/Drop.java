package co.purevanilla.mcplugins.gemmy.util;

import co.purevanilla.mcplugins.gemmy.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Drop {

    Location location;
    Player player;
    long quantity = 0;

    public Drop(Location location, long quantity){
        this.location=location;
        this.quantity=quantity;
    }

    public Drop(Location location, long quantity, Player player){
        this.location=location;
        this.quantity=quantity;
        this.player=player;
    }

    public boolean hasQuantity(){
        return quantity > 0;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Drop(Player player){

        Location newLocation = player.getLocation();
        newLocation.setY(newLocation.getY()+3+randomBetweenRange(0,2));
        newLocation.setX(randomBetweenRange(newLocation.getX()-2,newLocation.getX()+2));
        newLocation.setZ(randomBetweenRange(newLocation.getZ()-2,newLocation.getZ()+2));

        this.location=newLocation;
        this.quantity= Main.settings.getCondenseBreakpoint();

    }

    public Drop(ItemStack item){
        this.location=null;

        ItemMeta itemMeta = item.getItemMeta();
        List<String> metaList = itemMeta.getLore();
        boolean createdByGemmy = false;
        long temporalQuantity = 0;

        for (String metaLine:metaList) {

            if(metaLine.startsWith("quantity:")){
                temporalQuantity = Long.parseLong(metaLine.split(":")[1]);
            } else if (metaLine.startsWith("gnb:")){
                if(metaLine.split(":")[1].equals("gem")){
                    createdByGemmy=true;
                }
            }

        }

        if(createdByGemmy){
            this.quantity=temporalQuantity*item.getAmount();
        }


    }

    public float getRandomPitch(){
        Random r = new Random();
        return (float) (1.0 + r.nextFloat() * (2.5 - 1.0));
    }

    public int randomBetweenRange(double min, double max){
        Random r = new Random();
        return (int) (r.nextInt((int) ((max - min) + 1)) + min);
    }

    public void spawn(){

        try{

            final Drop drop = this;

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                @Override
                public void run() {

                    final Item[] itemEntity = {null};


                    if(player!=null){
                        long originalQuantity = quantity;
                        float correctionRate = Main.settings.getCorrectionRate(player);
                        quantity=(long) Math.ceil(correctionRate*quantity);
                        if(quantity==0&&originalQuantity>0&&correctionRate>0f) quantity=1; // the correction rate may null out drops and make em impossible, so we drop 1 gem if the cap hasn't been reached (correctionRate=0)
                        Main.settings.addPickedUpGems(player, (int) quantity);
                    }

                    if(quantity > 0){

                        if(quantity <= Main.settings.getCondenseBreakpoint()){

                            ItemStack item = new ItemStack(Main.settings.getGem(),1);
                            ItemMeta itemMeta = item.getItemMeta();
                            List<String> lores = new ArrayList<>();
                            lores.add("quantity:"+quantity);
                            lores.add("gnb:gem");
                            itemMeta.setLore(lores);
                            item.setItemMeta(itemMeta);

                            itemEntity[0] = location.getWorld().dropItem(location,item);
                            itemEntity[0].setItemStack(item);


                            for (int i = 1; i <= quantity; i++) {

                                final Item finalItemEntity = itemEntity[0];
                                final int finalI = i;
                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(Main.settings.getSound()!=null){
                                            location.getWorld().playSound(location, Main.settings.getSound(), SoundCategory.BLOCKS,0.8F,getRandomPitch());
                                        }
                                        itemEntity[0].setCustomName(Main.settings.getDropName(finalI));
                                    }
                                }, i);

                            }

                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                @Override
                                public void run() {

                                    if(Main.settings.getParticle()!=null){
                                        location.getWorld().spawnParticle(Main.settings.getParticle(),location,(int) quantity);
                                    }

                                }
                            }, quantity);

                        } else if(quantity>Main.settings.getCondenseBreakpoint()) {

                            ItemStack item = new ItemStack(Main.settings.getLargeGem(),1);
                            ItemMeta itemMeta = item.getItemMeta();
                            List<String> lores = new ArrayList<>();
                            lores.add("quantity:"+quantity);
                            lores.add("gnb:gem");
                            itemMeta.setLore(lores);
                            item.setItemMeta(itemMeta);

                            itemEntity[0] = location.getWorld().dropItem(location,item);
                            itemEntity[0].setItemStack(item);

                            for (float i = 1; i <= 15; i++) {

                                final float finalI = i;
                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(Main.settings.getSound()!=null){
                                            location.getWorld().playSound(location, Main.settings.getSound(), SoundCategory.BLOCKS,1, (float) (1+finalI*0.167));
                                        }
                                    }
                                }, (long) i);

                            }

                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                @Override
                                public void run() {

                                    if(Main.settings.getParticle()!=null){
                                        location.getWorld().spawnParticle(Main.settings.getParticle(),location,(int) quantity);
                                    }

                                }
                            }, 15);

                        }

                        itemEntity[0].setCustomName(Main.settings.getDropName(getQuantity()));
                        itemEntity[0].setCustomNameVisible(true);
                        if(Main.settings.getGlowDuration()>0){
                            itemEntity[0].setGlowing(true);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                @Override
                                public void run() {

                                    itemEntity[0].setGlowing(false);

                                }
                            }, Main.settings.getGlowDuration()*20);
                        }
                    }
                }
            });
        } catch (NullPointerException | IllegalArgumentException err){
            // Fix later?
        }

    }

    public long getQuantity() {
        return quantity;
    }
}
