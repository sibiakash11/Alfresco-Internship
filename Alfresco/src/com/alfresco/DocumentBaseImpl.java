package com.alfresco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

/**
 * Knows how to provide the values specific to Alfresco on-premise, versions
 * 4.2c and earlier. Extend this class to load files into Alfresco running on
 * your own server.
 * 
 * @author
 */
public class DocumentBaseImpl implements DocumentBase {

	// Change these to match your on-premise Alfresco server setup
	Config config = new Config();

	/**
	 * Gets a CMIS Session by connecting to the Alfresco Cloud.
	 *
	 * @param accessToken
	 * @return Session
	 * @throws IOException
	 */
	public Session getCmisSession() throws IOException {

		/**
		 * s Host domain and port with a trailing slash
		 */

		/**
		 * Username of a user with write access to the FOLDER_PATH
		 */
		final String USER_NAME = config.map("USER_NAME");

		/**
		 * Password of a user with write access to the FOLDER_PATH
		 */
		final String PASSWORD = config.map("PASSWORD");

		SessionFactory factory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();

		// connection settings

		parameter.put(SessionParameter.BROWSER_URL, Constants.BROWSERURL);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
		parameter.put(SessionParameter.AUTH_HTTP_BASIC, "true");
		parameter.put(SessionParameter.USER, USER_NAME);
		parameter.put(SessionParameter.PASSWORD, PASSWORD);
		parameter.put(SessionParameter.AUTH_HTTP_BASIC, "true");
		// parameter.put(SessionParameter.OBJECT_FACTORY_CLASS,
		// "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
		List<Repository> repositories = factory.getRepositories(parameter);

		return repositories.get(0).createSession();
	}

	public Folder getParentFolder(Session cmisSession) throws IOException {

		Config config = new Config();

		/**
		 * Folder path
		 */

		Folder folder = (Folder) cmisSession.getObjectByPath(config.map("FOLDER_PATH"));
		return folder;
	}

	public String getObjectTypeId() throws IOException {

		Config config = new Config();

		/**
		 * The content type that should be used for the uploaded objects. The default
		 * below Assumes you've deployed the Alfresco model included with the CMIS &
		 * Apache Chemistry in Action book from Manning, see
		 * 
		 */
		final String CONTENT_TYPE = config.map("CONTENT_TYPE");
		return CONTENT_TYPE;
	}

	/**
	 * Gets or creates a folder named folderName in the parentFolder.
	 * 
	 * @param cmisSession
	 * @param parentFolder
	 * @param folderName
	 * @return
	 */
	public Folder createFolder(Session cmisSession, Folder parentFolder, String folderName) {

		Folder subFolder = null;
		try {
			// Making an assumption here that you probably wouldn't normally do
			subFolder = (Folder) cmisSession.getObjectByPath(parentFolder.getPath() + "/" + folderName);
			System.out.println("Folder already existed!");
		} catch (CmisObjectNotFoundException onfe) {
			Map<String, Object> props = new HashMap<String, Object>();
			props.put("cmis:objectTypeId", "cmis:folder");
			props.put("cmis:name", folderName);
			subFolder = parentFolder.createFolder(props);
			String subFolderId = subFolder.getId();
			System.out.println("Created new folder: " + subFolderId);
		}

		return subFolder;
	}

	/**
	 * Returns the properties that need to be set on an object for a given file.
	 *
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Map<String, Object> getProperties(String objectTypeId, File file) throws FileNotFoundException, IOException {

		Map<String, Object> props = new HashMap<String, Object>();

		String fileName = file.getName();
		System.out.println("File: " + fileName);
		InputStream stream = new FileInputStream(file);
		// otherwise, just set the object type and name and be done
		props.put("cmis:objectTypeId", objectTypeId);
		props.put("cmis:name", fileName);
		if (stream != null) {
			stream.close();
		}

		return props;
	}

	/**
	 * Use the CMIS API to create a document in a folder
	 *
	 * @param cmisSession
	 * @param parentFolder
	 * @param file
	 * @param fileType
	 * @param props
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	 * 
	 *
	 */

	@Override
	public String getDocument(String obId) throws IOException {
		// TODO Auto-generated method stub
		final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		String ticket1 = Ticket.getticket();

		// System.out.println(LINK + "?objectId=" + ob_id + "&alf_ticket=" + ticket1);
		String Url = config.map("DOWNLOAD_LINK") + "?objectId=" + obId + "&alf_ticket=" + ticket1;
		LOGGER.log(Level.INFO, Url);

		return Url;
	}

	public String create_Document(Session cmisSession, Folder parentFolder, File file, String fileType,
			Map<String, Object> props) {

		String fileName = file.getName();

		if (props == null) {
			props = new HashMap<String, Object>();
		}

		if (props.get("cmis:objectTypeId") == null) {
			props.put("cmis:objectTypeId", "cmis:document");
		}

		if (props.get("cmis:name") == null) {
			props.put("cmis:name", fileName);
		}

		ContentStream contentStream = null;
		try {
			contentStream = cmisSession.getObjectFactory().createContentStream(fileName, file.length(), fileType,
					new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Document document = null;
		try {
			document = parentFolder.createDocument(props, contentStream, null);
			System.out.println("Created new document: ");
		} catch (CmisContentAlreadyExistsException ccaee) {
			document = (Document) cmisSession.getObjectByPath(parentFolder.getPath() + "/" + fileName);
			System.out.println("Document already exists: " + fileName);
		}

		return document.getId();

	}

	/**
	 * Uploads all files in a local directory to the CMIS server.
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */

	@Override
	public String createDocumentAlfresco(String pathname) {
		// Get a CMIS session
		Session cmisSession;

		Folder folder;
		try {
			cmisSession = getCmisSession();

			Folder parentFolder = getParentFolder(cmisSession);

			folder = createFolder(cmisSession, parentFolder, Constants.FOLDER_NAME);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

		String doc_id = null;

		File file1 = new File(pathname);

		// set up the properties map
		Map<String, Object> props;
		try {
			props = getProperties(getObjectTypeId(), file1);
			// create the document in the Alfresco Repository
			doc_id = create_Document(cmisSession, folder, file1, Constants.FILE_TYPE, props);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return doc_id;
	}

}
