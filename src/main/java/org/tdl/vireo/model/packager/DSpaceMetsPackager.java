package org.tdl.vireo.model.packager;

import javax.persistence.Entity;

import org.tdl.vireo.model.formatter.AbstractFormatter;

@Entity
public class DSpaceMetsPackager extends AbstractPackager {

    public DSpaceMetsPackager() {

    }

    public DSpaceMetsPackager(String name, AbstractFormatter formatter, String manifestPath, String primaryDocumentPath, String supplementalDocumentPath, String licenseDocumentPath) {
        setName(name);
        setFormatter(formatter);
        setManifestPath("DSpaceMETS");
        setPrimaryDocumentPath(primaryDocumentPath);
        setSupplementalDocumentPath(supplementalDocumentPath);
        setLicenseDocumentPath();
    }
}
