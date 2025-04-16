package com.adrian.witherbossmod;

import com.adrian.witherbossmod.command.SummonCustomWitherCommand;
import com.adrian.witherbossmod.entity.CustomWitherEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.launch.MixinBootstrap;

public class Witherbossmod implements ModInitializer {
    public static final EntityType<CustomWitherEntity> CUSTOM_WITHER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Constants.MOD_ID + ":withered_one"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, CustomWitherEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9F, 3.5F))
                    .build()
    );

    @Override
    public void onInitialize() {
        MixinBootstrap.init();
        Constants.LOGGER.info("Initializing Wither Boss Mod");

        FabricDefaultAttributeRegistry.register(CUSTOM_WITHER, CustomWitherEntity.createWitherAttributes());

        CommandRegistrationCallback.EVENT.register(SummonCustomWitherCommand::register);
    }
}