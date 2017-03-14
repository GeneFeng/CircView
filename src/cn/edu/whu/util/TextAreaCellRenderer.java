package cn.edu.whu.util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

	public TextAreaCellRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		// Component renderer =
		// DEFAULT_RENDERER.getTableCellRendererComponent(table, value,
		// isSelected, hasFocus, row,
		// column);
		// Color foreground, background;
		// if (isSelected) {
		// foreground = Color.YELLOW;
		// background = Color.GREEN;
		// } else {
		// if (row % 2 == 0) {
		// foreground = Color.GRAY;
		// background = Color.WHITE;
		// } else {
		// foreground = Color.WHITE;
		// background = Color.GRAY;
		// }
		// }
		// renderer.setForeground(foreground);
		// renderer.setBackground(background);

		// 计算当下行的最佳高度
		int maxPreferredHeight = 0;
		for (int i = 0; i < table.getColumnCount(); i++) {
			setText("" + table.getValueAt(row, i));
			setSize(table.getColumnModel().getColumn(column).getWidth(), 0);
			maxPreferredHeight = Math.max(maxPreferredHeight, getPreferredSize().height);
		}

		if (table.getRowHeight(row) != maxPreferredHeight)// 少了这行则处理器瞎忙
			table.setRowHeight(row, maxPreferredHeight);
		setText(value == null ? "" : value.toString());

		return this;
		// return renderer;
	}
}