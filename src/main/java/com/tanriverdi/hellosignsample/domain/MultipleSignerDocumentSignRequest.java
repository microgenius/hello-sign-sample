package com.tanriverdi.hellosignsample.domain;

import java.util.List;

public class MultipleSignerDocumentSignRequest {
    private List<DocumentSignRequest> signers;

    public List<DocumentSignRequest> getSigners() {
        return signers;
    }

    public void setSigners(final List<DocumentSignRequest> signers) {
        this.signers = signers;
    }
}
