package spinnery.widget;

import com.google.gson.annotations.SerializedName;
import org.lwjgl.glfw.GLFW;
import spinnery.client.BaseRenderer;
import spinnery.registry.ResourceRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WDropdown extends WWidget implements WClient, WCollection {
	public List<List<WWidget>> dropdownWidgets = new ArrayList<>();
	protected boolean state = false;
	protected WDropdown.Theme drawTheme;
	protected int[][] sizes = new int[2][2];

	public WDropdown(WAnchor anchor, int positionX, int positionY, int positionZ, int sizeX1, int sizeY1, int sizeX2, int sizeY2, WInterface linkedPanel) {
		setInterface(linkedPanel);

		setAnchor(anchor);

		setAnchoredPositionX(positionX);
		setAnchoredPositionY(positionY);
		setPositionZ(positionZ);

		setSizes(sizeX1, sizeY1, sizeX2, sizeY2);

		setTheme("default");

		updateHidden();
	}

	public List<List<WWidget>> getDropdownWidgets() {
		return dropdownWidgets;
	}

	public void setDropdownWidgets(List<List<WWidget>> dropdownWidgets) {
		this.dropdownWidgets = dropdownWidgets;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void setSizes(int sizeX1, int sizeY1, int sizeX2, int sizeY2) {
		sizes = new int[][] {{sizeX1, sizeY1}, {sizeX2, sizeY2}};
	}

	@Override
	public List<WWidget> getWidgets() {
		List<WWidget> widgets = new ArrayList<>();
		for (List<WWidget> widgetA : getDropdownWidgets()) {
			widgets.addAll(widgetA);
		}
		return widgets;
	}

	@Override
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (getFocus() && mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			setState(!getState());
			updateHidden();
		}

		super.onMouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void setTheme(String theme) {
		if (getInterface().isClient()) {
			super.setTheme(theme);
			drawTheme = ResourceRegistry.get(getTheme()).getWDropdownTheme();
		}
	}

	@Override
	public int getSizeX() {
		return sizes[!getState() ? 0 : 1][0];
	}

	@Override
	public int getSizeY() {
		return sizes[!getState() ? 0 : 1][1];
	}

	@Override
	public boolean scanFocus(int mouseX, int mouseY) {
		super.scanFocus(mouseX, mouseY);

		setFocus(isWithinBounds(mouseX, mouseY) && getWidgets().stream().noneMatch((WWidget::getFocus)));

		return getFocus();
	}

	@Override
	public void draw() {
		if (isHidden()) {
			return;
		}

		BaseRenderer.drawPanel(getPositionX(), getPositionY(), getPositionZ(), getSizeX(), getSizeY() + 1.75, drawTheme.getShadow(), drawTheme.getBackground(), drawTheme.getHighlight(), drawTheme.getOutline());

		if (hasLabel()) {
			BaseRenderer.getTextRenderer().drawWithShadow(getLabel().asFormattedString(), getPositionX() + getSizeX() / 2 - BaseRenderer.getTextRenderer().getStringWidth(getLabel().asFormattedString()) / 2, positionY + 6, drawTheme.getLabel().RGB);
			BaseRenderer.drawRectangle(positionX, positionY + 16, positionZ, getSizeX(), 1, drawTheme.getOutline());
			BaseRenderer.drawRectangle(positionX + 1, positionY + 17, positionZ, getSizeX() - 2, 0.75, drawTheme.getShadow());
		}

		if (getState()) {
			for (List<WWidget> widgetB : getDropdownWidgets()) {
				for (WWidget widgetC : widgetB) {
					widgetC.draw();
				}
			}
		}
	}

	public void updatePositions() {
		int y = positionY + sizes[0][1] + 4;

		for (List<WWidget> widgetA : getDropdownWidgets()) {
			int x = getPositionX() + 4;
			for (WWidget widgetB : widgetA) {
				widgetB.setPositionX(x);
				widgetB.setPositionY(y);
				x += widgetB.getSizeX() + 2;
			}
			y += widgetA.get(0).getSizeY() + 2;
		}
	}

	public void updateHidden() {
		for (List<WWidget> widgetB : getDropdownWidgets()) {
			for (WWidget widgetC : widgetB) {
				widgetC.setHidden(!getState());
			}
		}
	}

	public void add(WWidget... widgetArray) {
		getDropdownWidgets().add(Arrays.asList(widgetArray));
		updateHidden();
		updatePositions();
	}

	public void remove(WWidget... widgetArray) {
		getDropdownWidgets().remove(Arrays.asList(widgetArray));
		updateHidden();
		updatePositions();
	}

	public class Theme extends WWidget.Theme {
		transient private WColor shadow;
		transient private WColor background;
		transient private WColor highlight;
		transient private WColor outline;
		transient private WColor label;

		@SerializedName("shadow")
		private String rawShadow;

		@SerializedName("background")
		private String rawBackground;

		@SerializedName("highlight")
		private String rawHighlight;

		@SerializedName("outline")
		private String rawOutline;

		@SerializedName("label")
		private String rawLabel;

		public void build() {
			shadow = new WColor(rawShadow);
			background = new WColor(rawBackground);
			highlight = new WColor(rawHighlight);
			outline = new WColor(rawOutline);
			label = new WColor(rawLabel);
		}

		public WColor getShadow() {
			return shadow;
		}

		public WColor getBackground() {
			return background;
		}

		public WColor getHighlight() {
			return highlight;
		}

		public WColor getOutline() {
			return outline;
		}

		public WColor getLabel() {
			return label;
		}
	}
}
