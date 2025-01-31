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

package net.minecraftforge.fml.client.gui;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.StartupQuery;

public class GuiConfirmation extends GuiNotification
{
    public GuiConfirmation(StartupQuery query)
    {
        super(query);
    }

    @Override
    public void initGui()
    {
        this.addButton(new GuiButtonClickConsumer(0, this.width / 2 - 104, this.height - 38, 100, 20, ForgeI18n.parseMessage("gui.yes"),(x, y)->
            {
                GuiConfirmation.this.mc.currentScreen = null;
                query.setResult(true);
                query.finish();
            }
        ));
        this.addButton(new GuiButtonClickConsumer(1, this.width / 2 + 4, this.height - 38, 100, 20, ForgeI18n.parseMessage("gui.no"), (x,y)->
            {
                GuiConfirmation.this.mc.currentScreen = null;
                query.setResult(false);
                query.finish();
            }
        ));
    }
}