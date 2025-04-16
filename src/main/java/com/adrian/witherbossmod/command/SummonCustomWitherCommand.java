package com.adrian.witherbossmod.command;

import com.adrian.witherbossmod.Witherbossmod;
import com.adrian.witherbossmod.entity.CustomWitherEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class SummonCustomWitherCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("summonWitheredOne")
                        .requires(source -> source.hasPermissionLevel(2)) // Operator level 2
                        .executes(context -> execute(context, context.getSource().getPosition(), context.getSource()))
                        .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                .executes(context -> execute(context, Vec3ArgumentType.getVec3(context, "pos"), context.getSource()))
                        )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context, Vec3d position, ServerCommandSource source) throws CommandSyntaxException {
        CustomWitherEntity customWither = new CustomWitherEntity(Witherbossmod.CUSTOM_WITHER, source.getWorld());
        customWither.refreshPositionAndAngles(position.x, position.y, position.z, 0, 0);
        source.getWorld().spawnEntity(customWither);

        source.sendFeedback(() -> Text.literal("Withered One spawned at " + position.toString()), true);

        return 1;
    }
}