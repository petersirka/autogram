package digital.slovensko.autogram.util;

import digital.slovensko.autogram.core.AutogramMimeType;
import digital.slovensko.autogram.core.errors.MultipleOriginalDocumentsFoundException;
import digital.slovensko.autogram.core.errors.OriginalDocumentNotFoundException;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESContainerExtractor;
import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;

public class AsicContainerUtils {
    public static DSSDocument getOriginalDocument(DSSDocument asice) {
        var documentValidator = SignedDocumentValidator.fromDocument(asice);
        documentValidator.setCertificateVerifier(new CommonCertificateVerifier());
        var signatures = documentValidator.getSignatures();
        if (signatures.isEmpty())
            throw new OriginalDocumentNotFoundException("No signatures in document");

        var extractor = new ASiCWithXAdESContainerExtractor(asice);
        var aSiCContent = extractor.extract();

        if (aSiCContent.getAllDocuments().isEmpty())
            throw new OriginalDocumentNotFoundException("No documents in container");

        if (aSiCContent.getSignedDocuments().isEmpty())
            throw new OriginalDocumentNotFoundException("No signed documents in container");

        if (aSiCContent.getSignedDocuments().size() > 1)
            throw new MultipleOriginalDocumentsFoundException("Multiple original documents found in the container");

        var originalDocument = aSiCContent.getSignedDocuments().get(0);
        if (originalDocument.getMimeType().equals(MimeTypeEnum.XML))
            setMimeTypeFromManifest(asice, originalDocument);

        return originalDocument;
    }

    private static void setMimeTypeFromManifest(DSSDocument asiceContainer, DSSDocument documentToDisplay) {
        var manifest = getManifest(asiceContainer);
        if (manifest == null) {
            return;
        }

        var documentName = documentToDisplay.getName();
        var mimeType = getMimeTypeFromManifest(manifest, documentName);
        if (mimeType == null) {
            return;
        }

        documentToDisplay.setMimeType(mimeType);
    }

    private static DSSDocument getManifest(DSSDocument originalDocument) {
        var extractor = new ASiCWithXAdESContainerExtractor(originalDocument);
        var aSiCContent = extractor.extract();
        var manifestDocuments = aSiCContent.getManifestDocuments();
        if (manifestDocuments.isEmpty()) {
            return null;
        }
        return manifestDocuments.get(0);
    }

    private static MimeType getMimeTypeFromManifest(DSSDocument manifest, String documentName) {
        var fileEntries = getFileEntriesFromManifest(manifest);
        if (fileEntries == null) {
            return null;
        }

        for (int i = 0; i < fileEntries.getLength(); i++) {
            var attributes = fileEntries.item(i).getAttributes();
            if (attributes.getLength() < 2) {
                continue;
            }
            var fileName = attributes.item(0).getNodeValue();
            var fileType = attributes.item(1).getNodeValue();

            if (documentName.equals(fileName)) {
                return AutogramMimeType.fromMimeTypeString(fileType);
            }
        }

        return null;
    }

    private static NodeList getFileEntriesFromManifest(DSSDocument manifest) {
        try {
            var builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            var document = builderFactory.newDocumentBuilder().parse(new InputSource(manifest.openStream()));
            return document.getDocumentElement().getElementsByTagName("manifest:file-entry");
        } catch (Exception e) {
            return null;
        }
    }
}
