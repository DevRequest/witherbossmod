package com.adrian.witherbossmod.client;

import com.adrian.witherbossmod.Witherbossmod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.WitherEntityRenderer;

public class WitherbossmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Witherbossmod.CUSTOM_WITHER, WitherEntityRenderer::new);
    }
}