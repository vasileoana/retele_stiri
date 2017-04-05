package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import common.Channel;
import common.IClientService;
import common.IServerService;
import common.RegistryManager;
import common.Settings;
import common.Topic;

import org.eclipse.swt.widgets.List;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class Client extends Shell implements IClientService {

	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Client shell = new Client(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IServerService serverService;
	private IClientService clientService;
	private Text etName;
	private Text etDesc;
	private Text etTitlu;
	private Text etStire;
	private Table table;
	private List topicList;
	private Label message;

	public Client(Display display) throws MalformedURLException, RemoteException, NotBoundException {
		super(display, SWT.SHELL_TRIM);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					serverService.unsubscribe(clientService);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		createContents();

		this.serverService = (IServerService) Naming.lookup(MessageFormat.format("rmi://{0}:{1}/{2}",
				Settings.getServerHost(), Integer.toString(Settings.getServerPort()), Settings.getServerService()));

		Registry registry = RegistryManager.getRegistryManager(Settings.getClientPort());
		this.clientService = new ClientService(registry, Settings.getClientService(), this);

		Label lblAvailableChannels = new Label(this, SWT.NONE);
		lblAvailableChannels.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblAvailableChannels.setBounds(10, 32, 112, 15);
		lblAvailableChannels.setText("Available channels:");

		Button btnSubscribe = new Button(this, SWT.NONE);
		btnSubscribe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final TableItem[] items = table.getItems();
				int index = table.getSelectionIndex();
				if (index != -1) {
					try {
						serverService.subscribeToChannel(clientService, items[index].getText());
						message.setText("");
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					message.setText("Selectati un canal de stiri pentru care \n doriti subscribe");
				}
			}
		});

		btnSubscribe.setBounds(10, 224, 75, 25);
		btnSubscribe.setText("Subscribe");

		Button btnUnsubscribe = new Button(this, SWT.NONE);
		btnUnsubscribe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final TableItem[] items = table.getItems();
				int index = table.getSelectionIndex();
				if (index != -1) {
					try {
						serverService.unsubscribeToChannel(clientService, items[index].getText());
						message.setText("");
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					message.setText("Selectati un canal de stiri pentru care \n doriti unsubscribe");
				}
			}
		});
		btnUnsubscribe.setBounds(91, 224, 75, 25);
		btnUnsubscribe.setText("Unsubscribe");

		Group group = new Group(this, SWT.NONE);
		group.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		group.setBounds(287, 51, 128, 167);

		etName = new Text(group, SWT.BORDER);
		etName.setBounds(10, 44, 108, 21);

		Label lblDescription = new Label(group, SWT.NONE);
		lblDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblDescription.setBounds(10, 74, 65, 15);
		lblDescription.setText("Description");

		Label lblName = new Label(group, SWT.NONE);
		lblName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblName.setBounds(10, 23, 55, 15);
		lblName.setText("Name");

		etDesc = new Text(group, SWT.BORDER);
		etDesc.setBounds(10, 95, 108, 51);

		Label lblHelloToMy = new Label(this, SWT.NONE);
		lblHelloToMy.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblHelloToMy.setBounds(136, 10, 169, 15);
		lblHelloToMy.setText("Hello to my News Application");

		Button btnAddNewChannel = new Button(this, SWT.NONE);
		btnAddNewChannel.setBounds(303, 224, 112, 25);
		btnAddNewChannel.setText("Add new channel");
		btnAddNewChannel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					String name = etName.getText().trim();
					String description = etDesc.getText().trim();
					if (name != "" && description != "") {
						Channel channel = new Channel(name, description);
						serverService.publishChannel(clientService, channel);
						etName.setText("");
						etDesc.setText("");
						message.setText("");
						serverService.subscribeToChannel(clientService, name);
					} else {
						message.setText("denumirea si descrierea nu pot fi nule!");
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});

		topicList = new List(this, SWT.BORDER);
		topicList.setBounds(10, 282, 259, 160);
		topicList.add("Denumire canal; " + "Stire; " + "Descriere; ");

		Label lblListOfTopics = new Label(this, SWT.NONE);
		lblListOfTopics.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblListOfTopics.setBounds(10, 261, 75, 15);
		lblListOfTopics.setText("List of topics");

		Group group_1 = new Group(this, SWT.NONE);
		group_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		group_1.setBounds(287, 282, 128, 167);

		etTitlu = new Text(group_1, SWT.BORDER);
		etTitlu.setBounds(10, 44, 108, 21);

		Label lblContinut = new Label(group_1, SWT.NONE);
		lblContinut.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblContinut.setText("Continut");
		lblContinut.setBounds(10, 74, 65, 15);

		Label lblTopic = new Label(group_1, SWT.NONE);
		lblTopic.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblTopic.setText("Topic");
		lblTopic.setBounds(10, 23, 55, 15);

		etStire = new Text(group_1, SWT.BORDER);
		etStire.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				message.setText(
						"Atentie!!! Nu este permisa folosirea cuvintelor \n obscene, precum: urat1, urat2, interzis1, interzis2");
			}
		});
		etStire.setBounds(10, 95, 108, 51);

		Button btnAddTopic = new Button(this, SWT.NONE);
		btnAddTopic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final TableItem[] items = table.getItems();
				int index = table.getSelectionIndex();
				if (index != -1) {
					try {
						String titlu = etTitlu.getText();
						String descriere = etStire.getText();
						Topic topic = new Topic(titlu, descriere);
						serverService.publishTopic(clientService, items[index].getText(), topic);
						etTitlu.setText("");
						etStire.setText("");
						message.setText("");
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					message.setText("Selectati un canal de stiri mai intai, \ndetinut de dumneavoastra!");
				}
			}
		});
		btnAddTopic.setText("Add topic");
		btnAddTopic.setBounds(303, 453, 112, 25);

		Button btnDelete = new Button(this, SWT.NONE);
		btnDelete.setBounds(177, 224, 75, 25);
		btnDelete.setText("Delete");
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				final TableItem[] items = table.getItems();
				int index = table.getSelectionIndex();
				if (index != -1) {
					try {
						serverService.deleteChannel(clientService, items[index].getText());
						serverService.resetTable(clientService);
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					message.setText("Selectati un canal de stiri mai intai, \ndetinut de dumneavoastra!");
				}
			}
		});

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 62, 259, 154);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(115);
		tblclmnNewColumn.setText("Name");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(127);
		tblclmnNewColumn_1.setText("Description");

		message = new Label(this, SWT.NONE);
		message.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		message.setBounds(10, 448, 259, 30);

		serverService.subscribe(clientService);
	}

	protected void createContents() {
		setText("News Application");
		setSize(441, 527);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void receiveChannels(java.util.List<Channel> channels) throws RemoteException {
		for (Channel c : channels) {
			Display.getDefault().asyncExec(() -> {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setText(0, c.getName());
				tableItem.setText(1, c.getDescription());
			});
		}
	}

	@Override
	public void resetTheTable(java.util.List<Channel> channels) throws RemoteException {
		try {
			Display.getDefault().asyncExec(() -> {
				table.removeAll();
			});
			for (Channel c : channels) {
				Display.getDefault().asyncExec(() -> {
					TableItem tableItem = new TableItem(table, SWT.NONE);
					tableItem.setText(0, c.getName());
					tableItem.setText(1, c.getDescription());
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receiveTopics(java.util.List<Topic> topics, String channelName) throws RemoteException {
		Display.getDefault().asyncExec(() -> {
			topicList.removeAll();
			topicList.add("Denumire canal; " + "Stire; " + "Descriere; ");
		});
		for (Topic t : topics) {
			Display.getDefault().asyncExec(() -> {
				topicList.add(channelName + ":         " + t.getName() + ";      " + "      " + t.getText());
			});
		}
	}
}
