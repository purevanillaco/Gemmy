package co.purevanilla.mcplugins.gemmy.util;

import co.purevanilla.mcplugins.gemmy.Main;
import co.purevanilla.mcplugins.gemmy.baltop.Baltop;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Settings {

    // ---
    public Baltop baltop;

    // ---

    public int lengthPlaced;

    public HashMap<Material,Range> blocks;
    public HashMap<EntityType,Range> entities;

    public HashMap<Material,Harvest> farming;
    public Range breeding;
    public int glowDuration = 10;

    public boolean nearPickup;
    public boolean nearPickupParticleEnabled;
    public Particle nearPickupParticle;

    public List<String> disabledWorlds;

    public Material gem;
    public Material largeGem;
    public int condenseBreakpoint;
    public boolean charBefore;
    public ChatColor currencyColor;
    public String econCharacter;
    public String singleEconCharacter;
    public String condensedEconCharacter;
    public int deathPercent;
    public boolean deathEnabled;

    public Particle particle;
    public Sound sound;

    // --- player drops cache
    private Set<UUID> afk;

    public void addAFK(UUID uuid){
        this.afk.add(uuid);
    }

    public void unsetAFK(UUID uuid){
        this.afk.remove(uuid);
    }

    public boolean isAFK(UUID uuid){
        return this.afk.contains(uuid);
    }
    Calendar calendar;
    public Map<UUID,Double> playerGemsAmount;
    public int currentDate;

    // --- compiled lists for fast access

    public List<Material> blockMaterials;
    public List<EntityType> entityTypes;
    public List<Material> seeds;
    public List<Material> products;

    public HashMap<Material, Range> getBlocksHashMap(){
        return blocks;
    }

    public boolean isCharBefore() {
        return charBefore;
    }

    public String getCondensedEconCharacter() {
        return condensedEconCharacter;
    }

    public String getSingleEconCharacter() {
        return singleEconCharacter;
    }

    public ChatColor getCurrencyColor() {
        return currencyColor;
    }

    public int getPlayerRate(Player player, double amount){
        if(this.isAFK(player.getUniqueId())) {
            return 0;
        }

        double balance=this.baltop.getIncome(player).doubleValue();

        double denominator = 1+(balance-6000)/Math.pow(10, 4);
        if(denominator==0) denominator=1;
        double decimal = amount/denominator;
        if(decimal>amount) decimal=amount;
        if(decimal<0) return 0;
        if(decimal>=1) {
            return (int) Math.floor(decimal);
        } else {
            if(!playerGemsAmount.containsKey(player.getUniqueId())) {
                playerGemsAmount.put(player.getUniqueId(), 0.0);
            }

            decimal/=2;

            double sum = playerGemsAmount.get(player.getUniqueId()) + decimal;
            if(sum>=1) {
                playerGemsAmount.put(player.getUniqueId(), sum-1.0);
                return 1;
            } else {
                playerGemsAmount.put(player.getUniqueId(), sum);
                return 0;
            }
        }
    }

    public String getDropName(long amount){
        if(isCharBefore()){
            if(amount <= 1){
                return getCurrencyColor() + Main.settings.getSingleEconCharacter() + amount;
            } else if (amount > getCondenseBreakpoint()){
                return getCurrencyColor() + Main.settings.getCondensedEconCharacter() + amount;
            } else {
                return getCurrencyColor() + Main.settings.getEconCharacter() + amount;
            }
        } else {
            if(amount <= 1){
                return getCurrencyColor() + String.valueOf(amount) + Main.settings.getSingleEconCharacter();
            } else if (amount > getCondenseBreakpoint()){
                return getCurrencyColor() + String.valueOf(amount) + Main.settings.getCondensedEconCharacter();
            } else {
                return getCurrencyColor() + String.valueOf(amount) + Main.settings.getEconCharacter();
            }
        }
    }

    public String getEconCharacter() {
        return econCharacter;
    }

    public List<Material> getBlocks(){
        return blockMaterials;
    }

    public List<Material> getProducts(){
        return products;
    }

    public List<Material> getSeeds(){
        return seeds;
    }

    public HashMap<EntityType,Range> entityTypeRangeHashMap(){
        return entities;
    }

    public List<EntityType> getEntities(){
        return entityTypes;
    }

    public Range getBreedingRange() {
        return breeding;
    }

    public boolean isDeathEnabled() {
        return deathEnabled;
    }

    public int getDeathPercent(){
        return deathPercent;
    }

    public Sound getSound() {
        return sound;
    }

    public Particle getParticle() {
        return particle;
    }

    public Range getBlockRange(Material block){
        return blocks.get(block);
    }

    public Range getEntityRange(EntityType entityType){
        return entities.get(entityType);
    }

    public Harvest getHarvestFromSeed(Material seeds){

        Harvest result = null;
        Iterator<Map.Entry<Material, Harvest>> farmingIterator = farming.entrySet().iterator();
        while (farmingIterator.hasNext()) {
            Map.Entry pair = farmingIterator.next();
            Harvest harvest = (Harvest) pair.getValue();
            if(harvest.getSeed()==seeds){
                result=harvest;
            }
            farmingIterator.remove();

        }

        return result;

    }

    public int getGlowDuration(){
        return glowDuration;
    }

    public HashMap<Material, Harvest> getFarming() {
        return farming;
    }

    public int getCondenseBreakpoint() {
        return condenseBreakpoint;
    }

    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public List<Material> getBlockMaterials() {
        return blockMaterials;
    }

    public Material getGem() {
        return gem;
    }

    public Material getLargeGem() {
        return largeGem;
    }

    public Range getBreeding() {
        return breeding;
    }

    public Harvest getHarvest(Material result){
        return farming.get(result);
    }

    public Settings(FileConfiguration configuration, Plugin plugin){
        afk=new HashSet<>();
        playerGemsAmount = new HashMap<>();

        this.baltop=new Baltop(plugin);

        lengthPlaced = configuration.getInt("drops.player-placed-history-length");

        disabledWorlds = configuration.getStringList("drops.disabled_worlds");
        nearPickup = configuration.getBoolean("drops.near-pickup");
        nearPickupParticleEnabled = configuration.getBoolean("drops.near-pickup-particle-enabled");
        if(nearPickupParticleEnabled){
            nearPickupParticle = Particle.valueOf(configuration.getString("drops.near-pickup-particle"));
        }

        glowDuration = configuration.getInt("drops.glow-duration");
        deathEnabled = configuration.getBoolean("economy.death.enable");
        deathPercent = configuration.getInt("economy.death.default");
        econCharacter = configuration.getString("economy.currency");
        singleEconCharacter = configuration.getString("economy.single-item-currency");
        condensedEconCharacter = configuration.getString("economy.currency-condensed");
        charBefore = configuration.getBoolean("economy.currency-before");
        currencyColor = ChatColor.valueOf(configuration.getString("economy.currency-color"));

        ConfigurationSection sec = configuration.getConfigurationSection("blocks");
        if (sec != null) {
            this.blocks = new HashMap<Material, Range>();
            for(String key : sec.getKeys(false)){
                this.blocks.put(Material.valueOf(key),new Range(configuration.getInt("blocks."+key+".min"),configuration.getInt("blocks."+key+".max")));
            }
        }
        ConfigurationSection eec = configuration.getConfigurationSection("mobs");
        if (eec != null) {
            this.entities = new HashMap<EntityType, Range>();
            for(String key : eec.getKeys(false)){
                this.entities.put(EntityType.valueOf(key),new Range(configuration.getInt("mobs."+key+".min"),configuration.getInt("mobs."+key+".max")));
            }
        }

        // -------

        ConfigurationSection fec = configuration.getConfigurationSection("farming");
        if (fec != null) {
            this.farming = new HashMap<Material, Harvest>();
            for(String key : fec.getKeys(false)){
                Range harvestRange = new Range(configuration.getInt("farming."+key+".harvest.min"),configuration.getInt("farming."+key+".harvest.max"));
                Range replantRange = new Range(configuration.getInt("farming."+key+".replant.min"),configuration.getInt("farming."+key+".replant.max"));
                Harvest harvest = new Harvest(Material.valueOf(key),Material.valueOf(configuration.getString( "farming."+key+".seed")),harvestRange,replantRange);
                this.farming.put(Material.valueOf(key), harvest);
            }
        }
        this.breeding = new Range(configuration.getInt("breeding.min"),configuration.getInt("breeding.max"));

        // -------

        this.gem = Material.valueOf(configuration.getString("drops.gem"));
        this.largeGem = Material.valueOf(configuration.getString("drops.large"));
        this.condenseBreakpoint = configuration.getInt("drops.condense-from");

        // -------

        if(configuration.getBoolean("drops.particle-enabled")){
            this.particle=Particle.valueOf(configuration.getString("drops.particle"));
        }

        if(configuration.getBoolean("drops.sound-enabled")){
            this.sound=Sound.valueOf(configuration.getString("drops.sound"));
        }

        // -------

        this.compileLists();

    }

    public void compileLists(){

        Iterator<Map.Entry<Material, Range>> blockIterator = blocks.entrySet().iterator();
        this.blockMaterials=new ArrayList<>();

        while (blockIterator.hasNext()) {
            Map.Entry pair = blockIterator.next();
            this.blockMaterials.add((Material) pair.getKey());

        }

        Iterator<Map.Entry<EntityType, Range>> entityIterator = entities.entrySet().iterator();
        this.entityTypes=new ArrayList<>();
        while (entityIterator.hasNext()) {
            Map.Entry pair = entityIterator.next();
            this.entityTypes.add((EntityType) pair.getKey());

        }

        Iterator<Map.Entry<Material, Harvest>> farmingIterator = farming.entrySet().iterator();
        this.seeds=new ArrayList<>();
        this.products=new ArrayList<>();
        while (farmingIterator.hasNext()) {
            Map.Entry pair = farmingIterator.next();
            Harvest harvest = (Harvest) pair.getValue();
            this.seeds.add(harvest.getSeed());
            this.products.add((Material) pair.getKey());

        }

    }

}
