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
import com.tanriverdi.hellosignsample.domain.DocumentSignRequest;
import com.tanriverdi.hellosignsample.service.type.ISignService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl implements ISignService {
    public static final String DEFAULT_SIGNER_ROLE = "Signer";

    private Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

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
    public SignatureRequest signDocumentWithFile(final DocumentSignRequest signRequest) throws HelloSignException, IOException {
        final SignatureRequest request = new SignatureRequest();
        request.setTitle("NDA with Acme Co.");
        request.setSubject("The NDA we talked about");
        request.setMessage("Please sign this NDA and then we can discuss more. Let me know if you have any questions.");
        request.addSigner(signRequest.getEmail(), signRequest.getName());
        request.setTestMode(true);

        try(final InputStream dummyPdfResourceInputStream = dummyPdfResource.getInputStream()) {
            request.addFile(this.convertInputStreamToFile(dummyPdfResourceInputStream, dummyPdfResource.getFilename()));
        }

        HelloSignClient client = new HelloSignClient(helloSignApiKey);
        return client.sendSignatureRequest(request);
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
