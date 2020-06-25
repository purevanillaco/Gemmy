package co.purevanilla.mcplugins.gemmy.util;

import org.bukkit.Material;

public class Harvest {

    Material product;
    Material seed;
    Range harvest;
    Range replant;

    Harvest(Material product, Material seed, Range harvest, Range replant){
        this.product=product;
        this.seed=seed;
        this.harvest=harvest;
        this.replant=replant;
    }

    public Material getSeed() {
        return seed;
    }

    public Material getProduct() {
        return product;
    }

    public Range getHarvest() {
        return harvest;
    }

    public Range getReplant() {
        return replant;
    }
}
