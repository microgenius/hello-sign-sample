package com.tanriverdi.hellosignsample.service;

import com.hellosign.sdk.resource.Event;
import com.tanriverdi.hellosignsample.service.type.ISignService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl implements ISignService {
    private Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

    @Value("${app.hello_sign.api_key}")
    private String helloSignApiKey;

    @Override
    public String signDocument() {
        logger.info(helloSignApiKey);
        return null;
    }

    @Override
    public void documentCallback(final Event event) {

    }
}
