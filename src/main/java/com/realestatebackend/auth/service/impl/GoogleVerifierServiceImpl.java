package com.realestatebackend.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.realestatebackend.auth.service.GoogleVerifierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleVerifierServiceImpl implements GoogleVerifierService {
    @Value("${google.client-id}") private String clientId;
    @Override
    public GoogleIdToken.Payload verify(String idToken){
        try {
            var verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(java.util.List.of(clientId)).build();
            var token = verifier.verify(idToken);
            return token != null ? token.getPayload() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
