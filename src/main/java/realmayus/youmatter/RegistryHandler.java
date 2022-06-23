package realmayus.youmatter;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import realmayus.youmatter.creator.CreatorBlock;
import realmayus.youmatter.creator.CreatorMenu;
import realmayus.youmatter.creator.CreatorBlockEntity;
import realmayus.youmatter.encoder.EncoderBlock;
import realmayus.youmatter.encoder.EncoderMenu;
import realmayus.youmatter.encoder.EncoderBlockEntity;
import realmayus.youmatter.items.BlackHoleItem;
import realmayus.youmatter.items.ComputeModuleItem;
import realmayus.youmatter.items.MachineCasingItem;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.items.TransistorItem;
import realmayus.youmatter.items.TransistorRawItem;
import realmayus.youmatter.replicator.ReplicatorBlock;
import realmayus.youmatter.replicator.ReplicatorMenu;
import realmayus.youmatter.replicator.ReplicatorBlockEntity;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerMenu;
import realmayus.youmatter.scanner.ScannerBlockEntity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new ScannerBlock().setRegistryName(YouMatter.MODID, "scanner"));
        event.getRegistry().register(new EncoderBlock().setRegistryName(YouMatter.MODID, "encoder"));
        event.getRegistry().register(new CreatorBlock().setRegistryName(YouMatter.MODID, "creator"));
        event.getRegistry().register(new ReplicatorBlock().setRegistryName(YouMatter.MODID, "replicator"));
    }

    @SubscribeEvent
    public static void registerTileEntites(RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().register(BlockEntityType.Builder.of(ScannerBlockEntity::new, ObjectHolders.SCANNER_BLOCK).build(null).setRegistryName(YouMatter.MODID, "scanner"));
        event.getRegistry().register(BlockEntityType.Builder.of(EncoderBlockEntity::new, ObjectHolders.ENCODER_BLOCK).build(null).setRegistryName(YouMatter.MODID, "encoder"));
        event.getRegistry().register(BlockEntityType.Builder.of(CreatorBlockEntity::new, ObjectHolders.CREATOR_BLOCK).build(null).setRegistryName(YouMatter.MODID, "creator"));
        event.getRegistry().register(BlockEntityType.Builder.of(ReplicatorBlockEntity::new, ObjectHolders.REPLICATOR_BLOCK).build(null).setRegistryName(YouMatter.MODID, "replicator"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(ObjectHolders.SCANNER_BLOCK, new Item.Properties().tab(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "scanner"));
        event.getRegistry().register(new BlockItem(ObjectHolders.ENCODER_BLOCK, new Item.Properties().tab(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "encoder"));
        event.getRegistry().register(new BlockItem(ObjectHolders.CREATOR_BLOCK, new Item.Properties().tab(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "creator"));
        event.getRegistry().register(new BlockItem(ObjectHolders.REPLICATOR_BLOCK, new Item.Properties().tab(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "replicator"));
        event.getRegistry().register(new ThumbdriveItem().setRegistryName(YouMatter.MODID, "thumb_drive"));
        event.getRegistry().register(new BlackHoleItem().setRegistryName(YouMatter.MODID, "black_hole"));
        event.getRegistry().register(new MachineCasingItem().setRegistryName(YouMatter.MODID, "machine_casing"));
        event.getRegistry().register(new ComputeModuleItem().setRegistryName(YouMatter.MODID, "compute_module"));
        event.getRegistry().register(new TransistorItem().setRegistryName(YouMatter.MODID, "transistor"));
        event.getRegistry().register(new TransistorRawItem().setRegistryName(YouMatter.MODID, "transistor_raw"));
    }

    @SubscribeEvent
    public static void registerContainerTypes(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new ScannerMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":scanner"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new EncoderMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":encoder"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new CreatorMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":creator"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new ReplicatorMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":replicator"));

    }

}
