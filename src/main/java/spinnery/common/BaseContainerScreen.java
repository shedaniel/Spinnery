package spinnery.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import spinnery.widget.WCollection;
import spinnery.widget.WInterfaceHolder;
import spinnery.widget.WSlot;
import spinnery.widget.WWidget;

public class BaseContainerScreen<T extends BaseContainer> extends AbstractContainerScreen<T> {
	double tooltipX = 0;
	double tooltipY = 0;
	WSlot drawSlot;

	public BaseContainerScreen(Text name, T linkedContainer, PlayerEntity player) {
		super(linkedContainer, player.inventory, name);
		resizeAll();
		linkedContainer.tick();
	}

	public WInterfaceHolder getInterfaces() {
		return getLinkedContainer().getInterfaces();
	}

	public T getLinkedContainer() {
		return super.container;
	}

	public WSlot getDrawSlot() {
		return drawSlot;
	}

	public void setDrawSlot(WSlot drawSlot) {
		this.drawSlot = drawSlot;
	}

	public double getTooltipX() {
		return tooltipX;
	}

	public void setTooltipX(double tooltipX) {
		this.tooltipX = tooltipX;
	}

	public double getTooltipY() {
		return tooltipY;
	}

	public void setTooltipY(double tooltipY) {
		this.tooltipY = tooltipY;
	}

	public void resizeAll() {
		super.containerWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
		super.containerHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
		super.width = MinecraftClient.getInstance().getWindow().getScaledWidth();
		super.height = MinecraftClient.getInstance().getWindow().getScaledHeight();
		super.x = 0;
		super.y = 0;
		getLinkedContainer().setPositionX(super.x);
		getLinkedContainer().setPositionY(super.y);
	}

	public void drawTooltip() {
		if (getDrawSlot() != null && getLinkedContainer().getLinkedPlayerInventory().getCursorStack().isEmpty() && !getDrawSlot().getStack().isEmpty()) {
			this.renderTooltip(getDrawSlot().getStack(), (int) getTooltipX(), (int) getTooltipY());
		}
	}

	public void updateTooltip(double mouseX, double mouseY) {
		setDrawSlot(null);
		for (WWidget widgetA : getInterfaces().getWidgets()) {
			if (widgetA.getFocus() && widgetA instanceof WSlot) {
				setDrawSlot((WSlot) widgetA);
				setTooltipX(mouseX);
				setTooltipY(mouseY);
			} else if (widgetA instanceof WCollection) {
				for (WWidget widgetB : ((WCollection) widgetA).getWidgets()) {
					if (widgetB.scanFocus(mouseX, mouseY) && widgetB instanceof WSlot) {
						setDrawSlot((WSlot) widgetB);
						setTooltipX(mouseX);
						setTooltipY(mouseY);
					}
				}
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tick) {
		getInterfaces().draw();

		drawTooltip();

		super.render(mouseX, mouseY, tick);
	}

	@Override
	protected void drawMouseoverTooltip(int mouseX, int mouseY) {
		getInterfaces().drawMouseoverTooltip(mouseX, mouseY);

		super.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawBackground(float tick, int mouseX, int mouseY) {
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int int_1, int int_2, int int_3) {
		return false;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void tick() {
		getLinkedContainer().tick();
		getInterfaces().tick();
		super.tick();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		getInterfaces().onMouseClicked(mouseX, mouseY, mouseButton);

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		getInterfaces().onMouseReleased(mouseX, mouseY, mouseButton);

		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
		getInterfaces().onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);

		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double deltaY) {
		getInterfaces().onMouseScrolled(mouseX, mouseY, deltaY);

		return false;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		getInterfaces().mouseMoved(mouseX, mouseY);

		updateTooltip(mouseX, mouseY);
	}

	@Override
	public boolean keyReleased(int character, int keyCode, int keyModifier) {
		getInterfaces().onKeyReleased(character, keyCode, keyModifier);

		return false;
	}

	@Override
	public boolean charTyped(char character, int keyCode) {
		getInterfaces().onCharTyped(character, keyCode);

		return super.charTyped(character, keyCode);
	}

	@Override
	public boolean keyPressed(int character, int keyCode, int keyModifier) {
		getInterfaces().keyPressed(character, keyCode, keyModifier);

		if (character == GLFW.GLFW_KEY_ESCAPE) {
			minecraft.player.closeContainer();
			return true;
		} else {
			return false;
		}
	}
}