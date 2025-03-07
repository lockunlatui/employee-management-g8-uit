package gui;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {

	/**
	 * 
	 * @author Do Xuan Loc - 24410063@ms.uit.edu.vn
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new MainFrame();
			}
		});

	}

}
