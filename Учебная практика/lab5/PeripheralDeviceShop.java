package lab5;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PeripheralDeviceShop extends JFrame {
	private ArrayList<Device> devices;
	private JButton addButton = new JButton("Добавить устройство");
	private JButton deleteButton = new JButton("Удалить устройство");
	private JTree DeviceTree;
	private JTable DeviceTable;
	private MyTableModel myTableModel;
	private MyTreeModel myTreeModel;

	public static void main(String[] args) {
		PeripheralDeviceShop mainClass = new PeripheralDeviceShop();
		mainClass.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainClass.setVisible(true);
	}

	public PeripheralDeviceShop() throws HeadlessException {
		devices = new ArrayList<Device>();
		addButton.addActionListener(e -> add());
		deleteButton.addActionListener(this::DeleteDevice);
		myTableModel = new MyTableModel();
		DeviceTable = new JTable(myTableModel);
		DeviceTable.setRowSelectionAllowed(true);
		myTreeModel = new MyTreeModel(new TreeNode("Каталог"));
		DeviceTree = new JTree(myTreeModel);
		DeviceTree.addTreeSelectionListener(new addTreeNode());
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new JScrollPane(DeviceTree),
				new JScrollPane(DeviceTable));
		splitPane.setDividerLocation(200);

		JMenuBar menubar = new JMenuBar();
		this.setJMenuBar(menubar);
		JMenu filemenu = new JMenu("File");
		menubar.add(filemenu);
		JMenuItem open = new JMenuItem("Open");
		filemenu.add(open);
		JMenuItem save = new JMenuItem("Save");
		filemenu.add(save);

		open.addActionListener(this::OpenFile);
		save.addActionListener(this::SaveFile);

		getContentPane().add("North", addButton);
		getContentPane().add("South", deleteButton);
		getContentPane().add(splitPane);
		setBounds(100, 100, 900, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void OpenFile(ActionEvent event) {
		JFileChooser fileopen = new JFileChooser();
		int ret = fileopen.showDialog(null, "Открыть файл");
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileopen.getSelectedFile();
			try (FileReader reader = new FileReader(file.getAbsolutePath())) {
				Scanner scan = new Scanner(reader);
				ArrayList<Device> d = new ArrayList<Device>();
				while (scan.hasNextLine()) {
					String text = scan.nextLine();
					d.add(Device.Parse(text));
				}
				for (Device device : d) {
					this.addNewItem(device);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Не удалось загрузить данные из файла", "Ошибка",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void SaveFile(ActionEvent event) {
		JFileChooser fileopen = new JFileChooser();
		int ret = fileopen.showDialog(null, "Сохранить в файл");
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileopen.getSelectedFile();
			try (FileWriter writer = new FileWriter(file.getAbsolutePath(), false)) {
				if (!this.devices.isEmpty()) {
					for (Device d : this.devices) {
						String text = d.toString();
						writer.write(text);
					}
				}
				writer.flush();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Не удалось сохранить данные в файл", "Ошибка",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void addNewItem(Device d) {
		TreeNode temp, where, insert, root = myTreeModel.getRoot();
		try {
			insert = new TreeNode(d.getModel(), d);
			if ((where = findNode(root, d.getType())) != null
					&& (temp = findNode(where, d.getCountryOfOrigin())) != null
					&& (where = findNode(temp, d.getBrand())) != null
					&& (temp = findNode(where, Integer.toString(d.getPrice()))) != null) {
				myTreeModel.insertNodeInto(insert, temp, temp.getChildCount(), false);
			} else if ((where = findNode(root, d.getType())) != null
					&& (temp = findNode(where, d.getCountryOfOrigin())) != null
					&& (where = findNode(temp, d.getBrand())) != null) {
				myTreeModel.insertNodeInto(temp = new TreeNode(Integer.toString(d.getPrice())), where,
						where.getChildCount(), false);
				myTreeModel.insertNodeInto(insert, temp, temp.getChildCount(), false);
			} else if ((where = findNode(root, d.getType())) != null
					&& (temp = findNode(where, d.getCountryOfOrigin())) != null) {
				myTreeModel.insertNodeInto(where = new TreeNode(d.getBrand()), temp, temp.getChildCount(), false);
				myTreeModel.insertNodeInto(temp = new TreeNode(Integer.toString(d.getPrice())), where,
						where.getChildCount(), false);
				myTreeModel.insertNodeInto(insert, temp, temp.getChildCount(), false);
			} else if ((where = findNode(root, d.getType())) != null) {
				myTreeModel.insertNodeInto(temp = new TreeNode(d.getCountryOfOrigin()), where, where.getChildCount(),
						false);
				myTreeModel.insertNodeInto(where = new TreeNode(d.getBrand()), temp, temp.getChildCount(), false);
				myTreeModel.insertNodeInto(temp = new TreeNode(Integer.toString(d.getPrice())), where,
						where.getChildCount(), false);
				myTreeModel.insertNodeInto(insert, temp, temp.getChildCount(), false);
			} else {
				myTreeModel.insertNodeInto(where = new TreeNode(d.getType()), root, root.getChildCount(), false);
				myTreeModel.insertNodeInto(temp = new TreeNode(d.getCountryOfOrigin()), where, where.getChildCount(),
						false);
				myTreeModel.insertNodeInto(where = new TreeNode(d.getBrand()), temp, temp.getChildCount(), false);
				myTreeModel.insertNodeInto(temp = new TreeNode(Integer.toString(d.getPrice())), where,
						where.getChildCount(), false);
				myTreeModel.insertNodeInto(insert, temp, temp.getChildCount(), false);
			}
			this.devices.add(d);
		} catch (Exception e) {
			return;
		}
	}

	public static TreeNode findNode(TreeNode root, String s) {
		Enumeration<javax.swing.tree.TreeNode> e = root.preorderEnumeration();
		if (!e.hasMoreElements()) {
			return null;
		}
		Enumeration<javax.swing.tree.TreeNode> nodes = (Enumeration<javax.swing.tree.TreeNode>) e.nextElement()
				.children();
		while (nodes.hasMoreElements()) {
			javax.swing.tree.TreeNode node = nodes.nextElement();
			if (node.toString().equalsIgnoreCase(s)) {
				return (TreeNode) node;
			}
		}
		return null;
	}

	private class addTreeNode implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			TreeNode node = (TreeNode) DeviceTree.getLastSelectedPathComponent();
			if (node == null) {
				return;
			}
			ArrayList<Device> array = node.getAllNodes();
			myTableModel = new MyTableModel(array);
			DeviceTable.setModel(myTableModel);
		}
	}

	private void add() {
		AddDevice am = new AddDevice(this);
		am.setVisible(true);
	}

	private void DeleteDevice(ActionEvent event) {
		if (DeviceTable.getSelectedRow() == -1) {
			TreeNode selectedNode = (TreeNode) this.DeviceTree.getSelectionPath().getLastPathComponent();
			this.devices.removeAll(selectedNode.getAllNodes());
			this.myTreeModel = (MyTreeModel) this.DeviceTree.getModel();
			((TreeNode) selectedNode.getParent()).removeNode(selectedNode);
			this.myTreeModel.removeNodeFromParent(selectedNode);
			this.myTreeModel.reload();
			return;
		}

		Device d = (Device) this.myTableModel.getValueAt(this.DeviceTable.getSelectedRow());
		this.devices.remove(d);
		TreeNode selectedNode = this.myTreeModel.getRoot();
		selectedNode = this.findNode(selectedNode, d.getType());
		selectedNode = this.findNode(selectedNode, d.getCountryOfOrigin());
		selectedNode = this.findNode(selectedNode, d.getBrand());
		selectedNode = this.findNode(selectedNode, Integer.toString(d.getPrice()));
		selectedNode = this.findNode(selectedNode, d.getModel());
		((TreeNode) selectedNode.getParent()).removeNode(selectedNode);
		this.myTreeModel.removeNodeFromParent(selectedNode);
		this.myTreeModel.reload();

	}

}

class MyTreeModel extends DefaultTreeModel {
	private TreeNode root;

	public MyTreeModel(TreeNode r) {
		super(r);
		root = r;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void insertNodeInto(TreeNode child, TreeNode parent, int i, boolean flag) {
		this.insertNodeInto(child, parent, i);
		parent.addNode(child);
	}
}

class TreeNode extends DefaultMutableTreeNode {
	String name;
	Device nodeDevice = null;
	ArrayList<TreeNode> nodes;
	boolean isThisTheEnd = false;

	public TreeNode() {
		name = "-";
		nodes = new ArrayList<>();
		nodeDevice = null;
		isThisTheEnd = false;
	}

	public TreeNode(String str) {
		name = str;
		nodes = new ArrayList<TreeNode>();
		nodeDevice = null;
		isThisTheEnd = false;
	}

	public TreeNode(String str, Device nbNode) {
		name = str;
		nodes = new ArrayList<TreeNode>();
		nodeDevice = nbNode;
		isThisTheEnd = true;
	}

	public ArrayList<Device> getAllNodes() {
		ArrayList<Device> devices = new ArrayList<Device>();
		Deque<TreeNode> deque = new ArrayDeque<TreeNode>();
		TreeNode temp;
		deque.push(this);
		while (!deque.isEmpty()) {
			temp = deque.removeFirst();
			if (temp.isThisTheEnd) {
				devices.add(temp.getNodeDevice());
			} else {
				for (int i = 0; i < temp.nodes.size(); i++) {
					deque.push(temp.nodes.get(i));
				}
			}
		}
		return devices;
	}

	public void addNode(TreeNode tn) {
		nodes.add(tn);
	}

	public void removeNode(TreeNode tn) {
		nodes.remove(tn);
	}

	public Device getNodeDevice() {
		return nodeDevice;
	}

	public ArrayList<TreeNode> getNodes() {
		return nodes;
	}

	public String toString() {
		return name;
	}
}