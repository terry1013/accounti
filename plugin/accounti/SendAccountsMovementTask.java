package plugin.accounti;

import gui.*;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;

import javax.swing.*;

import org.apache.commons.csv.*;
import org.apache.commons.mail.*;

import core.*;
import core.datasource.*;
import core.tasks.*;

public class SendAccountsMovementTask implements TRunnable {

	private Vector<Record> rcdList;
	private Hashtable<String, Object> exportParameters;
	private Record sifiRcdModel;
	private ServiceResponse response;
	private ServiceRequest request;
	private TProgressMonitor monitor;
	private SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMdd");
	
	public SendAccountsMovementTask(Hashtable ht) {
		this.exportParameters = ht;
	}

	private Record translateRecord(Record srcRcd, int id) {
		Record tr = new Record(sifiRcdModel);
		if (srcRcd.getFieldValue("canctb").equals(0)) {
			System.out.println();
		}
		for (int c = 0; c < srcRcd.getFieldCount(); c++) {
			// rest of fields
			tr.setFieldValue(c + 2, srcRcd.getFieldValue(c));
		}
		// madatory
		tr.setFieldValue(0, "400");
		// id
		tr.setFieldValue(1, id);
		
		// dates to string
		Date tmp = (Date) srcRcd.getFieldValue("FECCMP");
		tr.setFieldValue("FECCMP", tmp.equals(TStringUtils.ZERODATE) ? null : simpleDF.format( tmp));
		tmp = (Date) srcRcd.getFieldValue("FECDOC");
        tr.setFieldValue("FECDOC", tmp.equals(TStringUtils.ZERODATE) ? null : simpleDF.format( tmp));
		tmp = (Date) srcRcd.getFieldValue("FECSIS");
        tr.setFieldValue("FECSIS", tmp.equals(TStringUtils.ZERODATE) ? null : simpleDF.format( tmp));
		tr.setFieldValue("FECPROC", "");
		return tr;
	}

	private void exportToDB() {
		DBAccess tardba = ConnectionManager.getAccessTo("ZIFI_REGCONT");
		for (int i = 0; i < rcdList.size(); i++) {
			Record srcd = rcdList.elementAt(i);
			tardba.add(translateRecord(srcd, i));
		}
	}
	public void setFuture(Future f, boolean ab) {
		this.monitor = new TProgressMonitor("SendAccountMovement", "sac.task", null, false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				monitor.show(AccountI.frame);
			}
		});
	}

	private String getWhereClause() {
		String wc = "(";
		// Selected companies
		String scmp[] = ((String) exportParameters.get("companySelected")).split(";");
		for (String cid : scmp) {
			wc+= "CODCIA = '"+cid+"' OR ";
		}
		wc = wc.substring(0, wc.length()-4)+")";
		// year
		int anoctb = (Integer) exportParameters.get("sac.year");
		if (anoctb > 0) {
			wc += " AND ANOCTB = " + anoctb;
		}
		// month
		int lapctb = (Integer) exportParameters.get("sac.month");
		if (lapctb > 0) {
			wc += " AND LAPCTB = " + lapctb;
		}
		// type
		String tipcmp = (String) exportParameters.get("sac.type");
		if (!tipcmp.equals("")) {
			wc += " AND TIPCMP = '" + tipcmp + "'";
		}
		// vauche #
		String numcmp = (String) exportParameters.get("sac.number");
		if (!numcmp.equals("")) {
			wc += " AND NUMCMP = '" + numcmp + "'";
		}
		// date
		Date feccmp = (Date) exportParameters.get("sac.date");
		if (!feccmp.equals(TStringUtils.ZERODATE)) {
			wc += " AND FECCMP = '" + feccmp + "'";
		}
		// user
		String userid = (String) exportParameters.get("sac.user");
		if (!userid.equals("")) {
			wc += " AND USERID = '" + userid + "'";
		}

		return wc;
	}

	@Override
	public void run() {
		try {
			setFuture(null, false);
			Thread.sleep(250);
			monitor.setProgress(0, "sac.task.msg01");
			request = new ServiceRequest(ServiceRequest.DB_QUERY, "NMM027", getWhereClause());
			response = ServiceConnection.sendTransaction(request);
			rcdList = (Vector) response.getData();
			sifiRcdModel = ConnectionManager.getAccessTo("ZIFI_REGCONT").getModel();
			boolean todb = (Boolean) exportParameters.get("sac.todb");
			String cvsf = (String) exportParameters.get("sac.filename");
			if (todb) {
				monitor.setProgress(0, "sac.task.msg02");
				Thread.sleep(250);
				exportToDB();
				monitor.setProgress(0, "sac.task.msg04");
				Thread.sleep(250);
				sendMail(null);
				AccountI.showNotification("ic.notification.msg19");
			} else {
				monitor.setProgress(0, "sac.task.msg03");
				exportCsv(cvsf);
				monitor.setProgress(0, "sac.task.msg04");
				sendMail(cvsf);
				AccountI.showNotification("notification.msg19", cvsf);
			}
			// delete NMM027?
			if ((Boolean) exportParameters.get("sac.delat")) {
				Connection con = ConnectionManager.getConnection("NMM027");
				con.createStatement().execute("DELETE FROM NMM027");
			}
			monitor.dispose();
		} catch (Exception ex) {
			monitor.dispose();
			// notify the user and log exeption
			SystemLog.logException(ex);
			AccountI.showNotification("notification.msg18", ex.getMessage());
		}
	}

	private void sendMail(String cvsf) throws Exception {
		// config email parameters
		ImageHtmlEmail imageHtmlEmail = new ImageHtmlEmail();
		imageHtmlEmail.setHostName(TStringUtils.getBundleString("mail.config.server"));
		String val = TStringUtils.getBundleString("mail.config.smptport");
		imageHtmlEmail.setSmtpPort(Integer.valueOf(val));
		String us = TStringUtils.getBundleString("mail.config.autenticator.user");
		String pass = TStringUtils.getBundleString("mail.config.autenticator.password");
		imageHtmlEmail.setAuthenticator(new DefaultAuthenticator(us, pass));
		String ssl = TStringUtils.getBundleString("mail.config.ssl");
		imageHtmlEmail.setSSLOnConnect(ssl.equals("true"));

		// header
		imageHtmlEmail.setFrom(us, TStringUtils.getBundleString("mail.config.user.name"));
		String too[] = TStringUtils.getBundleString("mail.recipient").split(";");
		for (String to : too) {
			imageHtmlEmail.addTo(to);
		}
		imageHtmlEmail.setSubject(TStringUtils.getBundleString("mail.subject"));

		// clear zero date
		if (((Date) exportParameters.get("sac.date")).equals(TStringUtils.ZERODATE)) {
			exportParameters.put("sac.date", "");
		}

		String patt = TStringUtils.getBundleString("mail.body");
		String newbody = TStringUtils.format(patt, "", exportParameters);
		imageHtmlEmail.setHtmlMsg(newbody);

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
		CSVFormat csvf = CSVFormat.DEFAULT.withDelimiter(';').withQuote(null);

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
		for (int i = 0; i < rcdList.size(); i++) {
			Record srcd = rcdList.elementAt(i);
			Record tar = translateRecord(srcd, i);
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
