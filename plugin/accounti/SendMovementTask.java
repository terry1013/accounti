package plugin.accounti;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.csv.*;
import org.apache.commons.mail.*;

import core.*;
import core.datasource.*;
import core.tasks.*;

public class SendMovementTask implements TRunnable {

	private Vector<Record> rcdList;
	private Hashtable<String, Object> exportParameters;
	private Record nmmRcdModel, sifiRcdModel;
	private ServiceResponse response;
	private ServiceRequest request;
	private SimpleDateFormat dateFormat;

	public SendMovementTask(Hashtable ht) {
		this.exportParameters = ht;
		dateFormat = new SimpleDateFormat("yyMMdd");
	}

	private Record translateRecord(Record srcRcd) {
		Record tr = new Record(sifiRcdModel);
		for (int c = 0; c < srcRcd.getFieldCount(); c++) {
			// madatory
			tr.setFieldValue(0, "400");
			// id
			tr.setFieldValue(1, c);
			// rest of fields
			tr.setFieldValue(c + 2, srcRcd.getFieldValue(c));
		}
		// process's date
		tr.setFieldValue("FECPROC", dateFormat.format(new Date()));
		return tr;
	}

	private void exportToDB() {
		DBAccess tardba = ConnectionManager.getAccessTo("ZIFI_REGCONT");
		for (Record rcd : rcdList) {
			tardba.add(translateRecord(rcd));
		}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(250);
			request = new ServiceRequest(ServiceRequest.DB_QUERY, "NMM027", null);
			response = ServiceConnection.sendTransaction(request);
			nmmRcdModel = (Record) response.getParameter(ServiceResponse.RECORD_MODEL);
			rcdList = (Vector) response.getData();
			sifiRcdModel = ConnectionManager.getAccessTo("ZIFI_REGCONT").getModel();
			boolean todb = (Boolean) exportParameters.get("sac.todb");
			String cvsf = (String) exportParameters.get("sac.filename");
			if (todb) {
				exportToDB();
				sendMail(null);
				AccountI.showNotification("ic.notification.msg19");
			} else {
				exportCsv(cvsf);
				sendMail(cvsf);
				AccountI.showNotification("notification.msg19", cvsf);
			}
		} catch (Exception ex) {
			// notify the user and log exeption
			SystemLog.logException(ex);
			AccountI.showNotification("notification.msg18", ex.getMessage());
		}
	}

	private void sendMail(String cvsf) throws Exception {

		// config email parameters
		ImageHtmlEmail imageHtmlEmail = new ImageHtmlEmail();
		imageHtmlEmail.setHostName(PluginManager.getPluginProperty("AccountIP", "mail.config.server"));
		String val = PluginManager.getPluginProperty("AccountIP", "mail.config.smptport");
		imageHtmlEmail.setSmtpPort(Integer.valueOf(val));
		String us = PluginManager.getPluginProperty("AccountIP", "mail.config.autenticator.user");
		String pass = PluginManager.getPluginProperty("AccountIP", "mail.config.autenticator.password");
		imageHtmlEmail.setAuthenticator(new DefaultAuthenticator(us, pass));
		String ssl = PluginManager.getPluginProperty("AccountIP", "mail.config.ssl");
		imageHtmlEmail.setSSLOnConnect(ssl.equals("true"));

		// header
		imageHtmlEmail.setFrom(us, PluginManager.getPluginProperty("AccountIP", "mail.config.user.name"));
		String too[] = PluginManager.getPluginProperty("AccountIP", "mail.recipient").split(";");
		for (String to : too) {
			imageHtmlEmail.addTo(to);
		}
		imageHtmlEmail.setSubject(PluginManager.getPluginProperty("AccountIP", "mail.subject"));

		// text message or template? (when template, inlinedir != null)
		String oldbody = PluginManager.getPluginProperty("AccountIP", "mail.body");
		// String newbody = replaceVars(oldbody, addr);
		// imageHtmlEmail.setHtmlMsg(newbody);
		imageHtmlEmail.setHtmlMsg(oldbody);

		// attachment
		if (cvsf != null) {
			EmailAttachment attach = new EmailAttachment();
			File f = new File(cvsf);
			attach.setPath(f.getAbsolutePath());
			attach.setDisposition(EmailAttachment.ATTACHMENT);
			attach.setName(f.getName());
			imageHtmlEmail.attach(attach);
		}

		imageHtmlEmail.send();
	}

	private void exportCsv(String fname) throws Exception {
		// file format
		CSVFormat csvf = CSVFormat.DEFAULT;

		// header
		String[] header = new String[sifiRcdModel.getFieldCount()];
		for (int i = 0; i < header.length; i++) {
			header[i] = sifiRcdModel.getFieldName(i);
		}
		// csvf = csvf.withHeader(header);

		// target file
		FileWriter out = new FileWriter(fname);
		CSVPrinter printer = csvf.print(out);

		// data
		for (Record srcd : rcdList) {
			Record tar = translateRecord(srcd);
			Object[] csvr = new Object[tar.getFieldCount()];
			for (int co = 0; co < tar.getFieldCount(); co++) {
				Object val = tar.getFieldValue(co);
				val = fixFieldValue(val);
				csvr[co] = val;
			}
			csvf.printRecord(out, csvr);
		}
		printer.close();
	}

	/**
	 * method that take care of some data considerations. This method is invoked before write the <code>val</code>
	 * object to the output file.
	 * 
	 * @param val - object to modify
	 */
	private Object fixFieldValue(Object val) {
		// date consideration: if < zero date, convert to empty string
		if (val instanceof Date) {
			Date d = (Date) val;
			return d.getTime() < 0 ? "" : d;
		}
		// Double: reduce decimal digits to 2
		if (val instanceof Double) {
			double sc = Math.pow(10, 2);
			double dval = Math.round(((Double) val) * sc) / sc;
			val = dval;
			// val = decimalF.format(dval);
		}

		return val;
	}

	@Override
	public void setTaskParameters(Record r, Object o) {

	}
}
