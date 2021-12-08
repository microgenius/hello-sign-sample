package com.tanriverdi.hellosignsample.controller;

import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.Event;
import com.tanriverdi.hellosignsample.service.type.ISignService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sign")
public class SignController {
    public static final String SUCCESSFUL_WEBHOOK_RESPONSE_FOR_HELLO_SIGN = "Hello API Event Received";

    private Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    private ISignService signService;

    @PostMapping
    public String signDocument() {
        return signService.signDocument();
    }

    @PostMapping("/webhook")
    public String documentCallback(@RequestParam String json) {
        try {
            final JSONObject jsonObject = new JSONObject(json);
            final Event callbackEventData = new Event(jsonObject);

            signService.documentCallback(callbackEventData);
        } catch (final HelloSignException helloSignException) {
            logger.error("HelloSign callback data parse error: ", helloSignException);
        }

        return SUCCESSFUL_WEBHOOK_RESPONSE_FOR_HELLO_SIGN;
    }
}
