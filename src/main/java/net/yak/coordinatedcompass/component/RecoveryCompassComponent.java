package net.yak.coordinatedcompass.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public record RecoveryCompassComponent(int cycle) implements TooltipAppender  {
    public static final Codec<RecoveryCompassComponent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.optionalFieldOf("cycle", -1).forGetter(RecoveryCompassComponent::cycle)).apply(instance, RecoveryCompassComponent::new);
    });
    public static final PacketCodec<ByteBuf, RecoveryCompassComponent> PACKET_CODEC = PacketCodecs.INTEGER.xmap(RecoveryCompassComponent::new, RecoveryCompassComponent::cycle);
    private static final Text RECOVERY_COMPASS_TOOLTIP = Text.translatable("tooltip.coordinatedcompass.recovery_compass_tooltip").formatted(Formatting.AQUA);
    private static final Text COORDINATES_ENABLED = Text.translatable("tooltip.coordinatedcompass.coordinates_enabled").formatted(Formatting.AQUA);
    private static final Text COORDINATES_DISABLED = Text.translatable("tooltip.coordinatedcompass.coordinates_disabled").setStyle(Style.EMPTY.withColor(0x326569));
    private static final Text BIOME_ENABLED = Text.translatable("tooltip.coordinatedcompass.biome_enabled").formatted(Formatting.AQUA);
    private static final Text BIOME_DISABLED = Text.translatable("tooltip.coordinatedcompass.biome_disabled").setStyle(Style.EMPTY.withColor(0x326569));

    public RecoveryCompassComponent(int cycle) {
        this.cycle = cycle;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        /*tooltip.accept(RECOVERY_COMPASS_TOOLTIP); // idk why this doesn't work
        switch (this.cycle) {
            case 1: {
                tooltip.accept(COORDINATES_ENABLED);
                tooltip.accept(BIOME_DISABLED);
                break;
            }
            case 2: {
                tooltip.accept(COORDINATES_DISABLED);
                tooltip.accept(BIOME_ENABLED);
                break;
            }
            case 3: {
                tooltip.accept(COORDINATES_ENABLED);
                tooltip.accept(BIOME_ENABLED);
                break;
            }
            default: {
                tooltip.accept(COORDINATES_DISABLED);
                tooltip.accept(BIOME_DISABLED);
            }
        }*/
    }

    public RecoveryCompassComponent withCycle(int cycle) {
        return new RecoveryCompassComponent(cycle);
    }

    public int getCurrentCycle() {
        return this.cycle;
    }

    public static RecoveryCompassComponent cycleValue(int currentValue) {
        if (currentValue < 3) {
            return new RecoveryCompassComponent(currentValue + 1);
        }
        return new RecoveryCompassComponent(0);
    }


}
