package org.tdl.vireo.deposit.impl;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.deposit.Packager;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.jpa.JpaPersonRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSettingsRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSubmissionRepositoryImpl;
import org.tdl.vireo.search.Semester;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the generic template package.
 * 
 * Since it is expected that there will be multiple beans may be defined for
 * this type. All defined beans will be tested.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class TemplatePackagerImplTest extends UnitTest {

	// All the repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);

	public Person person;
	public Submission sub;
	
	/**
	 * Set up a submission so we can test packaging it up.
	 */
	@Before
	public void setup() throws IOException {
		context.turnOffAuthorization();
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		sub = subRepo.createSubmission(person);
		
		// Okay, we should start generating action log messages now.
		sub.setStudentFirstName("first name");
		sub.setStudentLastName("last name");
		sub.setStudentMiddleName("middle name");
		sub.setStudentBirthYear(2002);
		sub.setDocumentTitle("document title");
		sub.setDocumentAbstract("document abstract");
		sub.setDocumentKeywords("document keywords");
		sub.setDegree("selected degree");
		sub.setDegreeLevel(DegreeLevel.UNDERGRADUATE);
		sub.setDepartment("selected department");
		sub.setCollege("selected college");
		sub.setMajor("selected major");
		sub.setDocumentType("selected document type");
		sub.setGraduationMonth(0);
		sub.setGraduationYear(2002);
		sub.setDepositId("depositId");
		
		// Create some attachments
		File tmpDir = createNewTempDir();
		File bottle_pdf = createAndWriteFile(tmpDir, "bottle.pdf", "bottle.pdf: This is not really a pdf file.");
		File fluff_jpg = createAndWriteFile(tmpDir, "fluff.jpg", "fluff.jpg: This is not really a jpg file.");
		
		sub.addAttachment(bottle_pdf, AttachmentType.PRIMARY);
		sub.addAttachment(fluff_jpg, AttachmentType.SUPPLEMENTAL);
		
		sub.save();
		
		bottle_pdf.delete();
		fluff_jpg.delete();
		tmpDir.delete();
		
	}
	
	/**
	 * Clean up our submission.
	 */
	@After
	public void cleanup() {
		try {
		sub.delete();
		person.delete();
		context.restoreAuthorization();
		} catch (RuntimeException re) {
			re.printStackTrace();
		}
	}
	
	/**
	 * Test each packager handling of the submission. We check that basic things
	 * are there, the files, a manifest, and that the manifest contains
	 * important pieces of metadata.
	 */
	@Test
	public void testPackager() throws IOException, JDOMException {

		// Test all the template packagers
		Map<String,TemplatePackagerImpl> packagers = Spring.getBeansOfType(TemplatePackagerImpl.class);
		
		for (TemplatePackagerImpl packager : packagers.values()) {
			
			Packager.Package pkg = packager.generatePackage(sub);
			
			assertNotNull(pkg);
			assertEquals(packager.format,pkg.getFormat());
			assertEquals(packager.mimeType,pkg.getMimeType());
			
			
			
			
			File zipFile = pkg.getFile();
			assertNotNull(zipFile);
			assertTrue("Package file does not exist", zipFile.exists());
			assertTrue("Package file is not readable", zipFile.canRead());
			
			File targetDir = createNewTempDir();
			decompressZip(targetDir, zipFile);
			Map<String, File> fileMap = getFileMap(targetDir);
			
			// There should be three files
			assertTrue(fileMap.containsKey(packager.manifestName));
			assertTrue(fileMap.containsKey("bottle.pdf"));
			assertTrue(fileMap.containsKey("fluff.jpg"));
			
			// Load up the manifest and make sure it's valid XML.
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(fileMap.get(packager.manifestName));
			
			// Check that the manifest contains important data
			String manifest = readFile(fileMap.get(packager.manifestName));
			assertTrue(manifest.contains(sub.getStudentFirstName()));
			assertTrue(manifest.contains(sub.getStudentLastName()));
			assertTrue(manifest.contains(sub.getDocumentTitle()));
			assertTrue(manifest.contains(sub.getDocumentAbstract()));
			
			// Cleanup
			for(File file : fileMap.values())
				file.delete();
			targetDir.delete();
			
			pkg.delete();
			assertFalse(zipFile.exists());
		}
	}
	
	
	/**
	 * Create a temporary working directory
	 * 
	 * @return the File object pointing to the created directory
	 */
	public File createNewTempDir() throws IOException {
		File tempDir = File.createTempFile("packager-tester", ".dat");
		tempDir.delete();
		tempDir.mkdir();

		assertTrue(tempDir.exists());
		assertTrue(tempDir.isDirectory());

		return tempDir;
	}
	
	/**
	 * A utility method that takes in zip file and extracts it into the target
	 * directory
	 * 
	 * @param targetDir
	 * @param zipFile
	 */
	public void decompressZip(File targetDir, File zipFile) throws IOException 
	{
		FileInputStream fio = new FileInputStream(zipFile);
		ZipInputStream zio = new ZipInputStream(new BufferedInputStream(fio));
	
		ZipEntry entry;
		File targetFile;
		String filename;
		while((entry = zio.getNextEntry()) != null) {
			filename = entry.getName();
			targetFile = new File(targetDir.getCanonicalPath() + File.separator + filename);
			saveZipEntry(targetFile, zio);
		}
		zio.close();
	}
	
	/**
	 * Read from a zip input stream and save the contents in the target file
	 * 
	 * @param targetFile
	 * @param zio
	 */
	public void saveZipEntry(File targetFile, ZipInputStream zio) throws IOException 
	{
		OutputStream os = new FileOutputStream(targetFile);

		// Copy the file out of the zip archive and into the target file.
		byte[] buffer = new byte[1024];
		int len;
		while ((len = zio.read(buffer)) > 0) {
			os.write(buffer, 0, len);
		}
		os.close();
	}

	/**
	 * Creates a Hashmap of file names to file pointers in a given directory
	 * 
	 * @param targetDir
	 *            the source directory be parsed
	 * @return a map of file names to file pointers
	 */
	public Map<String, File> getFileMap(File targetDir) 
	{
		File[] contents = targetDir.listFiles();
		Map<String, File> fileMap = new HashMap<String, File>();
		for (File file : contents) {
			fileMap.put(file.getName(), file);
		}
		return fileMap;
	}
	
	/**
	 * Create a new file within the parent directory and fill it with some data.
	 * 
	 * @param directory
	 *            The parent directory
	 * @param fileName
	 *            The name of the file to create.
	 * @param data
	 *            The data to put into the file, or null for no data.
	 * @return The file pointer of the newly created file.
	 */
	public static File createAndWriteFile(File directory, String fileName, String data) throws IOException {
		
		File file = new File(directory.getCanonicalPath()+File.separator+fileName);
		file.createNewFile();
		
		// Write some some data so the file is not empty.
		if (data != null) {
			FileWriter fw = new FileWriter(file);
			fw.write(data);
			fw.close();
		}
		
		return file;
		
	}

	/**
	 * Read a file and return it's contents as a string.
	 * 
	 * @param file
	 *            The file to be read.
	 * @return The contents of the file.
	 */
	public static String readFile(File file) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	
}
