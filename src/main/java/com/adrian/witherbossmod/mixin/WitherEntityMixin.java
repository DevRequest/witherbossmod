package com.adrian.witherbossmod.mixin;

import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.WitherEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin {

    @Accessor("bossBar")
    public abstract ServerBossBar getVanillaBossBar();

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void hideVanillaBossBar(CallbackInfo info) {
        WitherEntity entity = (WitherEntity) (Object) this;
        ServerBossBar vanillaBossBar = this.getVanillaBossBar();

        if (vanillaBossBar != null) {
            vanillaBossBar.setVisible(false);
        }
    }
}
