package realmayus.youmatter;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import realmayus.youmatter.creator.CreatorBlock;
import realmayus.youmatter.creator.CreatorContainer;
import realmayus.youmatter.creator.CreatorTile;
import realmayus.youmatter.encoder.EncoderBlock;
import realmayus.youmatter.encoder.EncoderContainer;
import realmayus.youmatter.encoder.EncoderTile;
import realmayus.youmatter.items.*;
import realmayus.youmatter.replicator.ReplicatorBlock;
import realmayus.youmatter.replicator.ReplicatorContainer;
import realmayus.youmatter.replicator.ReplicatorTile;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerContainer;
import realmayus.youmatter.scanner.ScannerTile;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler {

    @SubscribeEvent
    public static void addBlocksAndFluids(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new ScannerBlock().setRegistryName(YouMatter.MODID, "scanner"));
        event.getRegistry().register(new EncoderBlock().setRegistryName(YouMatter.MODID, "encoder"));
        event.getRegistry().register(new CreatorBlock().setRegistryName(YouMatter.MODID, "creator"));
        event.getRegistry().register(new ReplicatorBlock().setRegistryName(YouMatter.MODID, "replicator"));
    }

    @SubscribeEvent
    public static void addTE(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(ScannerTile::new, ObjectHolders.SCANNER_BLOCK).build(null).setRegistryName(YouMatter.MODID, "scanner"));
        event.getRegistry().register(TileEntityType.Builder.create(EncoderTile::new, ObjectHolders.ENCODER_BLOCK).build(null).setRegistryName(YouMatter.MODID, "encoder"));
        event.getRegistry().register(TileEntityType.Builder.create(CreatorTile::new, ObjectHolders.CREATOR_BLOCK).build(null).setRegistryName(YouMatter.MODID, "creator"));
        event.getRegistry().register(TileEntityType.Builder.create(ReplicatorTile::new, ObjectHolders.REPLICATOR_BLOCK).build(null).setRegistryName(YouMatter.MODID, "replicator"));
    }

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(ObjectHolders.SCANNER_BLOCK, new Item.Properties().group(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "scanner"));
        event.getRegistry().register(new BlockItem(ObjectHolders.ENCODER_BLOCK, new Item.Properties().group(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "encoder"));
        event.getRegistry().register(new BlockItem(ObjectHolders.CREATOR_BLOCK, new Item.Properties().group(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "creator"));
        event.getRegistry().register(new BlockItem(ObjectHolders.REPLICATOR_BLOCK, new Item.Properties().group(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "replicator"));
        event.getRegistry().register(new ThumbdriveItem().setRegistryName(YouMatter.MODID, "thumb_drive"));
        event.getRegistry().register(new BlackHoleItem().setRegistryName(YouMatter.MODID, "black_hole"));
        event.getRegistry().register(new MachineCasingItem().setRegistryName(YouMatter.MODID, "machine_casing"));
        event.getRegistry().register(new ComputeModuleItem().setRegistryName(YouMatter.MODID, "compute_module"));
        event.getRegistry().register(new TransistorItem().setRegistryName(YouMatter.MODID, "transistor"));
        event.getRegistry().register(new TransistorRawItem().setRegistryName(YouMatter.MODID, "transistor_raw"));
    }

    @SubscribeEvent
    public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new ScannerContainer(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":scanner"));
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new EncoderContainer(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":encoder"));
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new CreatorContainer(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":creator"));
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new ReplicatorContainer(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":replicator"));

    }

} //todo furnace recipe
