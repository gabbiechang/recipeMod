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

package net.minecraftforge.fml;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class TextComponentMessageFormatHandler {
    public static int handle(final TextComponentTranslation parent, final List<ITextComponent> children, final Object[] formatArgs, final String format) {
        try {
            TextComponentString component = new TextComponentString(ForgeI18n.parseFormat(format, formatArgs));
            component.getStyle().setParentStyle(parent.getStyle());
            children.add(component);
            return format.length();
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}