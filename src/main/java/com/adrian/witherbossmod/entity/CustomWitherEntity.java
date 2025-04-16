package com.adrian.witherbossmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CustomWitherEntity extends WitherEntity {

    public CustomWitherEntity(EntityType<? extends WitherEntity> entityType, World world) {
        super(entityType, world);

        this.setCustomNameVisible(true);
        this.setCustomName(Text.literal("The Withered One"));

        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(1200.0);
        this.setHealth(1200.0F);
    }

    public static DefaultAttributeContainer.Builder createWitherAttributes() {
        return WitherEntity.createWitherAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1200.0);
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        Item steelScythe = Registries.ITEM.get(Identifier.of("steel_scythe", "steel_scythe"));
        ItemStack stack = steelScythe.getDefaultStack();
        this.dropStack(stack);
    }
}
