package com.tanriverdi.hellosignsample.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.hellosign.sdk.HelloSignClient;
import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.Event;
import com.hellosign.sdk.resource.SignatureRequest;
import com.hellosign.sdk.resource.TemplateSignatureRequest;
import com.hellosign.sdk.resource.support.Document;
import com.hellosign.sdk.resource.support.FormField;
import com.hellosign.sdk.resource.support.types.FieldType;
import com.tanriverdi.hellosignsample.domain.DocumentSignRequest;
import com.tanriverdi.hellosignsample.domain.MultipleSignerDocumentSignRequest;
import com.tanriverdi.hellosignsample.service.type.ISignService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SignServiceImpl implements ISignService {
    public static final String DEFAULT_SIGNER_ROLE = "Signer";

    private final Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

    @Value("${app.hello_sign.webhook_url}")
    private String helloSignWebHookUrl;

    @Value("${app.hello_sign.api_key}")
    private String helloSignApiKey;

    @Value("${app.hello_sign.template_id}")
    private String templateId;

    @Value("classpath:data/dummy.pdf")
    private Resource dummyPdfResource;

    @EventListener(ApplicationReadyEvent.class)
    public void initCallbackAction() throws HelloSignException {
        final HelloSignClient helloSignClient = new HelloSignClient(helloSignApiKey);
        helloSignClient.setCallback(helloSignWebHookUrl);
    }

    @Override
    public SignatureRequest signDocumentWithTemplate(DocumentSignRequest signRequest) throws HelloSignException, IOException {
        final TemplateSignatureRequest templateSignatureRequest = new TemplateSignatureRequest();
        templateSignatureRequest.setTestMode(true);
        templateSignatureRequest.setTemplateId(templateId);
        templateSignatureRequest.setTitle("Test Title for Document Sign");
        templateSignatureRequest.setSigner(DEFAULT_SIGNER_ROLE, signRequest.getEmail(), signRequest.getName());

        try(final InputStream dummyPdfResourceInputStream = dummyPdfResource.getInputStream()) {
            templateSignatureRequest.addFile(this.convertInputStreamToFile(dummyPdfResourceInputStream, dummyPdfResource.getFilename()));
        }

        final HelloSignClient client = new HelloSignClient(helloSignApiKey);
        return client.sendTemplateSignatureRequest(templateSignatureRequest);
    }

    @Override
    public SignatureRequest signDocumentWithFile(final MultipleSignerDocumentSignRequest signRequest) throws Exception {
        final SignatureRequest signatureRequestForFile = new SignatureRequest();
        signatureRequestForFile.setTitle("NDA with Acme Co.");
        signatureRequestForFile.setSubject("The NDA we talked about");
        signatureRequestForFile.setMessage("Please sign this NDA and then we can discuss more. Let me know if you have any questions.");
        signatureRequestForFile.setTestMode(true);

        if (CollectionUtils.isEmpty(signRequest.getSigners())) {
            throw new Exception("signers required");
        }

        for (final DocumentSignRequest signer : signRequest.getSigners()) {
            signatureRequestForFile.addSigner(signer.getEmail(), signer.getName());
        }

        try(final InputStream dummyPdfResourceInputStream = dummyPdfResource.getInputStream()) {
            signatureRequestForFile.addFile(this.convertInputStreamToFile(dummyPdfResourceInputStream, dummyPdfResource.getFilename()));
        }

        final HelloSignClient client = new HelloSignClient(helloSignApiKey);
        return client.sendSignatureRequest(signatureRequestForFile);
    }

    @Override
    public SignatureRequest signDocumentWithFormFields(final MultipleSignerDocumentSignRequest signRequest) throws Exception {
        final SignatureRequest signatureRequestForFormFields = new SignatureRequest();
        signatureRequestForFormFields.setTitle("NDA with Acme Co.");
        signatureRequestForFormFields.setSubject("The NDA we talked about");
        signatureRequestForFormFields.setMessage("Please sign this NDA and then we can discuss more. Let me know if you have any questions.");
        signatureRequestForFormFields.setTestMode(true);

        if (CollectionUtils.isEmpty(signRequest.getSigners())) {
            throw new Exception("signers required");
        }

        for (final DocumentSignRequest signer : signRequest.getSigners()) {
            signatureRequestForFormFields.addSigner(signer.getEmail(), signer.getName());
        }

        final Document document = new Document();
        try(final InputStream dummyPdfResourceInputStream = dummyPdfResource.getInputStream()) {
            document.setFile(this.convertInputStreamToFile(dummyPdfResourceInputStream, dummyPdfResource.getFilename()));
        }

        final int widthForField = 100;
        final int heightForField = 16;
        final  int padding = 10;
        int currentX = 100;
        int currentY = 300;
        for (int i = 0; i < signRequest.getSigners().size(); i++) {
            final FormField signature = new FormField();
            signature.setApiId("signature_cd2def5c" + i);
            signature.setName("");
            signature.setType(FieldType.SIGNATURE);
            signature.setX(currentX);
            signature.setY(currentY);
            signature.setWidth(widthForField);
            signature.setHeight(heightForField);
            signature.setSigner(i);
            document.addFormField(signature);

            currentY += heightForField + padding;

            final FormField dateSigned = new FormField();
            dateSigned.setApiId("date_60329879" + i);
            dateSigned.setName("");
            dateSigned.setType(FieldType.DATE_SIGNED);
            dateSigned.setX(currentX);
            dateSigned.setY(currentY);
            dateSigned.setWidth(widthForField);
            dateSigned.setHeight(heightForField);
            dateSigned.setSigner(i);
            document.addFormField(dateSigned);

            currentX += widthForField + padding;
            currentY += heightForField + padding;
        }

        signatureRequestForFormFields.addDocument(document);

        final HelloSignClient client = new HelloSignClient(helloSignApiKey);
        return client.sendSignatureRequest(signatureRequestForFormFields);
    }

    @Override
    public void documentCallback(final Event event) throws HelloSignException {
        if (!event.isValid(helloSignApiKey)) {
            logger.error("Callback event is not valid, event {}", event);
            return;
        }

        switch (event.getTypeString()) {
            case "signature_request_signed":
                logger.info("Signature Request Signed, detail: {}", event);
                break;
            case "signature_request_sent":
                logger.info("Signature Request Sent, detail: {}", event);
                break;
            default:
                break;
        }
    }

    private File convertInputStreamToFile(InputStream inputStream, String filePath) throws IOException {
        final File file = new File(filePath);
        try (OutputStream output = new FileOutputStream(file, false)) {
            inputStream.transferTo(output);
        }

        return file;
    }
}
