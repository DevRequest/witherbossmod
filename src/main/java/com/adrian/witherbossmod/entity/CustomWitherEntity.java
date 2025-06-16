package com.adrian.witherbossmod.entity;

import net.farzad.steel_scythe.enchantments.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class CustomWitherEntity extends WitherEntity {

    DynamicRegistryManager registryManager = getEntityWorld().getRegistryManager();

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
        this.dropStack(this.createWitherScythe());
    }

    private ItemStack createWitherScythe() {
        Item steelScythe = Registries.ITEM.get(Identifier.of("steel_scythe", "steel_scythe"));
        ItemStack stack = steelScythe.getDefaultStack();

        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Wither Scythe").copy().styled(style -> style.withItalic(false)));

        Registry<Enchantment> enchantmentRegistry = registryManager.get(RegistryKeys.ENCHANTMENT);

        Map<RegistryEntry<Enchantment>, Integer> enchantments = new HashMap<>();
        enchantments.put(enchantmentRegistry.getEntry(enchantmentRegistry.get(Enchantments.LOOTING.getValue())), 3);
        enchantments.put(enchantmentRegistry.getEntry(enchantmentRegistry.get(Enchantments.SWEEPING_EDGE.getValue())), 3);
        enchantments.put(enchantmentRegistry.getEntry(enchantmentRegistry.get(Enchantments.FIRE_ASPECT.getValue())), 2);
        enchantments.put(enchantmentRegistry.getEntry(enchantmentRegistry.get(Enchantments.KNOCKBACK.getValue())), 2);
        enchantments.put(enchantmentRegistry.getEntry(enchantmentRegistry.get(Enchantments.UNBREAKING.getValue())), 3);
        enchantments.put(enchantmentRegistry.getEntry(enchantmentRegistry.get(Enchantments.MENDING.getValue())), 1);
        enchantments.put(enchantmentRegistry.getEntry(enchantmentRegistry.get(ModEnchantments.REAPING.getValue())), 2);

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(stack.getEnchantments());
        enchantments.forEach(builder::add);
        EnchantmentHelper.set(stack, builder.build());

        return stack;
    }
}
