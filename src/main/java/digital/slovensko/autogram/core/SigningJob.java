package digital.slovensko.autogram.core;

import java.io.File;

import digital.slovensko.autogram.core.eforms.EFormUtils;
import digital.slovensko.autogram.core.eforms.XDCBuilder;
import digital.slovensko.autogram.core.eforms.XDCValidator;
import digital.slovensko.autogram.core.errors.AutogramException;
import digital.slovensko.autogram.util.Logging;
import eu.europa.esig.dss.asic.cades.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.xades.signature.XAdESService;

import static digital.slovensko.autogram.core.AutogramMimeType.*;

public class SigningJob {
    private final Responder responder;
    private final DSSDocument document;
    private final SigningParameters parameters;

    private SigningJob(DSSDocument document, SigningParameters parameters, Responder responder) {
        this.document = document;
        this.parameters = parameters;
        this.responder = responder;
    }

    public DSSDocument getDocument() {
        return this.document;
    }

    public SigningParameters getParameters() {
        return parameters;
    }

    public int getVisualizationWidth() {
        return parameters.getVisualizationWidth();
    }

    public void signWithKeyAndRespond(SigningKey key) throws InterruptedException, AutogramException {

        Logging.log("Signing Job: " + this.hashCode() + " file " + getDocument().getName());
        boolean isContainer = getParameters().getContainer() != null;
        var doc = switch (getParameters().getSignatureType()) {
            case XAdES -> isContainer ? signDocumentAsAsiCWithXAdeS(key) : signDocumentAsXAdeS(key);
            case CAdES -> isContainer ? signDocumentAsASiCWithCAdeS(key) : signDocumentAsCAdeS(key);
            case PAdES -> signDocumentAsPAdeS(key);
            default -> throw new RuntimeException(
                    "Unsupported signature type: " + getParameters().getSignatureType());
        };
        responder.onDocumentSigned(new SignedDocument(doc, key.getCertificate()));
    }

    public void onDocumentSignFailed(AutogramException e) {
        responder.onDocumentSignFailed(e);
    }

    private DSSDocument signDocumentAsCAdeS(SigningKey key) {
        var commonCertificateVerifier = new CommonCertificateVerifier();
        var service = new CAdESService(commonCertificateVerifier);
        var jobParameters = getParameters();
        var signatureParameters = getParameters().getCAdESSignatureParameters();

        signatureParameters.setSigningCertificate(key.getCertificate());
        signatureParameters.setCertificateChain(key.getCertificateChain());
        signatureParameters.setSignWithExpiredCertificate(true);

        var dataToSign = service.getDataToSign(getDocument(), signatureParameters);
        var signatureValue = key.sign(dataToSign, jobParameters.getDigestAlgorithm());

        return service.signDocument(getDocument(), signatureParameters, signatureValue);
    }

    private DSSDocument signDocumentAsAsiCWithXAdeS(SigningKey key) {
        var commonCertificateVerifier = new CommonCertificateVerifier();
        var service = new ASiCWithXAdESService(commonCertificateVerifier);
        var signatureParameters = getParameters().getASiCWithXAdESSignatureParameters();

        signatureParameters.setSigningCertificate(key.getCertificate());
        signatureParameters.setCertificateChain(key.getCertificateChain());
        signatureParameters.setSignWithExpiredCertificate(true);

        var dataToSign = service.getDataToSign(getDocument(), signatureParameters);
        var signatureValue = key.sign(dataToSign, getParameters().getDigestAlgorithm());

        return service.signDocument(getDocument(), signatureParameters, signatureValue);
    }

    private DSSDocument signDocumentAsXAdeS(SigningKey key) {
        var commonCertificateVerifier = new CommonCertificateVerifier();
        var service = new XAdESService(commonCertificateVerifier);
        var jobParameters = getParameters();
        var signatureParameters = getParameters().getXAdESSignatureParameters();

        signatureParameters.setSigningCertificate(key.getCertificate());
        signatureParameters.setCertificateChain(key.getCertificateChain());
        signatureParameters.setSignWithExpiredCertificate(true);

        var dataToSign = service.getDataToSign(getDocument(), signatureParameters);
        var signatureValue = key.sign(dataToSign, jobParameters.getDigestAlgorithm());

        return service.signDocument(getDocument(), signatureParameters, signatureValue);
    }

    private DSSDocument signDocumentAsASiCWithCAdeS(SigningKey key) {
        var commonCertificateVerifier = new CommonCertificateVerifier();
        var service = new ASiCWithCAdESService(commonCertificateVerifier);
        var jobParameters = getParameters();
        var signatureParameters = getParameters().getASiCWithCAdESSignatureParameters();

        signatureParameters.setSigningCertificate(key.getCertificate());
        signatureParameters.setCertificateChain(key.getCertificateChain());
        signatureParameters.setSignWithExpiredCertificate(true);

        var dataToSign = service.getDataToSign(getDocument(), signatureParameters);
        var signatureValue = key.sign(dataToSign, jobParameters.getDigestAlgorithm());

        return service.signDocument(getDocument(), signatureParameters, signatureValue);
    }

    private DSSDocument signDocumentAsPAdeS(SigningKey key) {
        var commonCertificateVerifier = new CommonCertificateVerifier();
        var service = new PAdESService(commonCertificateVerifier);
        var jobParameters = getParameters();
        var signatureParameters = getParameters().getPAdESSignatureParameters();

        signatureParameters.setSigningCertificate(key.getCertificate());
        signatureParameters.setCertificateChain(key.getCertificateChain());
        signatureParameters.setSignWithExpiredCertificate(true);

        var dataToSign = service.getDataToSign(getDocument(), signatureParameters);
        var signatureValue = key.sign(dataToSign, jobParameters.getDigestAlgorithm());

        return service.signDocument(getDocument(), signatureParameters, signatureValue);
    }

    public static FileDocument createDSSFileDocumentFromFile(File file) {
        var fileDocument = new FileDocument(file);

        if (isXDC(fileDocument.getMimeType()) || isXML(fileDocument.getMimeType()) && XDCValidator.isXDCContent(fileDocument))
            fileDocument.setMimeType(AutogramMimeType.XML_DATACONTAINER);

        return fileDocument;
    }

    private static SigningJob build(DSSDocument document, SigningParameters params, Responder responder) {
        if (params.shouldCreateXdc()) {
            var mimeType = document.getMimeType();
            if (!isXDC(mimeType) && !isAsice(mimeType)) {
                document = XDCBuilder.transform(params, document.getName(), EFormUtils.getXmlFromDocument(document));
                document.setMimeType(AutogramMimeType.XML_DATACONTAINER);
            }
        }

        return new SigningJob(document, params, responder);
    }

    public static SigningJob buildFromRequest(DSSDocument document, SigningParameters params, Responder responder) {
        return build(document, params, responder);
    }

    public static SigningJob buildFromFile(File file, Responder responder, boolean checkPDFACompliance, SignatureLevel signatureType, boolean isEn319132) {
        var document = createDSSFileDocumentFromFile(file);
        var parameters = getParametersForFile(document, checkPDFACompliance, signatureType, isEn319132);
        return new SigningJob(document, parameters, responder);
    }

    public static SigningJob buildFromFileBatch(File file, Autogram autogram, Responder responder, boolean checkPDFACompliance, SignatureLevel signatureType, boolean isEn319132) {
        var document = createDSSFileDocumentFromFile(file);
        var parameters = getParametersForFile(document, checkPDFACompliance, signatureType, isEn319132);
        return new SigningJob(document, parameters, responder);
    }

    private static SigningParameters getParametersForFile(FileDocument document, boolean checkPDFACompliance, SignatureLevel signatureType, boolean isEn319132) {
        var level = SignatureValidator.getSignedDocumentSignatureLevel(document);
        if (level != null) switch (level) {
            case PAdES_BASELINE_B:
                return SigningParameters.buildForPDF(document.getName(), document, checkPDFACompliance, isEn319132);
            case XAdES_BASELINE_B:
                return SigningParameters.buildForASiCWithXAdES(document.getName(), document, isEn319132);
            case CAdES_BASELINE_B:
                return SigningParameters.buildForASiCWithCAdES(document.getName(), document, isEn319132);
            default:
                ;
        }

        var filename = document.getName();
        if (isPDF(document.getMimeType())) switch (signatureType) {
            case PAdES_BASELINE_B:
                return SigningParameters.buildForPDF(filename, document, checkPDFACompliance, isEn319132);
            case XAdES_BASELINE_B:
                return SigningParameters.buildForASiCWithXAdES(filename, document, isEn319132);
            case CAdES_BASELINE_B:
                return SigningParameters.buildForASiCWithCAdES(filename, document, isEn319132);
            default:
                ;
        }

        return SigningParameters.buildForASiCWithXAdES(filename, document, isEn319132);
    }

    public boolean shouldCheckPDFCompliance() {
        return parameters.getCheckPDFACompliance() && isPDF(document.getMimeType());
    }
}
