package core;

import java.awt.*;

import javax.swing.*;

import core.datasource.*;

public class TSplash extends JWindow {

	private JLabel progressLabel;

	public TSplash() {
		super();
//		ImageIcon s_fg = TResourceUtils.getIcon("Splash_fg");
//		Rectangle bound = new Rectangle(0, 0, s_fg.getIconWidth(), s_fg.getIconHeight());
		Rectangle bound = new Rectangle(0, 0, 470, 290);
		JLabel verjl = new JLabel("IContable " + SystemVariables.getStringVar("versionID"));
		verjl.setFont(new Font("Dosis", Font.BOLD, 50));
		verjl.setForeground(new Color(128, 128, 128, 128));
		verjl.setBounds(10, 190, 400, 45);

//		JLabel img_fg = new JLabel(s_fg);
		JLabel img_fg = new JLabel();
		img_fg.setOpaque(true);
		img_fg.setBackground(Color.black);
		img_fg.setBounds(bound);

		progressLabel = new JLabel();
		progressLabel.setFont(new Font("Verdana", Font.PLAIN, 11));
		progressLabel.setForeground(Color.GRAY);
		progressLabel.setBounds(18, 246, 300, 16);

		JPanel cont = new JPanel(null);
		cont.add(progressLabel);
		cont.add(verjl);
		cont.add(img_fg);
		setContentPane(cont);
//		setSize(s_fg.getIconWidth(), s_fg.getIconHeight());
		setSize(470,290);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * incrementa la barra de progreso y presenta el argumento de entrada
	 * 
	 * @param act - txto (no id)
	 */
	public void increment(String act) {
		progressLabel.setText(act);
	}
}