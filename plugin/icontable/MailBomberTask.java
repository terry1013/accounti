package plugin.icontable;

import java.io.*;
import java.util.*;

import org.apache.commons.mail.*;
import org.apache.commons.mail.resolver.*;


import core.*;
import core.datasource.*;
import core.tasks.*;

public class MailBomberTask implements TRunnable {

	private Hashtable<String, Object> parameters;
	private ImageHtmlEmail imageHtmlEmail;
	private DBAccess addressBookDBA;
	private String myFile;

	public MailBomberTask() {
		this.myFile = PluginManager.getMyFile("MailBomber", "m_address_book");
	}

	@Override
	public void run() {
		String[] mail_to = ((String) parameters.get("m_meto")).split(";");
		String obodymsg = ((String) parameters.get("m_mebody"));
		for (String mto : mail_to) {
			try {
				SystemLog.info("Preparing mail for " + mto);
				Record addr = addressBookDBA.exist("m_abemail = '" + mto + "'");
				if (addr == null) {
					SystemLog.severe("Contant with email " + mto + " not found");
					continue;
				}
				String toname = addr.getFieldValue("m_abtitle") + " " + addr.getFieldValue("m_abname");

				// config email parameters
				imageHtmlEmail = new ImageHtmlEmail();
				imageHtmlEmail.setHostName(PluginManager.getPluginProperty("MailBomber", "mail.config.server"));
				String val = PluginManager.getPluginProperty("MailBomber", "mail.config.smptport");
				imageHtmlEmail.setSmtpPort(Integer.valueOf(val));
				String us = PluginManager.getPluginProperty("MailBomber", "mail.config.autenticator.user");
				String pass = PluginManager.getPluginProperty("MailBomber", "mail.config.autenticator.password");
				imageHtmlEmail.setAuthenticator(new DefaultAuthenticator(us, pass));
				imageHtmlEmail.setSSLOnConnect(true);

				// header
				imageHtmlEmail.setFrom(us, PluginManager.getPluginProperty("MailBomber", "mail.config.user.name"));
				imageHtmlEmail.addTo(mto, toname);
				imageHtmlEmail.setSubject((String) parameters.get("m_mesubject"));

				// body
				imageHtmlEmail.setHtmlMsg(customMessage(obodymsg, addr));

				// inline images
				imageHtmlEmail.setDataSourceResolver(new DataSourceFileResolver(new File(TResourceUtils.USER_DIR
						+ "/plugin/mail/template/")));

				// Create the attachment
				ArrayList<File> files = (ArrayList<File>) TPreferences.getObjectFromByteArray((byte[]) parameters
						.get("m_meattachment"));
				for (File f : files) {
					EmailAttachment attach = new EmailAttachment();
					attach.setPath(f.getAbsolutePath());
					attach.setDisposition(EmailAttachment.ATTACHMENT);
					attach.setName(f.getName());
					imageHtmlEmail.attach(attach);
				}
				imageHtmlEmail.send();
				SystemLog.info("Mail for " + mto + " Sended.");
			} catch (Exception e) {
				SystemLog.logException1(e);
			}
		}
	}

	/**
	 * replace every variables found in body text to their values accourding with given parameters
	 * 
	 * @param bmsg - body message
	 * @param addr - contact from address book
	 * 
	 * @return personalized mody message
	 */
	private String customMessage(String bmsg, Record addr) {
		String newmsg = new String(bmsg);

		return newmsg;
	}

	@Override
	public void setTaskParameters(Record tr, Object ap) {
		this.parameters = (Hashtable<String, Object>) ap;
		this.addressBookDBA = ConnectionManager.getAccessTo(myFile);

	}
}
