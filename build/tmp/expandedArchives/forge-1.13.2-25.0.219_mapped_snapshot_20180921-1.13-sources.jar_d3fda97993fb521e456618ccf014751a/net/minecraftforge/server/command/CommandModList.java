/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.stream.Collectors;

public class CommandModList {
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("mods")
                .requires(cs->cs.hasPermissionLevel(0)) //permission
                .executes(ctx -> {
                            ctx.getSource().sendFeedback(new TextComponentTranslation("commands.forge.mods.list",
                                    ModList.get().getMods().stream().map(ModInfo::getModId).collect(Collectors.joining(","))),
                                    true);
                            return 0;
                        }
                );
    }

}