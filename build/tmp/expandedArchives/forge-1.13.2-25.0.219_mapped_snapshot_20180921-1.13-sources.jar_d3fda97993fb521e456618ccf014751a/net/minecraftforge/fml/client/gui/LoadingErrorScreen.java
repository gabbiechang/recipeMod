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

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoadingErrorScreen extends GuiErrorScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path modsDir;
    private final Path logFile;
    private final List<ModLoadingException> modLoadErrors;
    private final List<ModLoadingWarning> modLoadWarnings;
    private LoadingEntryList entryList;
    private String errorHeader;
    private String warningHeader;

    public LoadingErrorScreen(LoadingFailedException loadingException, List<ModLoadingWarning> warnings)
    {
        super(null, null);
        this.modLoadWarnings = warnings;
        this.modLoadErrors = loadingException == null ? Collections.emptyList() : loadingException.getErrors();
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs","latest.log"));
    }

    private double openModsDir(double mouseX, double mouseY)
    {
        Util.getOSType().openFile(modsDir.toFile());
        return 0.0;
    }

    private double openLogFile(double mouseX, double mouseY)
    {
        Util.getOSType().openFile(logFile.toFile());
        return 0.0;
    }

    private double continueGame(double mouseX, double mouseY)
    {
        ClientHooks.logMissingTextureErrors();
        mc.displayGuiScreen(null);
        return 0.0;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttons.clear();
        this.children.clear();

        this.errorHeader = TextFormatting.RED + ForgeI18n.parseMessage("fml.loadingerrorscreen.errorheader", this.modLoadErrors.size()) + TextFormatting.RESET;
        this.warningHeader = TextFormatting.YELLOW + ForgeI18n.parseMessage("fml.loadingerrorscreen.warningheader", this.modLoadErrors.size()) + TextFormatting.RESET;

        int yOffset = this.modLoadErrors.isEmpty() ? 46 : 38;
        this.addButton(new GuiButtonClickConsumer(10, 50, this.height - yOffset, this.width / 2 - 55, 20,
                ForgeI18n.parseMessage("fml.button.open.mods.folder"), this::openModsDir));
        this.addButton(new GuiButtonClickConsumer(11, this.width / 2 + 5, this.height - yOffset, this.width / 2 - 55, 20,
                ForgeI18n.parseMessage("fml.button.open.file", logFile.getFileName()), this::openLogFile));
        if (this.modLoadErrors.isEmpty()) {
            this.addButton(new GuiButtonClickConsumer(12, this.width / 4, this.height - 24, this.width / 2, 20,
                    ForgeI18n.parseMessage("fml.button.continue.launch"), this::continueGame));
        }

        this.entryList = new LoadingEntryList(this, this.modLoadErrors, this.modLoadWarnings);
        this.children.add(this.entryList);
        this.setFocused(this.entryList);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.entryList.drawScreen(mouseX, mouseY, partialTicks);
        drawMultiLineCenteredString(fontRenderer, this.modLoadErrors.isEmpty() ? warningHeader : errorHeader, this.width / 2, 10);
        this.buttons.forEach(button -> button.render(mouseX, mouseY, partialTicks));
    }

    private void drawMultiLineCenteredString(FontRenderer fr, String str, int x, int y) {
        for (String s : fr.listFormattedStringToWidth(str, this.width)) {
            fr.drawStringWithShadow(s, (float) (x - fr.getStringWidth(s) / 2.0), y, 0xFFFFFF);
            y+=fr.FONT_HEIGHT;
        }
    }
    public static class LoadingEntryList extends GuiListExtended<LoadingEntryList.LoadingMessageEntry> {
        LoadingEntryList(final LoadingErrorScreen parent, final List<ModLoadingException> errors, final List<ModLoadingWarning> warnings) {
            super(parent.mc, parent.width, parent.height, 35, parent.height - 50, 2 * parent.mc.fontRenderer.FONT_HEIGHT + 8);
            boolean both = !errors.isEmpty() && !warnings.isEmpty();
            if (both)
                addEntry(new LoadingMessageEntry(parent.errorHeader, true));
            errors.forEach(e->addEntry(new LoadingMessageEntry(e.formatToString())));
            if (both) {
                int maxChars = (this.width - 10) / parent.mc.fontRenderer.getStringWidth("-");
                addEntry(new LoadingMessageEntry("\n" + Strings.repeat("-", maxChars) + "\n"));
                addEntry(new LoadingMessageEntry(parent.warningHeader, true));
            }
            warnings.forEach(w->addEntry(new LoadingMessageEntry(w.formatToString())));
        }

        @Override
        protected int getScrollBarX()
        {
            return this.right - 6;
        }

        @Override
        public int getListWidth()
        {
            return this.width;
        }

        public class LoadingMessageEntry extends GuiListExtended.IGuiListEntry<LoadingMessageEntry> {
            private final String message;
            private final boolean center;

            LoadingMessageEntry(final String message) {
                this(message, false);
            }

            LoadingMessageEntry(final String message, final boolean center) {
                this.message = Objects.requireNonNull(message);
                this.center = center;
            }

            @Override
            public void drawEntry(final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean p_194999_5_, final float partialTicks) {
                int top = this.getY();
                int left = this.getX();
                FontRenderer font = Minecraft.getInstance().fontRenderer;
                final List<String> strings = font.listFormattedStringToWidth(message, LoadingEntryList.this.width);
                int y = top + 2;
                for (int i = 0; i < Math.min(strings.size(), 2); i++) {
                    if (center)
                        font.drawString(strings.get(i), left + (width  / 2F) - font.getStringWidth(strings.get(i)) / 2F, y, 0xFFFFFF);
                    else
                        font.drawString(strings.get(i), left + 5, y, 0xFFFFFF);
                    y += font.FONT_HEIGHT;
                }
            }
        }

    }
}