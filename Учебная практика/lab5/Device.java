package lab5;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Device {
	private String type;
	private String brand;
	private String model;
	private String countryOfOrigin;
	private String code;
	private int price;
	private int amount;

	public Device(String _type, String _brand, String _model, String _countryOfOrigin, String _code, int _price,
			int _amount) {
		this.type = _type;
		this.brand = _brand;
		this.model = _model;
		this.countryOfOrigin = _countryOfOrigin;
		this.code = _code;
		this.price = _price;
		this.amount = _amount;
	}

	public void setType(String _type) {
		this.type = _type;
	}

	public void setBrand(String _brand) {
		this.brand = _brand;
	}

	public void setModel(String _model) {
		this.model = _model;
	}

	public void setCountryOfOrigin(String _countryOfOrigin) {
		this.countryOfOrigin = _countryOfOrigin;
	}

	public void setCode(String _code) {
		this.code = _code;
	}

	public void setPrice(int _price) {
		this.price = _price;
	}

	public void setAmount(int _amount) {
		this.amount = _amount;
	}

	public String getType() {
		return this.type;
	}

	public String getBrand() {
		return this.brand;
	}

	public String getModel() {
		return this.model;
	}

	public String getCountryOfOrigin() {
		return this.countryOfOrigin;
	}

	public String getCode() {
		return this.code;
	}

	public int getPrice() {
		return this.price;
	}

	public int getAmount() {
		return this.amount;
	}

	public String toString() {
		return String.format(
				"Тип устройства - %s, бренд - %s, модель - %s, страна происхождения - %s, код товара - %s, цена - %d BYN, количество - %d\n",
				this.type, this.brand, this.model, this.countryOfOrigin, this.code, this.price, this.amount);
	}

	public static Device Parse(String s) {
		Pattern p = Pattern.compile(
				"Тип устройства - (\\w+), бренд - (\\w+), модель - (\\w+), страна происхождения - (\\w+), код товара - (\\w+), цена - (\\d+) BYN, количество - (\\d+)",
				Pattern.UNICODE_CHARACTER_CLASS);
		Matcher m = p.matcher(s);
		if (m.find()) {
			Device d = new Device(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5),
					Integer.parseInt(m.group(6)), Integer.parseInt(m.group(7)));
			return d;
		}
		return null;
	}
}