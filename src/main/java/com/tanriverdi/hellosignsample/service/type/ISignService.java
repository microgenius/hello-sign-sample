package com.tanriverdi.hellosignsample.service.type;

import com.hellosign.sdk.resource.Event;

public interface ISignService {
    String signDocument();

    void documentCallback(Event event);
}
