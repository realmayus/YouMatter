package org.realverse.youmatter.network;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.realverse.youmatter.creator.CreatorMenu;
import org.realverse.youmatter.replicator.ReplicatorMenu;

public class PacketHandler {
    private PacketHandler() {
    }

    public static class CreatorSettings {
        private CreatorSettings() {
        }

        public static void handle(PacketChangeSettingsCreatorServer data, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                AbstractContainerMenu menu = ctx.player().containerMenu;
                if (menu instanceof CreatorMenu openContainer) {
                    openContainer.creator.setActive(data.isActive());
                    openContainer.creator.setCurrentMode(data.mode());
                }

            });
        }
    }

    public static class ShowNext {
        private ShowNext() {
        }

        public static void handle(PacketShowNext data, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                AbstractContainerMenu menu = ctx.player().containerMenu;
                if (menu instanceof ReplicatorMenu openContainer) {
                    openContainer.replicator.renderNext();
                }

            });
        }
    }

    public static class ShowPrevious {
        private ShowPrevious() {
        }

        public static void handle(PacketShowPrevious data, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                AbstractContainerMenu menu = ctx.player().containerMenu;
                if (menu instanceof ReplicatorMenu openContainer) {
                    openContainer.replicator.renderPrevious();
                }

            });
        }
    }

    public static class ReplicatorSettings {
        private ReplicatorSettings() {
        }

        public static void handle(PacketChangeSettingsReplicatorServer data, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                AbstractContainerMenu menu = ctx.player().containerMenu;
                if (menu instanceof ReplicatorMenu openContainer) {
                    openContainer.replicator.setActive(data.isActivated());
                    openContainer.replicator.setCurrentMode(data.mode());
                }
            });
        }
    }
}
