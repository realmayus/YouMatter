package realmayus.youmatter;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import realmayus.youmatter.creator.CreatorBlock;
import realmayus.youmatter.creator.CreatorBlockEntity;
import realmayus.youmatter.creator.CreatorMenu;
import realmayus.youmatter.encoder.EncoderBlock;
import realmayus.youmatter.encoder.EncoderBlockEntity;
import realmayus.youmatter.encoder.EncoderMenu;
import realmayus.youmatter.items.BlackHoleItem;
import realmayus.youmatter.items.ComputeModuleItem;
import realmayus.youmatter.items.MachineCasingItem;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.items.TransistorItem;
import realmayus.youmatter.items.TransistorRawItem;
import realmayus.youmatter.replicator.ReplicatorBlock;
import realmayus.youmatter.replicator.ReplicatorBlockEntity;
import realmayus.youmatter.replicator.ReplicatorMenu;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerBlockEntity;
import realmayus.youmatter.scanner.ScannerMenu;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler {

    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event) {
        event.register(Keys.BLOCKS, helper -> {
            helper.register("scanner", new ScannerBlock());
            helper.register("encoder", new EncoderBlock());
            helper.register("creator", new CreatorBlock());
            helper.register("replicator", new ReplicatorBlock());
        });
        event.register(Keys.BLOCK_ENTITY_TYPES, helper -> {
            helper.register("scanner", BlockEntityType.Builder.of(ScannerBlockEntity::new, ObjectHolders.SCANNER_BLOCK).build(null));
            helper.register("encoder", BlockEntityType.Builder.of(EncoderBlockEntity::new, ObjectHolders.ENCODER_BLOCK).build(null));
            helper.register("creator", BlockEntityType.Builder.of(CreatorBlockEntity::new, ObjectHolders.CREATOR_BLOCK).build(null));
            helper.register("replicator", BlockEntityType.Builder.of(ReplicatorBlockEntity::new, ObjectHolders.REPLICATOR_BLOCK).build(null));
        });
        event.register(Keys.ITEMS, helper -> {
            helper.register("scanner", new BlockItem(ObjectHolders.SCANNER_BLOCK, new Item.Properties().tab(YouMatter.ITEM_GROUP)));
            helper.register("encoder", new BlockItem(ObjectHolders.ENCODER_BLOCK, new Item.Properties().tab(YouMatter.ITEM_GROUP)));
            helper.register("creator", new BlockItem(ObjectHolders.CREATOR_BLOCK, new Item.Properties().tab(YouMatter.ITEM_GROUP)));
            helper.register("replicator", new BlockItem(ObjectHolders.REPLICATOR_BLOCK, new Item.Properties().tab(YouMatter.ITEM_GROUP)));
            helper.register("thumb_drive", new ThumbdriveItem());
            helper.register("black_hole", new BlackHoleItem());
            helper.register("machine_casing", new MachineCasingItem());
            helper.register("compute_module", new ComputeModuleItem());
            helper.register("transistor", new TransistorItem());
            helper.register("transistor_raw", new TransistorRawItem());
        });
        event.register(Keys.MENU_TYPES, helper -> {
            helper.register("scanner", IForgeMenuType.create((windowId, inv, data) -> new ScannerMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)));
            helper.register("encoder", IForgeMenuType.create((windowId, inv, data) -> new EncoderMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)));
            helper.register("creator", IForgeMenuType.create((windowId, inv, data) -> new CreatorMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)));
            helper.register("replicator", IForgeMenuType.create((windowId, inv, data) -> new ReplicatorMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)));
        });
    }

}
