package realmayus.youmatter;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerContainer;
import realmayus.youmatter.scanner.ScannerTile;

@Mod.EventBusSubscriber
public class RegistryHandler {

    @SubscribeEvent
    public static void addBlocksAndFluids(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(new ScannerBlock());
    }

    public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(ScannerTile::new, ObjectHolders.SCANNER).build(null).setRegistryName(ObjectHolders.SCANNER.getRegistryName()));
    }

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        //		event.getRegistry().register(new Item(new Item.Properties().group(ItemGroup.REDSTONE).maxStackSize(7)).setRegistryName(new ResourceLocation(MOD_ID, "range_upgrade")));
    }

    @SubscribeEvent
    public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        IForgeContainerType.create((windowId, inv, data) -> new ScannerContainer(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)).setRegistryName(YouMatter.MODID + ":scannercontainertype");
    }

} //todo furnace recipe
