package net.sleepcraft.scrpmod.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ColorPickerWidget extends ButtonWidget {
    private int color;
    private Consumer<Integer> onColorChange;

    public ColorPickerWidget(int x, int y, int width, int height, Text message, int initialColor, Consumer<Integer> onColorChange) {
        super(x, y, width, height, message, (button) -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.color = initialColor;
        this.onColorChange = onColorChange;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);

        // Рисуем квадрат с текущим цветом
        context.fill(this.getX() + 5, this.getY() + 5, this.getX() + 25, this.getY() + this.height - 5, this.color);
        
        // Рисуем границу квадрата
        context.fill(this.getX() + 4, this.getY() + 4, this.getX() + 26, this.getY() + 5, 0xFF000000);
        context.fill(this.getX() + 4, this.getY() + this.height - 5, this.getX() + 26, this.getY() + this.height - 4, 0xFF000000);
        context.fill(this.getX() + 4, this.getY() + 4, this.getX() + 5, this.getY() + this.height - 4, 0xFF000000);
        context.fill(this.getX() + 25, this.getY() + 4, this.getX() + 26, this.getY() + this.height - 4, 0xFF000000);
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        if (this.onColorChange != null) {
            this.onColorChange.accept(color);
        }
    }

    @Override
    public void onPress() {
        // Циклирует через предустановленные цвета
        int[] colors = {
            0xFF1A1A1A, 0xFF2C2C2C, 0xFF3A3A3A, 0xFF4A4A4A, 0xFF5A5A5A,
            0xFF6B6B6B, 0xFF8B8B8B, 0xFFABABAB, 0xFFCDCDCD
        };

        for (int i = 0; i < colors.length; i++) {
            if (this.color == colors[i]) {
                this.setColor(colors[(i + 1) % colors.length]);
                return;
            }
        }
        this.setColor(colors[0]);
    }
}
