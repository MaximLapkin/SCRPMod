package com.scrp.client.screen;

import com.scrp.config.GUIConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GUIThemeScreen extends Screen {
    private final Screen parent;
    private SliderWidget transparencySlider;
    private ColorPickerWidget backgroundColorPicker;
    private ColorPickerWidget slotColorPicker;
    private ColorPickerWidget borderColorPicker;

    public GUIThemeScreen(Screen parent) {
        super(Text.literal("GUI Theme Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        // Прозрачность слайдер
        int sliderY = this.height / 4;
        this.transparencySlider = new SliderWidget(this.width / 4, sliderY, this.width / 2, 20, 
                Text.literal("Transparency: "), GUIConfig.getTransparency()) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Transparency: " + String.format("%.2f", this.getValue())));
            }

            @Override
            protected void applyValue() {
                GUIConfig.setTransparency((float) this.getValue());
            }
        };
        this.addDrawableChild(this.transparencySlider);

        // Кнопка выбора цвета фона
        this.backgroundColorPicker = new ColorPickerWidget(this.width / 4, sliderY + 40, this.width / 2, 20,
                Text.literal("Background Color"), GUIConfig.getBackgroundColor(), (color) -> {
                    GUIConfig.setBackgroundColor(color);
                });
        this.addDrawableChild(this.backgroundColorPicker);

        // Кнопка выбора цвета слотов
        this.slotColorPicker = new ColorPickerWidget(this.width / 4, sliderY + 80, this.width / 2, 20,
                Text.literal("Slot Color"), GUIConfig.getSlotColor(), (color) -> {
                    GUIConfig.setSlotColor(color);
                });
        this.addDrawableChild(this.slotColorPicker);

        // Кнопка выбора цвета окантовки
        this.borderColorPicker = new ColorPickerWidget(this.width / 4, sliderY + 120, this.width / 2, 20,
                Text.literal("Border Color"), GUIConfig.getBorderColor(), (color) -> {
                    GUIConfig.setBorderColor(color);
                });
        this.addDrawableChild(this.borderColorPicker);

        // Кнопка сброса
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset to Default"), (button) -> {
            GUIConfig.reset();
            this.init(this.client, this.width, this.height);
        }).dimensions(this.width / 4, sliderY + 160, this.width / 2, 20).build());

        // Кнопка закрытия
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), (button) -> {
            this.close();
        }).dimensions(this.width / 4, sliderY + 190, this.width / 2, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Заголовок
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
