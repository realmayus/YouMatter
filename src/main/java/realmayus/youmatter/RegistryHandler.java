package realmayus.youmatter;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerContainer;
import realmayus.youmatter.scanner.ScannerTile;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler {

    @SubscribeEvent
    public static void addBlocksAndFluids(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new ScannerBlock().setRegistryName(YouMatter.MODID, "scanner"));
    }

    public static void addTE(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(ScannerTile::new, ObjectHolders.SCANNER).build(null).setRegistryName(YouMatter.MODID, "scanner"));
    }

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(ObjectHolders.SCANNER, new Item.Properties().group(YouMatter.ITEM_GROUP)).setRegistryName(YouMatter.MODID, "scanner"));
        event.getRegistry().register(new Item(new Item.Properties().group(YouMatter.ITEM_GROUP)).setRegistryName(ObjectHolders.SCANNER.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        IForgeContainerType.create((windowId, inv, data) -> new ScannerContainer(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":scannercontainertype");
    }

} //todo furnace recipe
