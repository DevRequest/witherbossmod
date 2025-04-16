package com.adrian.witherbossmod.entity;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.registry.*;
import net.farzad.steel_scythe.enchantments.ModEnchantments;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CustomWitherEntity extends WitherEntity {

    private static final float TRUE_MAX_HEALTH = 1200.0F;

    private final ServerBossBar bossBar;
    private float trueHealth = TRUE_MAX_HEALTH;
    private boolean isTrulyDead = false;
    private boolean hasDroppedLoot = false;

    DynamicRegistryManager registryManager = getEntityWorld().getRegistryManager();

    public CustomWitherEntity(EntityType<? extends WitherEntity> entityType, World world) {
        super(entityType, world);
        this.setCustomName(Text.literal("The Withered One"));

        bossBar = new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
        bossBar.setVisible(true);
    }

    public static DefaultAttributeContainer.Builder createWitherAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, TRUE_MAX_HEALTH)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6F)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0F)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0F);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || isTrulyDead) return false;

        this.trueHealth -= amount;

        if (this.trueHealth <= 0 && !isTrulyDead) {
            this.trueHealth = 0;
            isTrulyDead = true;
            this.kill();
            return super.damage(source, amount);
        }

        this.setHealth(Math.min(this.trueHealth, this.getMaxHealth()));
        return super.damage(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age == 1) {
            this.setHealth(this.getMaxHealth());
        }

        if (!this.isDead()) {
            this.bossBar.setPercent(this.trueHealth / TRUE_MAX_HEALTH);

            for (PlayerEntity player : this.getEntityWorld().getPlayers()) {
                if (player instanceof ServerPlayerEntity serverPlayer && !this.bossBar.getPlayers().contains(serverPlayer)) {
                    this.bossBar.addPlayer(serverPlayer);
                }
            }
        } else {
            this.bossBar.clearPlayers();

            if (!hasDroppedLoot) {
                hasDroppedLoot = true;
                this.dropLoot();
            }
        }

        if (isTrulyDead && !this.isDead() && !this.getWorld().isClient) {
            this.kill();
        }
    }

    @Override
    public void heal(float amount) {
        this.trueHealth = Math.min(this.trueHealth + amount, TRUE_MAX_HEALTH);
        this.setHealth(Math.min(this.trueHealth, this.getMaxHealth()));
    }

    public void dropLoot() {
        if (!this.getEntityWorld().isClient && isTrulyDead) {
            this.dropStack(this.createWitherScythe());
        }
    }


    public float getTrueHealth() {
        return this.trueHealth;
    }

    private ItemStack createWitherScythe() {
        Item steelScythe = Registries.ITEM.get(Identifier.of("steel_scythe", "steel_scythe"));
        ItemStack stack = steelScythe.getDefaultStack();

        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Wither Scythe").copy().styled(style -> style.withItalic(false)));

        Registry<Enchantment> enchantmentRegistry = registryManager.get(RegistryKeys.ENCHANTMENT);

        Map<RegistryEntry<Enchantment>, Integer> enchantments = new HashMap<>();
        enchantments.put(enchantmentRegistry.getEntry(enchantmentRegistry.get(Enchantments.SHARPNESS.getValue())), 5);
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

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("TrueHealth", this.trueHealth);
        nbt.putBoolean("IsTrulyDead", this.isTrulyDead);
        nbt.putBoolean("HasDroppedLoot", this.hasDroppedLoot);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TrueHealth")) {
            this.trueHealth = nbt.getFloat("TrueHealth");
        }
        if (nbt.contains("IsTrulyDead")) {
            this.isTrulyDead = nbt.getBoolean("IsTrulyDead");
        }
        if (nbt.contains("HasDroppedLoot")) {
            this.hasDroppedLoot = nbt.getBoolean("HasDroppedLoot");
        }

        if (this.isTrulyDead) {
            this.kill();
        }
    }

}
