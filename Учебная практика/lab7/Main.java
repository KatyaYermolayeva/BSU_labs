package lab7;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;

public class Main {
	static Connection connection = null;
	static JFrame frame;

	public static void main(String[] args) {
		frame = new JFrame("PeripheralDeviceShop");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		JMenuBar menuBar = new JMenuBar();
		JMenuItem createMenuItem = new JMenuItem("Создать базу данных");
		JMenuItem openMenuItem = new JMenuItem("Открыть базу данных");
		createMenuItem.addActionListener(new createDB());
		openMenuItem.addActionListener(new openDB());
		frame.add(new JScrollPane());
		JMenu openTableMenu = new JMenu("Открыть таблицу");
		JMenu addRecord = new JMenu("Добавить запись");
		JMenu deleteRecord = new JMenu("Удалить запись");

		JMenuItem addDevice = new JMenuItem("Добавить устройство");
		addDevice.addActionListener(new addDevice());
		JMenuItem addSale = new JMenuItem("Добавить запись о продаже");
		addSale.addActionListener(new addSale());
		JMenuItem addType = new JMenuItem("Добавить новый тип устройства");
		addType.addActionListener(new addType());
		JMenuItem addExistingDevice = new JMenuItem("Пополнить склад");
		addExistingDevice.addActionListener(new addDevices());

		addRecord.add(addDevice);
		addRecord.add(addSale);
		addRecord.add(addType);
		addRecord.add(addExistingDevice);

		JMenuItem deleteDevice = new JMenuItem("Удалить устройство");
		deleteDevice.addActionListener(new deleteDevice());
		JMenuItem deleteSale = new JMenuItem("Удалить запись о продаже");
		deleteSale.addActionListener(new deleteSale());

		deleteRecord.add(deleteDevice);
		deleteRecord.add(deleteSale);

		JMenuItem openSales = new JMenuItem("Открыть таблицу продаж");
		openSales.addActionListener(new openTable(1));
		JMenuItem openDevices = new JMenuItem("Открыть таблицу устройств");
		openDevices.addActionListener(new openTable(2));
		JMenuItem openTypes = new JMenuItem("Открыть таблицу типов");
		openTypes.addActionListener(new openTable(3));
		openTableMenu.add(openSales);
		openTableMenu.add(openDevices);
		openTableMenu.add(openTypes);
		JMenuItem closeDB = new JMenuItem("Закрыть базу данных");
		closeDB.addActionListener(new closeDB());

		openTableMenu.setEnabled(false);
		addRecord.setEnabled(false);
		closeDB.setEnabled(false);
		deleteRecord.setEnabled(false);

		menuBar.add(createMenuItem);
		menuBar.add(openMenuItem);
		menuBar.add(openTableMenu);
		menuBar.add(addRecord);
		menuBar.add(deleteRecord);
		menuBar.add(closeDB);
		frame.setSize(1000, 500);
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
	}

	static class closeDB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				connection.close();
				JMenuBar mb = Main.frame.getJMenuBar();
				for (int i = 2; i < mb.getComponents().length; i++) {
					mb.getComponent(i).setEnabled(false);
				}
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
			}
		}
	}

	static class addDevice implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				new addDeviceFrame();
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
			}
		}

		class addDeviceFrame extends JFrame {
			private JLabel type = new JLabel("Тип устройства (ID типа)");
			private JLabel brand = new JLabel("Бренд");
			private JLabel model = new JLabel("Модель");
			private JLabel country = new JLabel("Производитель (страна)");
			private JLabel code = new JLabel("Код товара");
			private JLabel price = new JLabel("Цена (BYN)");
			private JLabel amount = new JLabel("Количество");

			private JTextField text_type = new JTextField();
			private JTextField text_brand = new JTextField();
			private JTextField text_model = new JTextField();
			private JTextField text_country = new JTextField();
			private JTextField text_code = new JTextField();
			private JTextField text_price = new JTextField();
			private JTextField text_amount = new JTextField();
			private JButton ok_button = new JButton("OK");

			public addDeviceFrame() {
				Container c = getContentPane();
				this.setTitle("Добавление устройства");
				this.setBounds(400, 200, 350, 400);
				this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

				panel.add(type);
				panel.add(text_type);
				panel.add(brand);
				panel.add(text_brand);
				panel.add(model);
				panel.add(text_model);
				panel.add(country);
				panel.add(text_country);
				panel.add(code);
				panel.add(text_code);
				panel.add(price);
				panel.add(text_price);
				panel.add(amount);
				panel.add(text_amount);
				ActionListener OKAction = new OKButtonActionListener(1, this);
				ok_button.addActionListener(OKAction);
				panel.add(ok_button);
				c.add(panel);
				this.setVisible(true);
			}
		}
	}

	static class addDevices implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				int deviceID = Integer.parseInt(JOptionPane.showInputDialog(frame, "Введите ID устройства", "1"));
				int n = Integer.parseInt(JOptionPane.showInputDialog(frame, "Введите количество", "1"));
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery("SELECT count(*) AS total FROM Devices WHERE ID = " + deviceID);
				rs.next();
				int k = rs.getInt(1);
				if (k == 0) {
					JOptionPane.showMessageDialog(frame,
							"База данных не содержит информации об устройстве с ID " + deviceID);
				} else
					st.executeUpdate("UPDATE Devices SET amount = amount + " + n + " WHERE ID = " + deviceID);
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
			}
		}
	}

	static class addType implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				new addTypeFrame();
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
			}
		}

		class addTypeFrame extends JFrame {
			private JLabel name = new JLabel("Тип устройства");
			private JLabel description = new JLabel("Описание");

			private JTextField text_name = new JTextField();
			private JTextField text_description = new JTextField();

			private JButton ok_button = new JButton("OK");

			public addTypeFrame() {
				Container c = getContentPane();
				this.setTitle("Добавление устройства");
				this.setBounds(400, 200, 350, 200);
				this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

				panel.add(name);
				panel.add(text_name);
				panel.add(description);
				panel.add(text_description);
				ActionListener OKAction = new OKButtonActionListener(3, this);
				ok_button.addActionListener(OKAction);
				panel.add(ok_button);
				c.add(panel);
				this.setVisible(true);
			}
		}
	}

	static class addSale implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				new addSaleFrame();
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
			}
		}

		class addSaleFrame extends JFrame {
			private JLabel code = new JLabel("Код устройства");
			private JLabel amount = new JLabel("Количество");
			private JLabel date = new JLabel("Дата (гггг-мм-дд)");

			private JTextField text_code = new JTextField();
			private JTextField text_amount = new JTextField();
			private JTextField text_date = new JTextField();

			private JButton ok_button = new JButton("OK");

			public addSaleFrame() {
				Container c = getContentPane();
				this.setTitle("Добавление записи о продаже");
				this.setBounds(500, 400, 350, 260);
				this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

				panel.add(code);
				panel.add(text_code);
				panel.add(amount);
				panel.add(text_amount);
				panel.add(date);
				panel.add(text_date);

				ActionListener OKAction = new OKButtonActionListener(2, this);
				ok_button.addActionListener(OKAction);
				panel.add(ok_button);
				c.add(panel);
				this.setVisible(true);
			}
		}
	}

	static class OKButtonActionListener implements ActionListener {
		int mode;
		JFrame f;
		public ArrayList<String> data;

		public OKButtonActionListener(int i, JFrame _f) {
			mode = i;
			f = _f;
			data = new ArrayList<String>();
		}

		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent e) {
			try {
				for (Component i : ((JPanel) f.getContentPane().getComponent(0)).getComponents()) {
					if (i.getClass() == JTextField.class) {
						data.add(((JTextField) i).getText());
					}
				}
				Statement st = connection.createStatement();
				switch (mode) {
				case 1:
					st.executeUpdate(
							"INSERT INTO Devices (type_ID, brand, model, country_of_origin, code, price, amount) "
									+ "VALUES (" + Integer.parseInt(data.get(0)) + ", '" + data.get(1) + "', '"
									+ data.get(2) + "', '" + data.get(3) + "', '" + data.get(4) + "', "
									+ Double.parseDouble(data.get(5)) + ", " + Integer.parseInt(data.get(6)) + ")");
					break;
				case 2:
					st.executeUpdate(
							"INSERT INTO Sales (device_ID, amount, date) " + "VALUES (" + Integer.parseInt(data.get(0))
									+ ", " + Integer.parseInt(data.get(1)) + ", '" + data.get(2) + "')");
					break;
				case 3:
					st.executeUpdate("INSERT INTO DeviceTypes (name, description) " + "VALUES ('" + data.get(0) + "', '"
							+ data.get(1) + "')");
					break;
				}
				f.setVisible(false);
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
				JOptionPane.showMessageDialog(frame, "Не удалось добавить запись");
			}
		}
	}

	static class deleteDevice implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				int i = Integer.parseInt(
						JOptionPane.showInputDialog(frame, "Введите ID устройства, которое хотите удалить", "1"));
				Statement st = connection.createStatement();
				st.executeUpdate("DELETE from Sales WHERE device_ID = " + i);
				st.executeUpdate("DELETE from Devices WHERE ID = " + i);
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
				JOptionPane.showMessageDialog(frame, "Не удалось удалить запись ");
			}
		}
	}

	static class deleteSale implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				int i = Integer.parseInt(
						JOptionPane.showInputDialog(frame, "Введите ID записи о продаже, которую хотите удалить", "1"));
				Statement st = connection.createStatement();
				st.executeUpdate("DELETE from Sales WHERE sale_ID = " + i);
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
				JOptionPane.showMessageDialog(frame, "Не удалось удалить запись ");
			}
		}
	}

	static class openTable implements ActionListener {
		int mode;

		public openTable(int m) {
			if (m < 1 || m > 3) {
				throw new IllegalArgumentException("Invalid open mode");
			}
			mode = m;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				Statement st = connection.createStatement();
				ResultSet data = null;
				switch (mode) {
				case 1:
					data = st.executeQuery("SELECT * FROM Sales");
					break;
				case 2:
					data = st.executeQuery(
							"SELECT Devices.ID, brand, model, country_of_origin, DeviceTypes.name as type, code, price, amount"
									+ " FROM Devices JOIN DeviceTypes on Devices.type_ID=DeviceTypes.ID");
					break;
				case 3:
					data = st.executeQuery("SELECT * FROM DeviceTypes");
					break;
				}
				String[] columnNames = new String[data.getMetaData().getColumnCount()];
				for (int i = 0; i < columnNames.length; i++) {
					columnNames[i] = data.getMetaData().getColumnName(i + 1);
				}
				Object[] rows = new Object[columnNames.length];
				DefaultTableModel mTableModel = new DefaultTableModel(null, columnNames);
				while (data.next()) {
					for (int i = 0; i < columnNames.length; i++) {
						rows[i] = data.getString(i + 1);
					}
					mTableModel.addRow(rows);
				}
				JTable table = new JTable(mTableModel);
				JScrollPane pane = new JScrollPane(table);
				JPanel c = (JPanel) frame.getContentPane();
				c.removeAll();
				c.add(pane);
				frame.setContentPane(c);
				st.close();
			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
				JOptionPane.showMessageDialog(frame, "Не удалось открыть таблицу ");
			}
		}
	}

	static class createDB implements ActionListener {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String connect = "jdbc:derby:";

		public void actionPerformed(ActionEvent e) {
			System.setProperty("derby.system.home", "C:\\учеба\\4.2 сем\\УП\\Лабораторные\\lab7");
			String DBName = JOptionPane.showInputDialog(frame, "Введите имя базы данных", "PeripheralDeviceShopDB");
			if (DBName == null) {
				return;
			}
			connect += DBName;
			connect += ";create=true";
			try {
				// Регистрируем драйвер JDBC
				Class.forName(driver);
				// Подключаемся к БД
				Connection conn = DriverManager.getConnection(connect);
				// Выполняем запросы
				Statement st = conn.createStatement();
				st.executeUpdate("CREATE TABLE DeviceTypes "
						+ "(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, "
						+ "name VARCHAR(300) NOT NULL, " + "description VARCHAR(500) NOT NULL)");
				st.executeUpdate("CREATE TABLE Devices "
						+ "(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,  INCREMENT BY 1) PRIMARY KEY, "
						+ "brand VARCHAR(300) NOT NULL, model VARCHAR(500) NOT NULL, "
						+ "country_of_origin VARCHAR(300) NOT NULL, "
						+ "type_ID INT NOT NULL REFERENCES DeviceTypes(ID), code VARCHAR(20) NOT NULL,"
						+ "price REAL NOT NULL, amount INT NOT NULL)");
				st.executeUpdate("CREATE TABLE Sales "
						+ "(sale_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,  INCREMENT BY 1) PRIMARY KEY, "
						+ "device_ID INT NOT NULL REFERENCES Devices(ID), "
						+ "amount INT NOT NULL, date DATE NOT NULL)");
				st.close();
				JOptionPane.showMessageDialog(frame, "База данных " + DBName + " успешно создана");
				conn.close();

			} catch (Exception ex) {
				System.err.println("Run-time error: " + ex);
				JOptionPane.showMessageDialog(frame, "Не удалось создать базу данных " + DBName);
			}
		}
	}

	static class openDB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			new openDBFrame();
		}
	}

	static class openDBFrame extends JFrame {
		private JButton openDBButton = new JButton("Открыть базу данных");
		static private JTextField DBName = new JTextField();

		public openDBFrame() {
			openDBButton.addActionListener(new openDBActionListener());
			this.setTitle("Открытие базы данных");
			DBName.setText("PeripheralDeviceShopDB");
			this.setBounds(500, 400, 350, 130);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Container c = getContentPane();
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(Box.createRigidArea(new Dimension(0, 20)));
			panel.add(DBName);
			panel.add(Box.createRigidArea(new Dimension(120, 20)));
			panel.add(openDBButton);
			c.add(panel);
			this.setVisible(true);
		}

		class openDBActionListener implements ActionListener {
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			String connect = "jdbc:derby:";

			public void actionPerformed(ActionEvent e) {
				String name = DBName.getText();
				connect += name;
				try {
					Class.forName(driver);
					Connection conn = DriverManager.getConnection(connect);
					Main.connection = conn;
					JOptionPane.showMessageDialog(frame, "База данных " + name + " успешно открыта");
					for (Component i : Main.frame.getJMenuBar().getComponents()) {
						i.setEnabled(true);
					}
				} catch (Exception ex) {
					System.err.println("Run-time error: " + ex);
					JOptionPane.showMessageDialog(frame, "Не удалось открыть базу данных " + DBName);
				}
				dispose();
			}
		}
	}
}
