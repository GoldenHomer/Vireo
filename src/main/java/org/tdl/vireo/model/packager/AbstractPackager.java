package org.tdl.vireo.model.packager;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

import org.tdl.vireo.model.formatter.AbstractFormatter;
import org.tdl.vireo.utility.FileHelperUtility;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
@Inheritance
public abstract class AbstractPackager extends BaseEntity implements Packager {

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    public AbstractFormatter formatter;

    @Column(unique = true)
    private String name;

    @Column
    private String manifestPath;

    @Column
    private String primaryDocumentPath;

    @Column
    private String supplementalDocumentPath;

    @Column
    private String licenseDocumentPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(AbstractFormatter formatter) {
        this.formatter = formatter;
    }

    public String getManifestPath() {
        return manifestPath;
    }

    public void setManifestPath(String path) {
        this.manifestPath = path;
    }

    public String getPrimaryDocumentPath() {
        return primaryDocumentPath;
    }

    public void setPrimaryDocumentPath(String path) {
        this.primaryDocumentPath = path;
    }

    public String getSupplementalDocumentPath() {
        return supplementalDocumentPath;
    }

    public void setSupplementalDocumentPath(String path) {
        this.supplementalDocumentPath = path;
    }

    public String getLicenseDocumentPath() {
        return licenseDocumentPath;
    }

    public void setLicenseDocumentPath(String path) {
        this.licenseDocumentPath = path;
    }
    
    protected Path getAbsolutePath(String relativePath) {
        return Paths.get(FileHelperUtility.getPath(relativePath));
    }

}
