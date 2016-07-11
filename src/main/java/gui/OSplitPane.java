package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.sun.org.apache.xpath.internal.operations.Gt;

public class OSplitPane extends JSplitPane {

	public OSplitPane(int orientation, Component component1, Component component2) {
		super(orientation, component1, component2);
		setBorder(BorderFactory.createEmptyBorder());
		setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this) {
					public void setBorder(Border b) {
					}

					@Override
					public void paint(Graphics g) {
						g.setColor(GuiColours.SPLTPNE_SLIDER);
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
				return divider;
			}
		});

	}

}
