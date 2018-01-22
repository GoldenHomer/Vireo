package org.tdl.vireo.utility.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.utility.export.TemplateFormatterUtility;

@Service
public class TemplatePackagerUtility {
	
	@Autowired
	private SubmissionRepo submissionRepo;
	
	@Autowired
	private TemplateFormatterUtility templateFormatterUtility;
	
	public File packageExports(User user, String packagerName) {		
		File pkg = null;
		try {
			pkg = File.createTempFile(packagerName, ".zip");
			FileOutputStream fos = new FileOutputStream(pkg);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			for (Submission submission : findSubmissions(user)) {
				String manifestName = "submission-" + submission.getId() + "/mets.xml";
				zos.putNextEntry(new ZipEntry(manifestName));
				zos.write(Files.readAllBytes(buildExport(packagerName, submission).toPath()));
				zos.closeEntry();
			}
			zos.close();
			fos.close();
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to generate package", ioe);
		}
		
		return pkg;
	}
	
	public File buildExport(String manifestName, Submission submission) {

		String manifest = templateFormatterUtility.renderManifest(manifestName, submission);
		File manifestFile = null;
		try {
			manifestFile = File.createTempFile(manifestName, null);
			FileUtils.writeStringToFile(manifestFile, manifest, "UTF-8");
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to generate manifest", ioe);
		}
		
		return manifestFile;
	}
	
	private List<Submission> findSubmissions(User user) {
		return submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns());
	}
}
