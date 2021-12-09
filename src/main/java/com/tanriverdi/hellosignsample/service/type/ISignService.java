package com.tanriverdi.hellosignsample.service.type;

import java.io.IOException;

import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.Event;
import com.hellosign.sdk.resource.SignatureRequest;
import com.tanriverdi.hellosignsample.domain.DocumentSignRequest;

public interface ISignService {
    SignatureRequest signDocument(DocumentSignRequest signRequest) throws HelloSignException, IOException;

    void documentCallback(Event event) throws HelloSignException;
}
