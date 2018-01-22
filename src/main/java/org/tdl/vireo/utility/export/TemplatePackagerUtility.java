package org.tdl.vireo.utility.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.utility.FileIOUtility;

@Service
public class TemplatePackagerUtility {

	@Autowired
	private FileIOUtility fileIOUtility;
	
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
				String submissionPath = packagerName + "/submission_" + submission.getId() + "/";
				zos.putNextEntry(new ZipEntry(submissionPath + "mets.xml"));
				zos.write(Files.readAllBytes(buildExport(packagerName, submission).toPath()));
				zos.closeEntry();
				for (FieldValue fv : findAttachedFileFieldValues(submission)) {
					zos.putNextEntry(new ZipEntry(submissionPath + fv.getFileName()));
					zos.write(Files.readAllBytes(Paths.get(fv.getValue())));
					zos.closeEntry();
				}
			}
			zos.close();
			fos.close();
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to generate package", ioe);
		}
		return pkg;
	}

	private List<FieldValue> findAttachedFileFieldValues(Submission submission) {
		ArrayList<FieldValue> attachedFiles = new ArrayList<FieldValue>();
		attachedFiles.addAll(submission.getSupplementalAndSourceDocumentFieldValues());
		attachedFiles.add(submission.getPrimaryDocumentFieldValue());
		attachedFiles.addAll(submission.getLicenseDocumentFieldValues());
		return attachedFiles;
	}

	private List<Submission> findSubmissions(User user) {
		return submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns());
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
}
