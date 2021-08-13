package com.bsms.util;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.crypto.Cipher;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.bsms.restobj.MbApiResp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;

import org.springframework.mail.javamail.JavaMailSender;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class LibFunctionUtil {

	//========== Email Method Added By Dodo ===================//
	private static String mailHeaderEn = "Purchase/Payment ";
	private static String mailHeaderId = "Bayar/Beli ";

	 public static void sendEmailAsync(String trans_ref,
	          String mail_to, String subject, String html_content,
	          String pdf_content, boolean landscape) {

	      Thread t = new Thread(new EmailSender(trans_ref, mail_to, subject, html_content, pdf_content,
	    		  landscape));
	      t.start();
	  }

	 private static class EmailSender implements Runnable {
	        String trans_ref;
	        String mail_to;
	        String subject;
	        String html_content;
	        String pdf_content;
	        boolean landscape;
	        String tmp_folder;

	        public EmailSender(String trans_ref,
	          String mail_to, String subject, String html_content,
	          String pdf_content, boolean landscape) {
	            this.trans_ref = trans_ref;
	            this.mail_to = mail_to;
	            this.subject = subject;
	            this.html_content = html_content;
	            this.pdf_content = pdf_content;
	            this.landscape = landscape;

	        }

	        @Override
	        public void run() {
	            try {
	                SendEmail(trans_ref,
	                          mail_to,
	                          subject,
	                          html_content,
	                          pdf_content, landscape);
	            }
	            catch (Exception e) {
	            	System.out.println(e.getMessage());
	            }
	        }

	  }

	 public static void SendEmail(String trans_ref,
	          String mail_to, String subject, String html_content,
	          String pdf_content, boolean landscape) throws IOException {

	    String dir_name = "/tmp/" + getDatetime("yyyyMMdd");


	    CreateDir(dir_name);

	    String file_path = dir_name + "/" + trans_ref + ".pdf";

	    try {

	      System.out.println("Sending email to: " + mail_to);

	      Date tempDate = new Date();

	      Properties props = new Properties();
	      props.put("10.250.48.151", "BSM MAIL Server");
	      props.put("mail.smtp.port", "25");
	      props.put("mail.smtp.host", "10.250.48.151");
	      props.put("mail.smtp.auth", "false");
	      //props.put("mail.smtp.auth", "true");
	      //props.put("mail.smtp.starttls.enable", true);
	      props.put("mail.smtp.starttls.enable", false);
	      props.put("mail.smtp.connectiontimeout", "30000");
	      props.put("mail.smtp.timeout", "30000");

	      Session session = Session.getInstance(props, null);
	      session.setDebug(true);

	      MimeMessage msg = new MimeMessage(session);

	      msg.setFrom("mobile@syariahmandiri.co.id");
	      //msg.setRecipients(Message.RecipientType.TO, mail_to);
	      if (mail_to.contains(","))
	          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail_to));
	      else
	          msg.setRecipients(Message.RecipientType.TO, mail_to);
	      msg.setSubject(subject);
	      msg.setSentDate(new Date());

//	      OutputStream pdf_file = new FileOutputStream(new File(file_path));

	     Document document;
	      if (landscape == true) {
	        document = new Document(PageSize.LETTER.rotate());
	      } else {
	        document = new Document();
	      }

//	      PdfWriter.getInstance(document, pdf_file);
//	      document.open();
//	      HTMLWorker htmlWorker = new HTMLWorker(document);
//	      htmlWorker.parse(new StringReader(pdf_content));
//	      document.close();
	//
//	      pdf_file.close();

	      OutputStream pdf_file = new FileOutputStream(new File(file_path));
	      PdfWriter writer = PdfWriter.getInstance(document, pdf_file);
	      writer.setFullCompression();
	      document.open();
	      InputStream is = new ByteArrayInputStream(pdf_content.getBytes());
	      XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
	      document.close();

	      pdf_file.close();

	      MimeMultipart multipart = new MimeMultipart("alternative");
	      MimeBodyPart messageBodyPart = new MimeBodyPart();
	      messageBodyPart.setContent(html_content, "text/html");
	      multipart.addBodyPart(messageBodyPart);

	      String fileName = trans_ref + ".pdf";
	      messageBodyPart = new MimeBodyPart();
	      DataSource source = new FileDataSource(file_path);
	      messageBodyPart.setDataHandler(new DataHandler(source));
	      messageBodyPart.setFileName(fileName);
	      multipart.addBodyPart(messageBodyPart);

	      msg.setContent(multipart);
	      msg.saveChanges();


	      Transport.send(msg);

	      tempDate = new Date();

	      //logger.info("Email sent to: " + mail_to + " on " + tempDate);

	      System.out.println("Email sent to: " + mail_to + " on " + tempDate);

	    }
	    catch (Exception e) {
       	System.out.println(e.getMessage());
       }
	  }

	public static void mailNotif(String mailDest, MbApiResp mbApiResp, String mailTemplate, String lang) {
		try {
			boolean landscape = false;
			String template = null;
			String templateTrf = null;

			System.out.println("template : " + mailTemplate);

			BufferedInputStream bis = new BufferedInputStream(new ClassPathResource(mailTemplate).getInputStream());
			byte[] buffer = new byte[bis.available()];
			bis.read(buffer, 0, buffer.length);
			bis.close();

			templateTrf = new String(buffer);

			JsonObject resp = new JsonParser().parse(new Gson().toJson(mbApiResp)).getAsJsonObject();
			JsonObject respContent = resp.get("responseContent").getAsJsonObject();
			JsonArray content = resp.get("responseContent").getAsJsonObject().get("content").getAsJsonArray();

			String mailContent = "";
			mailContent += "<tr><td>Transaction No</td><td>" + respContent.get("no").getAsString() + "</td></tr>";
			mailContent += "<tr><td>Date</td><td>" + respContent.get("date").getAsString() + "</td></tr>";
			mailContent += "<tr><td colspan='2'><b>" + respContent.get("title").getAsString() + "</b></td></tr>";

			for (int i = 0; i < content.size(); i++) {
				mailContent += "<tr><td>" + content.get(i).getAsJsonObject().get("key").getAsString() + "</td><td>"
						+ content.get(i).getAsJsonObject().get("value").getAsString() + "</td></tr>";
				if (content.get(i).getAsJsonObject().has("desc")) {
					System.out.println("punya desc");
					mailContent += "<tr><td></td><td>" + content.get(i).getAsJsonObject().get("desc").getAsString() + "</td></tr>";
				}
			}

			if (lang.equals("id")) {
				templateTrf = templateTrf.replace("{header}", mailHeaderId);
			} else {
				templateTrf = templateTrf.replace("{header}", mailHeaderEn);
			}
			templateTrf = templateTrf.replace("{code_name}", respContent.get("title").getAsString());
			templateTrf = templateTrf.replace("{image}", new ClassPathResource("template/logo.jpg").getURL().toString());
			templateTrf = templateTrf.replace("{mail_content}", mailContent);
			templateTrf = templateTrf.replace("{footer}", respContent.get("footer").getAsString());
			template = templateTrf;

			sendEmailAsync(respContent.get("no").getAsString(), mailDest, "Transaksi Bank Syariah Indonesia " + respContent.get("title").getAsString(),
					template, template, landscape);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error mailer : " + e.getMessage());
		}
	}

	//=========== End Email Method ===========================//

	public static String log_message = "";
//	  private static Logger logger = LogManager.getLogger("bsm-service");

	public static String getDatetime(String format) {
		DateFormat dateFormat = null;
		Date date = null;
		try {
			dateFormat = new SimpleDateFormat(format.trim());
			date = new Date();
		} catch (Exception ex) {
			return "";
		}
		return dateFormat.format(date);
	}

	public static String DateFormat(String format_org, String format, String date_str) {
		String result = "";
		try {
			SimpleDateFormat input_date = new SimpleDateFormat(format_org);
			SimpleDateFormat date_format = new SimpleDateFormat(format, new Locale("id", "ID"));
			result = date_format.format(input_date.parse(date_str));
		} catch (ParseException e) {
			return date_str;
		} catch (Exception e) {
			return date_str;
		}
		return result;
	}

	public static boolean CreateDir(String dir_name) {
		File directory = new File(dir_name);
		try {
			if (!directory.exists()) {
				directory.mkdir();
			}
			return true;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}

	public static boolean CreateFile(String file_name) {

		try {
			File f;
			f = new File(file_name.trim());
			if (!f.exists()) {
				f.createNewFile();
			} else {
				return false;
			}

			return true;
		} catch (IOException Ex) {
			System.out.println(Ex.getMessage());
			return false;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}

	public static boolean WriteFile(String file_name, String data) {
		try {
			String filename = file_name.trim();
			FileWriter fw = new FileWriter(filename, true);
			fw.write(data);
			fw.close();
			return true;
		} catch (IOException Ex) {
			System.out.println(Ex.getMessage());
			return false;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}

	public static boolean DeleteFile(String fileName) {
		try {
			File f = new File(fileName);
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}

//	  public static boolean ReplaceFile(String old_file, String new_file) {
//
//	    File sourceFile = null;
//	    File destFile = null;
//	    FileChannel source = null;
//	    FileChannel destination = null;
//	    try {
//	      sourceFile = new File(new_file);
//	      destFile = new File(old_file);
//	      if (destFile.isFile() && destFile.exists()) {
//	        source = new FileInputStream(sourceFile).getChannel();
//	        destination = new FileOutputStream(destFile).getChannel();
//	        destination.transferFrom(source, 0, source.size());
//	      } else {
//	        return false;
//	      }
//	      return true;
//	    } catch (IOException Ex) {
//	      WriteLogFile(Ex.getMessage());
//	      return false;
//	    } catch (Exception ex) {
//	      WriteLogFile(ex.getMessage());
//	      return false;
//	    } finally {
//	      try {
//	        if (source != null) {
//	          source.close();
//	        }
//	        if (destination != null) {
//	          destination.close();
//	        }
//	      } catch (IOException Ex) {
//	        WriteLogFile(Ex.getMessage());
//	      } catch (Exception ex) {
//	        WriteLogFile(ex.getMessage());
//	        return false;
//	      }
//	    }
//	  }

//	  public static boolean WriteLogFile(String message) {
//
//	    if (message == null) {
//	      return false;
//	    }
//	    try {
//	      int infoidx = 1;
////	      String file_name = LibConfig.log_folder + "/log_"
//	              + getDatetime("yyyyMMdd") + ".txt";
//	      File f;
//	      f = new File(file_name.trim());
//
//	      StackTraceElement[] Info = new Throwable().fillInStackTrace().getStackTrace();
//
//	      if (Info.length > 3) {
//	        infoidx = 3;
//	      }
//	      for (int i = infoidx; i < Info.length; i++) {
//	        if (Info[i].hashCode() > 0) {
//	          infoidx = i;
//	          break;
//	        }
//	      }
//	      log_message = message;
//	      if (!f.exists()) {
//	        if (CreateFile(file_name)) {
//	          if (!WriteFile(file_name.trim(), message)) {
//	            return false;
//	          }
//	        } else {
//	          return false;
//	        }
//
//	      } else {
//	        message = "\r\n" + message;
//	        if (!WriteFile(file_name.trim(), message)) {
//	          return false;
//	        }
//	      }
//	    } catch (Exception ex) {
//	      System.out.println(ex.getMessage());
//	      return false;
//	    }
//	    return true;
//	  }

//	  public static void OpenLogFile(String file_name) {
//
//	    String cmd = "rundll32 url.dll,FileProtocolHandler ";
//	    String log_file = LibConfig.log_folder + "/log_" + file_name + ".txt";
//
//	    try {
//	      Process p = Runtime.getRuntime().exec(cmd + log_file);
//	      try {
//	        p.waitFor();
//	      } catch (InterruptedException Ex) {
//	        WriteLogFile(Ex.getMessage());
//	      }
//	    } catch (IOException Ex) {
//	    } catch (Exception ex) {
//	    }
//	  }

	public static String ReadFile(String filename) {
		String content = "";
		File file = new File(filename);
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (Exception e) {
			return "";
		}

		return content;
	}

	public static boolean isNumber(String number) {
		try {
			if (number == null) {
				return false;
			}

			if (number.equals("")) {
				return false;
			}
			Double.parseDouble(number);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static String DecimalToString(double value) {
		String result = "";
		try {
			DecimalFormat df = new DecimalFormat("0");
			result = df.format(value);
		} catch (Exception ex) {
			result = "";
		}
		return result.trim();
	}

	public static String RandomNumber(int count) {
		try {
			Random rand = new Random();
			String[] charset = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
			StringBuffer sb = new StringBuffer();
			for (int n = 0; n < count; n++) {
				sb = sb.append(charset[rand.nextInt(10)]);
			}
			return (sb.toString());
		} catch (Exception ex) {
			return "";
		}
	}

	public String getTransactionID(int count) {
		try {
			String result = LibFunctionUtil.getDatetime("yyyyMMddHHmmss");
			Random rand = new Random();
			String[] charset = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
			StringBuffer sb = new StringBuffer();
			for (int n = 0; n < count; n++) {
				sb = sb.append(charset[rand.nextInt(10)]);
			}

			result += sb.toString();
			return result;
		} catch (Exception ex) {
			return "";
		}
	}

	public static boolean ArraySearch(String array[], String key) {
		try {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(key)) {
					return true;
				}
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	public static String[] SplitString(String text, String delimiter) {
		String result[] = null;
		try {
			if (text != null) {
				int lenght = text.length();
				int first = 0;
				Vector lista = new Vector();
				if (text.indexOf(delimiter) != -1) {
					for (int i = 0; i < lenght; i++) {
						if (i + delimiter.length() <= lenght) {
							if (text.substring(i, i + delimiter.length()).equals(delimiter)) {
								lista.addElement(text.substring(first, i));
								first = i + delimiter.length();
							}
						}
					}
					if (!text.endsWith(delimiter)) {
						lista.addElement(text.substring(first, lenght));
					}
				} else {
					lista.addElement(text);
				}
				result = new String[lista.size()];
				for (int i = 0; i < lista.size(); i++) {
					result[i] = lista.elementAt(i).toString();
				}
			}
		} catch (Exception ex) {
			result[0] = "";
			return result;
		}
		return result;
	}

	public static Vector VectorUnique(Vector source) {
		int i = 0;
		int j = 0;
		boolean duplicates = false;
		Vector v = new Vector();
		for (i = 0; i < source.size(); i++) {
			duplicates = false;
			for (j = (i + 1); j < source.size(); j++) {
				if (source.elementAt(i).toString().equalsIgnoreCase(source.elementAt(j).toString())) {
					duplicates = true;
				}

			}
			if (duplicates == false) {
				v.addElement(source.elementAt(i).toString().trim());
			}
		}
		return v;
	}

	public static int ArrayMaxValue(int[] value) {
		int maximum = value[0];
		try {
			for (int i = 1; i < value.length; i++) {
				if (value[i] > maximum) {
					maximum = value[i];
				}
			}
		} catch (Exception Ex) {
			return 0;
		}
		return maximum;
	}

	public static String GetErrorLog(String message) {
		String result = "";
		try {
			int infoidx = 1;
			StackTraceElement[] Info = new Throwable().fillInStackTrace().getStackTrace();

			if (Info.length > 3) {
				infoidx = 3;
			}
			for (int i = infoidx; i < Info.length; i++) {
				if (Info[i].hashCode() > 0) {
					infoidx = i;
					break;
				}
			}
			result = "Maaf, sedang terjadi kesalahan, Silahkan ulangi beberapa saat lagi.\n\n"
					+ Info[infoidx].getClassName() + "." + Info[infoidx].getMethodName() + " ("
					+ Info[infoidx].getLineNumber() + ") : " + message;

		} catch (Exception ex) {
		}
		return result;
	}

	public static String NumberFormat(String number) {
		try {
			NumberFormat formatter = new DecimalFormat("###,###,###.##");
			return formatter.format(Double.valueOf(number));
		} catch (Exception ex) {
			return number;
		}
	}

	/**
	 * format local INDONESIA
	 *
	 * @param number
	 * @param dec
	 * @return
	 */
	public static String NumberFormat(String number, int dec) {
		return NumberFormat(Double.parseDouble(number), dec);
	}

	public static String NumberFormat(Double number, int dec) {
		Locale locale = new Locale("in", "IN");
		NumberFormat formatter = NumberFormat.getInstance(locale);
		formatter.setMaximumFractionDigits(dec);
		formatter.setMinimumFractionDigits(dec);
		return formatter.format(number) + "";
	}

	public static String getIPAddress() {
		String result = "";
		try {
			InetAddress address = InetAddress.getLocalHost();
			result = address.getHostAddress();
		} catch (UnknownHostException ex) {
			return "";
		}
		return result;
	}

	public static String ScriptEngine(String engine, String eval) {
		String result = "";
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine jsEngine = mgr.getEngineByName(engine);
		try {
			result = jsEngine.eval(eval).toString();
		} catch (Exception Ex) {
			return "";
		}
		return result;
	}

	public static boolean CreditCardValidation(String cc_number) {
		try {
			cc_number = cc_number.replace(" ", "").trim();
			int j = cc_number.length();

			String[] s1 = new String[j];
			for (int i = 0; i < cc_number.length(); i++) {
				s1[i] = "" + cc_number.charAt(i);
			}

			int checksum = 0;

			for (int i = s1.length - 1; i >= 0; i -= 2) {
				int k = 0;

				if (i > 0) {
					k = Integer.valueOf(s1[i - 1]).intValue() * 2;
					if (k > 9) {
						String s = "" + k;
						k = Integer.valueOf(s.substring(0, 1)).intValue() + Integer.valueOf(s.substring(1)).intValue();
					}
					checksum += Integer.valueOf(s1[i]).intValue() + k;
				} else {
					checksum += Integer.valueOf(s1[0]).intValue();
				}
			}
			return ((checksum % 10) == 0);
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean PingToServer(String url_location, int ping_timeout) {
		try {
			int timeout = ping_timeout;
			InetAddress[] addresses = InetAddress.getAllByName(url_location);
			for (InetAddress address : addresses) {
				if (address.isReachable(timeout)) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception Ex) {
			return false;
		}
		return true;
	}

	public static String SendHTTPPost(String url_location, String data_post) {

		String result = "";

		int TIMEOUT_VALUE = 1000;
		try {
			URL url = new URL(url_location);
			long start = System.nanoTime();
			URLConnection url_conn = url.openConnection();
			url_conn.setDoOutput(true);
			url_conn.setConnectTimeout(TIMEOUT_VALUE);
			url_conn.setReadTimeout(TIMEOUT_VALUE);
			OutputStreamWriter wr = new OutputStreamWriter(url_conn.getOutputStream());
			wr.write(data_post);
			wr.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(url_conn.getInputStream()));
			String inputLine;

			while ((inputLine = rd.readLine()) != null) {
				result += inputLine + "\n";
			}
			rd.close();

			long elapsed = System.nanoTime() - start;
			System.out.println("Elapsed (ms): " + elapsed / 1000000);
			System.out.println(result);
		} catch (Exception e) {
			System.out.println("More than " + TIMEOUT_VALUE + " elapsed.");
			return "-1";
		}
		return result.trim();
	}

	public static String md5(String s) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			BigInteger i = new BigInteger(1, m.digest());
			return String.format("%1$032x", i);
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}

	public static boolean isValidDate(String inDate) {
		if (inDate == null) {
			return false;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (inDate.trim().length() != dateFormat.toPattern().length()) {
			return false;
		}

		dateFormat.setLenient(false);

		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public static String GetCurrentDirectory() {
		String result = "";
		File dir = null;
		try {
			dir = new File(".");
			result = dir.getCanonicalPath();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return result = "";
		}
		return result.trim();
	}

//	  private static String genISOLogData(String isoData) throws Exception {
//	      JSONObject jISOLogData =  new JSONObject(isoData);
//	      //System.out.println(jISOLogData.toString());
//	      if (jISOLogData.has("DE2")) {
//	        String strDE2 = jISOLogData.getString("DE2");
//	        strDE2 = "XXXXXXXXXXXX" + strDE2.substring(12, 16);
//	        jISOLogData.put("DE2", strDE2);
//	      }
//
//	      if (jISOLogData.has("PINOFFSET")) {
//	        String strPO = jISOLogData.getString("PINOFFSET");
//	        strPO = "XXXXXX" + strPO.substring(6);
//	        jISOLogData.put("PINOFFSET", strPO);
//	      }
//
//	      if (jISOLogData.has("OTP")) {
//	        jISOLogData.put("OTP", "XXXXXXXX");
//	      }
//
//	      return jISOLogData.toString();
//	  }

//	  public static String SocketCliet_Bak(String url, int port, String data) {
//	    String result = "";
//	    Socket socket = null;
//	    PrintWriter toServer = null;
//	    BufferedReader fromServer = null;
//	    try {
//	      InetAddress host = InetAddress.getByName(url);
//	      //setLogMessage("Request ISO: " + data);
//	      /*EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Request ISO: " + data);*/
//	      // PCI DSS
//	      setLogMessage("Request ISO: " + genISOLogData(data));
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Request ISO: " + genISOLogData(data));
//	      // PCI DSS
//	      socket = new Socket(host, port);
//	      socket.setKeepAlive(false);
//	      socket.setSoTimeout(60000);
//	      toServer = new PrintWriter(socket.getOutputStream(), true);
//	      fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//	      toServer.println(data);
//	      result = fromServer.readLine();
//	      /*setLogMessage("Response ISO: " + result);
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Response ISO: " + result);*/
//	      // PCI DSS
//	      setLogMessage("Response ISO: " + genISOLogData(result));
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Response ISO: " + genISOLogData(result));
//	      // PCI DSS
//	      toServer.close();
//	      fromServer.close();
//	      socket.close();
//	    } catch (UnknownHostException ex) {
//	      setLogMessage(ex.getMessage());
//
//	    } catch (IOException e) {
//	      setLogMessage(e.getMessage());
//
//	    } catch (Exception e) {
//	      setLogMessage(e.getMessage());
//	    } finally {
//	      try {
//	        toServer.close();
//	        fromServer.close();
//	        socket.close();
//	      } catch (IOException ex) {
//	      } catch (Exception ex) {
//	      }
//	    }
//
//	    return result;
//	  }

//	  public static String SocketCliet(String url, int port, String data) {
//	    return SocketCliet(url, port, data, 60000);
//	  }

//	  public static String SocketCliet(String url, int port, String data, int timeout) {
//	    String result = "";
//	    Socket socket = null;
//	    PrintWriter toServer = null;
//	    BufferedReader fromServer = null;
//	    try {
//	      InetAddress host = InetAddress.getByName(url);
//	      //setLogMessage("Request ISO: " + data);
//	      /*EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Request ISO: " + data);*/
//	      // PCI DSS
//	      setLogMessage("Request ISO: " + genISOLogData(data));
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Request ISO: " + genISOLogData(data));
//	      // PCI DSS
//	      socket = new Socket(host, port);
//	      socket.setKeepAlive(false);
//	      socket.setSoTimeout(timeout);
//	      toServer = new PrintWriter(socket.getOutputStream(), true);
//	      fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//	      toServer.println(data);
//	      result = fromServer.readLine();
//	      /*setLogMessage("Response ISO: " + result);
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Response ISO: " + result);*/
//	      // PCI DSS
//	      setLogMessage("Response ISO: " + genISOLogData(result));
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Response ISO: " + genISOLogData(result));
//	      // PCI DSS
//	      toServer.close();
//	      fromServer.close();
//	      socket.close();
//	    } catch (UnknownHostException ex) {
//	      setLogMessage(ex.getMessage());
//
//	    } catch (IOException e) {
//	      setLogMessage(e.getMessage());
//
//	    } catch (Exception e) {
//	      setLogMessage(e.getMessage());
//	    } finally {
//	      try {
//	        toServer.close();
//	        fromServer.close();
//	        socket.close();
//	      } catch (IOException ex) {
//	      } catch (Exception ex) {
//	      }
//	    }
//
//	    return result;
//	  }

//	  public static String SocketClient1(String url, int port, String data, int timeout) {
//	    String result = "";
//	    Socket socket = null;
//	    PrintWriter toServer = null;
//	    BufferedReader fromServer = null;
//	    try {
//	      InetAddress host = InetAddress.getByName(url);
//	      //setLogMessage("Request ISO: " + data);
//	      /*EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Request ISO: " + data);*/
//	      // PCI DSS
//	      setLogMessage("Request ISO: " + genISOLogData(data));
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Request ISO: " + genISOLogData(data));
//	      // PCI DSS
//	      socket = new Socket(host, port);
//	      socket.setKeepAlive(false);
//	      socket.setSoTimeout(timeout);
//	      toServer = new PrintWriter(socket.getOutputStream(), true);
//	      fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//	      toServer.println(data);
//	      result = fromServer.readLine();
//	      /*setLogMessage("Response ISO: " + result);
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Response ISO: " + result);*/
//	      // PCI DSS
//	      setLogMessage("Response ISO: " + genISOLogData(result));
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Response ISO: " + genISOLogData(result));
//	      // PCI DSS
//	      toServer.close();
//	      fromServer.close();
//	      socket.close();
//	    } catch (UnknownHostException ex) {
//	      setLogMessage(ex.getMessage());
//
//	    } catch (SocketTimeoutException e) {
//	      setLogMessage(e.getMessage());
//
//	      JSONObject resp = new JSONObject();
//	      try {
//	        resp.put("DE39", "68");
//	        result = resp.toString();
//	      }
//	      catch (Exception e1) {
//	      }
//
//	    } catch (IOException e) {
//	      setLogMessage(e.getMessage());
//
//	    } catch (Exception e) {
//	      setLogMessage(e.getMessage());
//	    } finally {
//	      try {
//	        toServer.close();
//	        fromServer.close();
//	        socket.close();
//	      } catch (IOException ex) {
//	      } catch (Exception ex) {
//	      }
//	    }
//
//	    return result;
//	  }
//
//	  public static void setLogMessage(String message) {
//	    StackTraceElement[] info = new Throwable().fillInStackTrace().getStackTrace();
//	    message = info[1].getMethodName() + "(" + info[1].getLineNumber() + "): " + message;
//	    //message = getDatetime("dd-MM-yyyy HH:mm:ss") + " => " + message;
//	    //System.out.println(message);
//	    //WriteLogFile(message);
//	    logger.info(message);
//	  }

	public static String getClassInfo() {
		String result = "";
		StackTraceElement[] info = new Throwable().fillInStackTrace().getStackTrace();
		result = info[1].getMethodName() + "(" + info[1].getLineNumber() + ")";
		return result;
	}

//	  public static void SendEmail(String trans_ref,
//	          String mail_to, String subject, String html_content,
//	          String pdf_content, boolean landscape) {
//
//	    String dir_name = LibFunction.GetCurrentDirectory() + "/tmp/" + getDatetime("yyyyMMdd");
//	    CreateDir(dir_name);
//	    String file_path = dir_name + "/" + trans_ref + ".pdf";
//	    try {
//	      //setLogMessage("Sending email to: " + mail_to);
////	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
////	              EL.elInformation, "Sending email to: " + mail_to);
//	      Date tempDate = new Date();
//	      setLogMessage("Sending email to: " + mail_to + " on " + tempDate);
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Sending email to: " + mail_to + " on " + tempDate);
//
//	      Properties props = new Properties();
//	      props.put(LibConfig.smtp_host, "BSM MAIL Server");
//	      props.put("mail.smtp.port", LibConfig.smtp_port);
//	      props.put("mail.smtp.host", LibConfig.smtp_host);
//	      props.put("mail.smtp.auth", "false");
//	      //props.put("mail.smtp.auth", "true");
//	      //props.put("mail.smtp.starttls.enable", true);
//	      props.put("mail.smtp.starttls.enable", false);
//	      props.put("mail.smtp.connectiontimeout", LibConfig.smtp_conn_timeout);
//	      props.put("mail.smtp.timeout", LibConfig.smtp_timeout);
//
//	      Session session = Session.getInstance(props, null);
//	      session.setDebug(false);
//
//	      MimeMessage msg = new MimeMessage(session);
//
//	      msg.setFrom(LibConfig.mail_from);
//	      //msg.setRecipients(Message.RecipientType.TO, mail_to);
//	      if (mail_to.contains(","))
//	          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail_to));
//	      else
//	          msg.setRecipients(Message.RecipientType.TO, mail_to);
//	      msg.setSubject(subject);
//	      msg.setSentDate(new Date());
//
////	      OutputStream pdf_file = new FileOutputStream(new File(file_path));
//
//	      Document document;
//	      if (landscape == true) {
//	        document = new Document(PageSize.LETTER.rotate());
//	      } else {
//	        document = new Document();
//	      }
//
////	      PdfWriter.getInstance(document, pdf_file);
////	      document.open();
////	      HTMLWorker htmlWorker = new HTMLWorker(document);
////	      htmlWorker.parse(new StringReader(pdf_content));
////	      document.close();
//	//
////	      pdf_file.close();
//
//	      OutputStream pdf_file = new FileOutputStream(new File(file_path));
//	      PdfWriter writer = PdfWriter.getInstance(document, pdf_file);
//	      writer.setFullCompression();
//	      document.open();
//	      InputStream is = new ByteArrayInputStream(pdf_content.getBytes());
//	      XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
//	      document.close();
//
//	      pdf_file.close();
//
//	      MimeMultipart multipart = new MimeMultipart("alternative");
//	      MimeBodyPart messageBodyPart = new MimeBodyPart();
//	      messageBodyPart.setContent(html_content, "text/html");
//	      multipart.addBodyPart(messageBodyPart);
//
//	      String fileName = trans_ref + ".pdf";
//	      messageBodyPart = new MimeBodyPart();
//	      DataSource source = new FileDataSource(file_path);
//	      messageBodyPart.setDataHandler(new DataHandler(source));
//	      messageBodyPart.setFileName(fileName);
//	      multipart.addBodyPart(messageBodyPart);
//
//	      msg.setContent(multipart);
//	      msg.saveChanges();
//
//	      //Transport.send(msg, LibConfig.mail_user, LibConfig.mail_pass);
//	      Transport.send(msg);
////	      setLogMessage("Email sent to: " + mail_to);
////	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
////	              EL.elInformation, "Email sent to: " + mail_to);
//	      tempDate = new Date();
//	      setLogMessage("Email sent to: " + mail_to + " on " + tempDate);
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 3,
//	              EL.elInformation, "Email sent to: " + mail_to + " on " + tempDate);
//	    } catch (DocumentException Ex) {
//	      setLogMessage("Email Error:" + Ex.getMessage());
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 1,
//	              EL.elWarning, "Email Error:" + Ex.getMessage());
//	    } catch (IOException Ex) {
//	      setLogMessage("Email Error:" + Ex.getMessage());
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 1,
//	              EL.elWarning, "Email Error:" + Ex.getMessage());
//	    } catch (MessagingException Ex) {
//	      setLogMessage("Email Error:" + Ex.getMessage());
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 1,
//	              EL.elWarning, "Email Error:" + Ex.getMessage());
//	    } catch (Exception Ex) {
//	      setLogMessage("Email Error:" + Ex.getMessage());
//	      EL.log(LibConfig.application_id, LibFunction.getClassInfo(), 1,
//	              EL.elWarning, "Email Error:" + Ex.getMessage());
//	    }
//	  }

//	  private static class EmailSender implements Runnable {
//	        String trans_ref;
//	        String mail_to;
//	        String subject;
//	        String html_content;
//	        String pdf_content;
//	        boolean landscape;
//
//	        public EmailSender(String trans_ref,
//	          String mail_to, String subject, String html_content,
//	          String pdf_content, boolean landscape) {
//	            this.trans_ref = trans_ref;
//	            this.mail_to = mail_to;
//	            this.subject = subject;
//	            this.html_content = html_content;
//	            this.pdf_content = pdf_content;
//	            this.landscape = landscape;
//	        }
//
//	        @Override
//	        public void run() {
//	            try {
//	                SendEmail(trans_ref,
//	                          mail_to,
//	                          subject,
//	                          html_content,
//	                          pdf_content, landscape);
//	            }
//	            catch (Exception e) {
//	                setLogMessage("EmailSender: Email Error:" + e.getMessage());
//	            }
//	        }
//
//	  }

//	  public static void sendEmailAsync(String trans_ref,
//	          String mail_to, String subject, String html_content,
//	          String pdf_content, boolean landscape) {
//
//	      Thread t = new Thread(new EmailSender(trans_ref, mail_to, subject, html_content, pdf_content, landscape));
//	      t.start();
//	  }

	public static String SeparateString(int div, String data) {
		try {
			String result = "";
			for (int i = 0; i < data.length(); i++) {
				result += data.charAt(i);
				if (i == (div - 1)) {
					result += " ";
					div = div + 4;
				}
			}
			return result;
		} catch (Exception Ex) {
			return data;
		}
	}

	public static String GetMacAddress() {
		String result = "";
		try {
			InetAddress ip_address;
			ip_address = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip_address);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "" : ""));
			}
			result = sb.toString();
		} catch (UnknownHostException Ex) {
			return "Error: " + Ex.getMessage();
		} catch (SocketException Ex) {
			return "Error: " + Ex.getMessage();
		} catch (Exception Ex) {
			return "Error: " + Ex.getMessage();
		}
		return result;
	}

	public static String genMaskedMSISDN(String msisdn) {
		String maskedMSISDN = "XXX";

		if (msisdn != null && msisdn.length() > 3) {
			maskedMSISDN = msisdn.substring(0, msisdn.length() - maskedMSISDN.length()) + maskedMSISDN;
		}

		return maskedMSISDN;
	}

	public static String getPINBlock(String key, String PIN) {
		if (key == null || key.isEmpty()) {
			return "";
		}

		// PIN = padRight(PIN, 16, 'F');
		PIN = String.format("%-16s", PIN).replace(" ", "F");
		// Log.i(TAG, "setPINBlock: PIN " + PIN);
		String pinBlock = "";// null;

		try {
			// Log.i("hextobyte KEY", "" + toByte(key).length);
			// Log.i("hextobyte PIN", "" + toByte(PIN).length);

			DESKeySpec key_spec = new DESKeySpec(toByte(key));
			SecretKeySpec DESKey = new SecretKeySpec(key_spec.getKey(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, DESKey);
			byte[] encrypted = cipher.doFinal(toByte(PIN));
			// Log.i(TAG, "setPINBlock: panjang hasil" + encrypted.length);
			pinBlock = toHex(encrypted).toUpperCase();
			// Log.i(TAG, "setPINBlock: hasil " + pinBlock);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pinBlock;
	}

	public static byte[] toByte(String hex) {
		if (hex == null || hex.length() == 0) {
			return null;
		}

		byte[] ba = new byte[hex.length() / 2];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return ba;
	}

	// byte[] to hex
	public static String toHex(byte[] ba) {
		if (ba == null || ba.length == 0) {
			return null;
		}

		StringBuffer sb = new StringBuffer(ba.length * 2);
		String hexNumber;
		for (int x = 0; x < ba.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}

//	    private static class SMSSender implements Runnable {
//	        JSONObject params;
//
//	        public SMSSender(JSONObject params) {
//	            this.params = params;
//	        }
//
//	        @Override
//	        public void run() {
//	            try {
//	                LibFunction.notify(params);
//	            }
//	            catch (Exception e) {
//	                setLogMessage("SMSSender: Error:" + e.getMessage());
//	            }
//	        }
//
//	    }
//
//	    public static void notifyAsync(JSONObject params) {
//	        Thread t = new Thread(new SMSSender(params));
//	        t.start();
//	    }
//
//	    public static void notify(JSONObject params) throws Exception {
//	        Connection connSMS = null;
//	        String sql;
//	        PreparedStatement psSMS = null;
//
//	        try {
//	            int i;
//
//	            // Send the notification
//	            connSMS = LibMSSQL.cpdsSMS.getConnection();
//	            connSMS.setAutoCommit(true);
//	            sql = "insert into dbo.sms_dispatcher (src,dest,msg,cost_center,stat_intern,msg_cnt_fwap,inserted_timestamp,sms_count,delivery_variable,priority) " +
//	                    " values (?,?,?,?,?,?,?,?,?,?)";
////	                    'BSMCenter'",
////	                    '081911013328',
////	                    '$msg',
////	                    'BSM_TOKEN2',
////	                    '0',
////	                    '0',
////	                    CURRENT_TIMESTAMP,
////	                    '0',
////	                    '0',
////	                    '2');
//	            psSMS = connSMS.prepareStatement(sql);
//	            i = 0;
//	            psSMS.setObject(++i, "BSMCenter");
//	            //psSMS.setObject(++i, "081911013328");
//	            psSMS.setObject(++i, params.getString("msisdn"));
//	            psSMS.setObject(++i, params.getString("msg"));
//	            psSMS.setObject(++i, "BSM_TOKEN2");
//	            psSMS.setObject(++i, "0");
//	            psSMS.setObject(++i, "0");
//	            psSMS.setObject(++i, new Timestamp(System.currentTimeMillis()));
//	            psSMS.setObject(++i, "0");
//	            psSMS.setObject(++i, "0");
//	            psSMS.setObject(++i, "2");
//	            psSMS.executeUpdate();
//	        }
//	        catch(Exception e) {
//	            setLogMessage("An error occurred on notify()");
//	            e.printStackTrace();
//
//	            throw e;
//	        }
//	        finally {
//	            if (psSMS != null)
//	                psSMS.close();
//	            if (connSMS != null)
//	                connSMS.close();
//	        }
//	    }

	public static String genMSGID(String prefix, String trxId) {
		int length = trxId.length();
		if (length > 9)
			trxId = trxId.substring(length - 9, length);

		return prefix + new SimpleDateFormat("yyD").format(new Date()) + trxId;
	}

	public static String getYearOfMonth(String loanTenor, String language) {
		String returnValue = loanTenor;
		int loanTenorInt = Integer.valueOf(loanTenor);
		if ("id".equals(language))
			returnValue += " Bulan";
		else
			returnValue += " Month";
		if (loanTenorInt >= 12) {
			int year = loanTenorInt / 12;
			int overMonth = loanTenorInt % 12;
			if ("id".equals(language)) {
				returnValue += " (" + year + " Tahun";
				if (overMonth > 0)
					returnValue += " " + overMonth + " Bulan";
				returnValue += ")";
			} else {
				returnValue += " (" + year + " Year";
				if (overMonth > 0)
					returnValue += " " + overMonth + " Month";
				returnValue += ")";
			}
		}
		return returnValue;
	}

	public String str_pad(String input, int length, String pad, String sense) {
		int resto_pad = length - input.length();
		String padded = "";

		if (resto_pad <= 0) {
			return input;
		}

		if (sense.equals("STR_PAD_RIGHT")) {
			padded = input;
			padded += _fill_string(pad, resto_pad);
		} else if (sense.equals("STR_PAD_LEFT")) {
			padded = _fill_string(pad, resto_pad);
			padded += input;
		} else // STR_PAD_BOTH
		{
			int pad_left = (int) Math.ceil(resto_pad / 2);
			int pad_right = resto_pad - pad_left;

			padded = _fill_string(pad, pad_left);
			padded += input;
			padded += _fill_string(pad, pad_right);
		}
		return padded;
	}

	protected String _fill_string(String pad, int resto) {
		boolean first = true;
		String padded = "";

		if (resto >= pad.length()) {
			for (int i = resto; i >= 0; i = i - pad.length()) {
				if (i >= pad.length()) {
					if (first) {
						padded = pad;
					} else {
						padded += pad;
					}
				} else {
					if (first) {
						padded = pad.substring(0, i);
					} else {
						padded += pad.substring(0, i);
					}
				}
				first = false;
			}
		} else {
			padded = pad.substring(0, resto);
		}
		return padded;
	}

	/* Added by Prasetyo for transfer schedule purpose */
	protected static String setTimezone(String formatdatetime, int timezone, String datetime) {

		int year = Integer.parseInt(datetime.substring(0, 4));
		int month = Integer.parseInt(datetime.substring(5, 7)) - 1;
		int day = Integer.parseInt(datetime.substring(8, 10));
		int hour = Integer.parseInt(datetime.substring(11, 13));
		int minute = Integer.parseInt(datetime.substring(14, 16));
		int second = Integer.parseInt(datetime.substring(17, 19));

		DateFormat dateFormat = new SimpleDateFormat(formatdatetime);
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, hour, minute, second);
		c.add(c.HOUR, timezone);
		Date currentAddDate = c.getTime();

		return dateFormat.format(currentAddDate);

	}

	protected static String formatDateUTC(String tgl) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+0000'");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = sdf.parse(tgl);
		String formattedTime = output.format(d);
		String formattedTimeZone = setTimezone("yyyy-MM-dd HH:mm:ss", 7, formattedTime);
		return formattedTimeZone;
	}

	public String formatIDRCurrency(Double amount) {
		DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

		formatRp.setCurrencySymbol("Rp. ");
		formatRp.setMonetaryDecimalSeparator(',');
		formatRp.setGroupingSeparator('.');

		idr.setDecimalFormatSymbols(formatRp);

		return idr.format(amount);
	}

}
