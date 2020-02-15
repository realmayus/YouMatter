package realmayus.youmatter.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;

import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import realmayus.youmatter.YouMatter;

//@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class LootHandler {
/*
    @SubscribeEvent
    public void lootLoad(LootTableLoadEvent event) {
        if(event.getName().toString().equalsIgnoreCase("minecraft:chests/end_city_treasure")) {
            event.getTable().addPool(getInjectPool());
        }
    }

    private LootPool getInjectPool() {
        return new LootPool(new LootEntry[] { getInjectEntry() }, new ILootCondition[0], new RandomValueRange(1), new RandomValueRange(8, 9), "youmatter_inject_pool");
    }

    private TableLootEntry getInjectEntry() {
        return new TableLootEntry(new ResourceLocation(YouMatter.MODID, "inject/end_city_treasure"), 100000, 0, new LootCondition[0], "youmatter_inject_entry");
    } */
}
