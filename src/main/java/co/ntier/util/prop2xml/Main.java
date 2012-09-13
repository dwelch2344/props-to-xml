package co.ntier.util.prop2xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

public class Main {

	private static FilenameFilter filter = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			if (name != null && name.toLowerCase().endsWith(".properties")) {
				return true;
			}
			return false;
		}
	};

	public static void main(String[] args) {
		File dir = new File("");
		if (args.length > 0) {
			String path = args[0];
			log("Using provided path: %s", path);
			dir = new File(path);
		}

		if (!dir.isDirectory()) {
			throw new RuntimeException(dir + " is not a directory");
		}

		if (!dir.canRead() || !dir.canWrite()) {
			throw new RuntimeException(dir + " must be readable and writeable");
		}


		convertDirectory(dir);
	}

	private static void convertDirectory(File dir){
		log("Converting %s... ", dir.getAbsolutePath());
		for (File file : dir.listFiles(filter)) {
			log("Analyzing %s...", file);
			try {
				convert(file);
			} catch (Exception e) {
				log("Failed converting %s: %s", file, e.getMessage());
			}
		}
	}

	private static void convert(File file) throws FileNotFoundException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		if( !file.canRead() || !file.canWrite() ) {
			throw new RuntimeException(file + " is not readable and/or writeable");
		}

		Locale locale = null;

		String filename = file.getName().replace(".properties", ".xml");

		if( filename.contains("_") ) {
			String localeChars = filename.replace("messages_", "").replace(".xml", "");
			locale = new Locale(localeChars);
		}

		File fileOut = new File(file.getParent(), filename);
		FileOutputStream fos = new FileOutputStream(fileOut);

		String message = locale == null ? "Default" : locale.getDisplayLanguage();

		Properties props = new Properties();
		props.load(new FileInputStream(file));
		props.storeToXML(fos, message, "UTF-8");

		fos.close();
		log("Writing output to %s...", filename, fileOut.getAbsolutePath());
	}

	private static void log(String message, Object... params) {
		System.out.println(String.format(message, params));
	}

}
