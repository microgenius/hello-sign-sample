package com.tanriverdi.hellosignsample.service;

import java.io.IOException;

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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl implements ISignService {
    public static final String DEFAULT_SIGNER_ROLE = "Signer";

    private Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

    @Value("${app.hello_sign.api_key}")
    private String helloSignApiKey;

    @Value("${app.hello_sign.template_id}")
    private String templateId;

    @Value("classpath:data/dummy.pdf")
    private Resource dummyPdfResource;

    @Override
    public SignatureRequest signDocument(DocumentSignRequest signRequest) throws HelloSignException, IOException {
        final TemplateSignatureRequest templateSignatureRequest = new TemplateSignatureRequest();
        templateSignatureRequest.setTestMode(true);
        templateSignatureRequest.setTemplateId(templateId);
        templateSignatureRequest.setTitle("Test Title for Document Sign");
        templateSignatureRequest.setSigner(DEFAULT_SIGNER_ROLE, signRequest.getEmail(), signRequest.getName());
        templateSignatureRequest.addFile(dummyPdfResource.getFile());

        final HelloSignClient client = new HelloSignClient(helloSignApiKey);
        return client.sendTemplateSignatureRequest(templateSignatureRequest);
    }

    @Override
    public void documentCallback(final Event event) throws HelloSignException {
        if (!event.isValid(helloSignApiKey)) {
            logger.error("Callback event is not valid, event {}", event);
            return;
        }

        logger.debug("Event data: {}", event);
        switch (event.getTypeString()) {
            case "signature_request_signed":
                logger.info("Signature Request Signed");
                break;
            case "signature_request_sent":
                logger.info("Signature Request Sent");
                break;
            default:
                break;
        }
    }
}
