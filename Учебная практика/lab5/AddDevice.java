package lab5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddDevice extends JFrame {
	private PeripheralDeviceShop shop;
	private JLabel type = new JLabel("Тип устройства");
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
	private JButton ok_Button = new JButton("OK");

	public AddDevice(PeripheralDeviceShop s) throws HeadlessException {
		Container c = getContentPane();
		this.shop = s;
		ok_Button.addActionListener(new ok_Listner());
		this.setTitle("Добавление устройства");
		this.setBounds(400, 200, 400, 150);
		this.setSize(350, 400);
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
		panel.add(ok_Button);
		c.add(panel);
	}

	private class ok_Listner implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (text_type.equals("") || text_brand.equals("") || text_model.equals("") || text_country.equals("")
					|| text_code.equals("") || text_price.equals("") || text_amount.equals("")) {
				JOptionPane.showMessageDialog(null, "Все поля должны быть заполнены", "Ошибка",
						JOptionPane.WARNING_MESSAGE);
			} else {
				try {
					Device d = new Device(text_type.getText(), text_brand.getText(), text_model.getText(),
							text_country.getText(), text_code.getText(), Integer.parseInt(text_price.getText()),
							Integer.parseInt(text_amount.getText()));
					shop.addNewItem(d);
					dispose();
				} catch (NumberFormatException x) {
					JOptionPane.showMessageDialog(null, "Цена и количество - числовые значения", "Ошибка",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}
}