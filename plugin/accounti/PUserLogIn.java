package plugin.accounti;

import gui.*;

import java.beans.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import action.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import core.*;
import core.datasource.*;

/**
 * panel de entrada de usuario y contraceña. este panel retorna el registro pasado como argumento del constructor con
 * los campos usuario y contrace;a llenos
 * 
 */
public class PUserLogIn extends AbstractRecordDataInput implements PropertyChangeListener {

	private JCheckBox jcb_rem_usr;
	private JTextField jtf_user_id;
	private Record usrmod;

	/**
	 * nueva instancia
	 * 
	 * @param usr - registro de usuario
	 * 
	 */
	public PUserLogIn() {
		super("security.title04", null, false);

		usrmod = new Record("", new Field[]{new Field("USERNAME", "", 20), new Field("PASSWORD", "", 20)});
		setModel(usrmod);
		addPropertyChangeListener(TConstants.ACTION_PERFORMED, this);
		this.jtf_user_id = TUIUtils.getJTextField("ttusername", (String) usrmod.getFieldValue("username"), 20);
		JPasswordField jtfp = TUIUtils.getJPasswordField("ttpassword", (String) usrmod.getFieldValue("password"), 20);
		jtfp.setText("");
		addInputComponent("username", jtf_user_id, true, true);
		addInputComponent("password", jtfp, true, true);

		// recordar usuario
		boolean bol = (Boolean) TPreferences.getPreference(TPreferences.REMIND_USER, "", false);
		String str = (String) TPreferences.getPreference(TPreferences.USER, "", "");
		jtf_user_id.setText(str);
		this.jcb_rem_usr = TUIUtils.getJCheckBox("security.r05", bol);

		FormLayout lay = new FormLayout("left:pref, 3dlu, 80dlu, 7dlu, left:pref, 3dlu, 80dlu",
				"pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");
		PanelBuilder bui = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		bui.add(getLabelFor("username"), cc.xy(1, 1));
		bui.add(getInputComponent("username"), cc.xy(3, 1));
		bui.add(getLabelFor("password"), cc.xy(5, 1));
		bui.add(getInputComponent("password"), cc.xy(7, 1));
		// bui.add(TUIUtils.getJLabel("i.s04", false, true), cc.xy(1, 3));
		// bui.add(DBselector, cc.xyw(3, 3, 3));
		bui.add(jcb_rem_usr, cc.xyw(1, 5, 3));

		setDefaultActionBar();
		add(bui.getPanel());
		preValidate(null);
	}

	@Override
	public Record getRecord() {
		Record usr = super.getRecord();
		// guarda preferencias
		boolean bol = jcb_rem_usr.isSelected();
		String str = bol ? (String) usr.getFieldValue("username") : "";
		TPreferences.setPreference(TPreferences.REMIND_USER, "", bol);
		TPreferences.setPreference(TPreferences.USER, "", str);
		return usr;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof AceptAction) {
			Record r1 = getRecord();
			String usr = (String) r1.getFieldValue("username");
			String pass = (String) r1.getFieldValue("password");

			String autMet = SystemVariables.getStringVar("autenticationMethod");
			Record r2 = null;
			r2 = autMet.equals("autenticationOracleDB") ? autenticationOracleDB(usr, pass) : null;
			if (r2 != null) {
				Session.setUser(r2);
			}
		}
		if (evt.getNewValue() instanceof CancelAction) {
			Exit.shutdown();
		}
	}

	/**
	 * this method check user and password against oracle database manager. if database connection ok,
	 * <code>autenticationApp</code> method is called to check all internal aplication conditions.
	 * 
	 * @param usr - user
	 * @param pass - password
	 * 
	 * @return user record or <code>null</code> if any error
	 * @see #autenticationApp(String, String)
	 * 
	 */
	private Record autenticationOracleDB(String usr, String pass) {
		Record r2 = null;
		try {
			Record cf = ConnectionManager.getAccessTo("t_connections").exist("t_cnname = 'SleOracle'");
			// 1823: override fields value from table values to values stored in jdbc.properties file
			String[] rp = PUserLogIn.getJdbcProperties("jdbc.url");
			cf.setFieldValue("t_cnurl", rp[0]);
			Class.forName((String) cf.getFieldValue("t_cndriver")).newInstance();
			java.sql.Connection con = java.sql.DriverManager.getConnection((String) cf.getFieldValue("t_cnurl"), usr,
					pass);
			con.close();
			r2 = new Record(usrmod);
			r2.setFieldValue("USERNAME", usr);
			r2.setFieldValue("PASSWORD", pass);
		} catch (Exception e) {
			showAplicationException(new AplicationException("security.msg04", e.getMessage()));
		}
		return r2;
	}

	/**
	 * Retrive the values stored in <code>jdbc.properties</code> file.
	 * <p>
	 * If any error is found try reaching the file or if any parameter is missing , this method log the exception, Show
	 * the exception message and finish the applicacion. (because its relation with security)
	 * 
	 * @param pn - list of properties
	 * @return array with property value in the same order as requested
	 */
	public static String[] getJdbcProperties(String... pn) {
		String[] rs = null;
		SystemLog.info("Looking for parameters in externa file.");
		try {
			Properties cfgp = new Properties();
			cfgp.load(TResourceUtils.getFile("config.properties"));
			Properties dsop = new Properties();
			
			// source dbatabase (include mail properties)
			String dsf = cfgp.getProperty("sourceFolder") + cfgp.getProperty("datasourceFile");
			dsop.load(new FileInputStream(new File(dsf)));
			
			// target dbatabase 
			dsf = cfgp.getProperty("targetFolder") + cfgp.getProperty("targetDatasourceFile");
			dsop.load(new FileInputStream(new File(dsf)));

			// load into main property list
			TStringUtils.loadProperties(dsop);
			rs = new String[pn.length];
			for (int i = 0; i < pn.length; i++) {
				String pv = dsop.getProperty(pn[i]);
				if (pv == null) {
					throw new Exception("Parameter " + pn[i] + " not found.");
				}
				rs[i] = pv;
			}
		} catch (Exception e) {
			SystemLog.severe("Error trying to retrive parameters from external property file.");
			SystemLog.logException1(e, true);
			System.exit(-1);
		}
		return rs;
	}
}
