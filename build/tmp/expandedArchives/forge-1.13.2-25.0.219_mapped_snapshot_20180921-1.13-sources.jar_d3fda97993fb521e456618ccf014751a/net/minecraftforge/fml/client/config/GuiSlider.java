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

package net.minecraftforge.fml.client.config;

import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

/**
 * This class is blatantly stolen from iChunUtils with permission.
 *
 * @author iChun
 */
public class GuiSlider extends GuiButtonExt
{
    /** The value of this slider control. */
    public double sliderValue = 1.0F;

    public String dispString = "";

    /** Is this slider control being dragged. */
    public boolean dragging = false;
    public boolean showDecimal = true;

    public double minValue = 0.0D;
    public double maxValue = 5.0D;
    public int precision = 1;

    @Nullable
    public ISlider parent = null;

    public String suffix = "";

    public boolean drawString = true;

    public GuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr)
    {
        this(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, null);
    }

    public GuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
    {
        super(id, xPos, yPos, width, height, prefix);
        minValue = minVal;
        maxValue = maxVal;
        sliderValue = (currentVal - minValue) / (maxValue - minValue);
        dispString = prefix;
        parent = par;
        suffix = suf;
        showDecimal = showDec;
        String val;

        if (showDecimal)
        {
            val = Double.toString(sliderValue * (maxValue - minValue) + minValue);
            precision = Math.min(val.substring(val.indexOf(".") + 1).length(), 4);
        }
        else
        {
            val = Integer.toString((int)Math.round(sliderValue * (maxValue - minValue) + minValue));
            precision = 0;
        }

        displayString = dispString + val + suffix;

        drawString = drawStr;
        if(!drawString)
        {
            displayString = "";
        }
    }

    public GuiSlider(int id, int xPos, int yPos, String displayStr, double minVal, double maxVal, double currentVal, ISlider par)
    {
        this(id, xPos, yPos, 150, 20, displayStr, "", minVal, maxVal, currentVal, true, true, par);
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    @Override
    public int getHoverState(boolean par1)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    @Override
    protected void renderBg(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                this.sliderValue = (par2 - (this.x + 4)) / (float)(this.width - 8);
                updateSlider();
            }

            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.sliderValue = (mouseX - (this.x + 4)) / (this.width - 8);
        updateSlider();
        this.dragging = true;
    }

    public void updateSlider()
    {
        if (this.sliderValue < 0.0F)
        {
            this.sliderValue = 0.0F;
        }

        if (this.sliderValue > 1.0F)
        {
            this.sliderValue = 1.0F;
        }

        String val;

        if (showDecimal)
        {
            val = Double.toString(sliderValue * (maxValue - minValue) + minValue);

            if (val.substring(val.indexOf(".") + 1).length() > precision)
            {
                val = val.substring(0, val.indexOf(".") + precision + 1);

                if (val.endsWith("."))
                {
                    val = val.substring(0, val.indexOf(".") + precision);
                }
            }
            else
            {
                while (val.substring(val.indexOf(".") + 1).length() < precision)
                {
                    val = val + "0";
                }
            }
        }
        else
        {
            val = Integer.toString((int)Math.round(sliderValue * (maxValue - minValue) + minValue));
        }

        if(drawString)
        {
            displayString = dispString + val + suffix;
        }

        if (parent != null)
        {
            parent.onChangeSliderValue(this);
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    @Override
    public void onRelease(double mouseX, double mouseY)
    {
        this.dragging = false;
    }

    public int getValueInt()
    {
        return (int)Math.round(sliderValue * (maxValue - minValue) + minValue);
    }

    public double getValue()
    {
        return sliderValue * (maxValue - minValue) + minValue;
    }

    public void setValue(double d)
    {
        this.sliderValue = (d - minValue) / (maxValue - minValue);
    }

    public static interface ISlider
    {
        void onChangeSliderValue(GuiSlider slider);
    }
}