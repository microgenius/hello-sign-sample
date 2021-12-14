package com.tanriverdi.hellosignsample.service.type;

import java.io.IOException;

import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.Event;
import com.hellosign.sdk.resource.SignatureRequest;
import com.tanriverdi.hellosignsample.domain.DocumentSignRequest;
import com.tanriverdi.hellosignsample.domain.MultipleSignerDocumentSignRequest;

public interface ISignService {
    SignatureRequest signDocumentWithTemplate(DocumentSignRequest signRequest) throws HelloSignException, IOException;

    SignatureRequest signDocumentWithFile(MultipleSignerDocumentSignRequest signRequest) throws Exception;

    SignatureRequest signDocumentWithFormFields(MultipleSignerDocumentSignRequest signRequest) throws Exception;

    void documentCallback(Event event) throws HelloSignException;
}
