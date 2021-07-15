import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.DigitalOutput;

public class WinBuilder {

	DigitalOutput redLED;
	JButton btnNewButton;

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WinBuilder window = new WinBuilder();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 *
	 */
	public WinBuilder() throws Exception {

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	protected void initialize() {

		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {

				try {
					setUp();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		btnNewButton = new JButton("LEDOn");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					buttonClick();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(164, 120, 114, 45);
		frame.getContentPane().add(btnNewButton);
	}

	private void setUp() throws Exception {

		redLED = new DigitalOutput();

		redLED.setHubPort(1);
		redLED.setIsHubPortDevice(true);
		btnNewButton.setEnabled(false);

		redLED.addAttachListener(new AttachListener() {

			@Override
			public void onAttach(AttachEvent e) {

				btnNewButton.setEnabled(true);

			}
		});

		redLED.addDetachListener(new DetachListener() {

			@Override
			public void onDetach(DetachEvent e) {
				
				btnNewButton.setEnabled(false);

			}

		});

		redLED.open();

	}

	private void buttonClick() throws Exception {

		if (redLED.getState())
			btnNewButton.setText("LED On");
		else
			btnNewButton.setText("LED Off");

		redLED.setState(!redLED.getState());

	}

}
