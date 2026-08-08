package com.r4men.game_night.network;

import com.r4men.game_night.common.lib.Version;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public abstract class BasePacketHandler {
    protected BasePacketHandler(IEventBus modEventBus, Version version) {
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            PayloadRegistrar registrar = event.registrar(version.toString());

            registerClientToServer(new PacketRegistrar(registrar, true));
            registerServerToClient(new PacketRegistrar(registrar, false));
        });
    }

    protected abstract void registerClientToServer(PacketRegistrar registrar);

    protected abstract void registerServerToClient(PacketRegistrar registrar);

    protected record SimplePacketPayload(
            CustomPacketPayload.Type<CustomPacketPayload> type) implements CustomPacketPayload {
        private SimplePacketPayload(Identifier identifier) {
            this(new CustomPacketPayload.Type<>(identifier));
        }
    }

    protected record PacketRegistrar(PayloadRegistrar registrar, boolean toServer) {
        public <MSG extends IGameNightPacket> void configuration(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            if (toServer) {
                registrar.configurationToServer(type, reader, IGameNightPacket::handle);
            } else {
                registrar.configurationToClient(type, reader, IGameNightPacket::handle);
            }
        }

        public <MSG extends IGameNightPacket> void play(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> reader) {
            if (toServer) {
                registrar.playToServer(type, reader, IGameNightPacket::handle);
            } else {
                registrar.playToClient(type, reader, IGameNightPacket::handle);
            }
        }

        public SimplePacketPayload playInstanced(Identifier identifier, IPayloadHandler<CustomPacketPayload> handler) {
            SimplePacketPayload payload = new SimplePacketPayload(identifier);
            if (toServer) {
                registrar.playToServer(payload.type, StreamCodec.unit(payload), handler);
            } else {
                registrar.playToClient(payload.type, StreamCodec.unit(payload), handler);
            }

            return payload;
        }
    }
}
