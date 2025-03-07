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
				JFrame jframe = new JFrame("Employee Management");
				jframe.setSize(1366, 768);
				jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jframe.setVisible(true);
			}
		});

	}

}
