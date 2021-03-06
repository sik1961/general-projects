package com.sik.markiv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.sik.markiv.events.EventManager;
import com.sik.markiv.exception.MarkIVException;
import com.sik.markiv.ftp.MarkIVUploader;
import com.sik.markiv.html.GalleryManager;
import com.sik.markiv.html.GigsAvailabilityBuilder;
import com.sik.markiv.html.HtmlManager;
import com.sik.markiv.utils.PropsUtils;
/**
 * @author sik
 *
 */
public class MarkIVHelper {
	private static final Logger LOG = Logger.getLogger(MarkIVHelper.class);
	private static final String PROPS_FILE = "/Users/sik/Java/markiv/markiv.properties";

	private Properties props;
	private PropsUtils pu = new PropsUtils(); 
	// private static String calendarExportFile;
	private String localWebRoot;
	private String galleryDir;
	private String htmlHeaderFile;
	private String htmlTrailerFile;
	private String gigsHtmlFile;
	private String availHtmlFile;
	private String gallHtmlFile;
	//private String uploadFileList;

	private GigsAvailabilityBuilder gigAvailBuilder;
	private EventManager em;

	public MarkIVHelper(EventManager em) {
		this.em = em;
		this.gigAvailBuilder = new GigsAvailabilityBuilder(this.em);
		this.props = this.readProps();
		localWebRoot = props.getProperty("LocalWebRoot");
		galleryDir = props.getProperty("UploadDirs");
		htmlHeaderFile = props.getProperty("HtmlHeaderFile");
		htmlTrailerFile = props.getProperty("HtmlTrailerFile");
		gigsHtmlFile = props.getProperty("GigsHtmlFile");
		availHtmlFile = props.getProperty("AvailHtmlFile");
		gallHtmlFile = props.getProperty("GalleryHtmlFile");
	}

	public void uploadFiles() throws MarkIVException {
		final MarkIVUploader mul = new MarkIVUploader(props);

		final List<String> uploadFiles = new ArrayList<String>();
		for (final String f : Arrays.asList(props.getProperty("UploadFiles").split(","))) {
			uploadFiles.add(f);
		}

		mul.upload(props.getProperty("ProjectName"), uploadFiles);
	}

	public void buildAvailPage() {
		final HtmlManager htmlMan = new HtmlManager();
		try {
			htmlMan.writeHtmlFile(availHtmlFile,
					gigAvailBuilder.buildAvail(htmlHeaderFile, htmlTrailerFile));
		} catch (final IOException e) {
			throw new MarkIVException("Exception occurred:",e);
		}

	}

	public void buildGigsPage() {
		try {
			final HtmlManager htmlMan = new HtmlManager();
			htmlMan.writeHtmlFile(gigsHtmlFile,
					gigAvailBuilder.buildGigs(htmlHeaderFile, htmlTrailerFile));
		} catch (final IOException e) {
			throw new MarkIVException("Exception occurred:",e);
		}
	}

	public void buildGalleryPage() {
		final GalleryManager galleryManager = new GalleryManager();
		final HtmlManager htmlMan = new HtmlManager();
		try {
			htmlMan.writeHtmlFile(
					gallHtmlFile,
					htmlMan.readFileAsString(htmlHeaderFile)
							+ galleryManager.buildGalleryHtml(localWebRoot,
									galleryDir)
							+ htmlMan.readFileAsString(htmlTrailerFile));
		} catch (final IOException e) {
			throw new MarkIVException("Exception occurred:",e);
		}
	}

	public Properties readProps() {
		Properties props = this.pu.readProperties(PROPS_FILE);
		LOG.info("Properties loaded:");
		for (final Object key : props.keySet()) {
			LOG.info(key + "=" + props.get(key));
		}
		return props;
	}

	public void writeProps(Properties props, String propsFile) {
		this.pu.writeProperties(props, propsFile);
	}
}
