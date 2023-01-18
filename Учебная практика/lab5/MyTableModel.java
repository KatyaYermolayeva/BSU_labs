package lab5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class MyTableModel implements TableModel {
	static final String[] columnNames = new String[] { "Тип", "Бренд", "Модель", "Страна производства", "Код",
			"Цена (BYN)", "Количество" };
	static final Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class,
			String.class, Integer.class, Integer.class };
	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	private ArrayList<Device> infoNodes;

	public MyTableModel() {
		infoNodes = new ArrayList<Device>();
	}

	public MyTableModel(ArrayList<Device> al) {
		this.infoNodes = al;
	}

	public void setInfoArray(ArrayList<Device> al) {
		infoNodes = al;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return infoNodes.size();
	}

	public Class getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}

	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Device lp = infoNodes.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return lp.getType();
		case 1:
			return lp.getBrand();
		case 2:
			return lp.getModel();
		case 3:
			return lp.getCountryOfOrigin();
		case 4:
			return lp.getCode();
		case 5:
			return lp.getPrice();
		case 6:
			return lp.getAmount();
		}
		return "";
	}

	public Object getValueAt(int rowIndex) {
		Device lp = infoNodes.get(rowIndex);
		return lp;
	}

	public void addTableModelListener(TableModelListener listener) {
		listeners.add(listener);
	}

	public void removeTableModelListener(TableModelListener listener) {
		listeners.remove(listener);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	}
}