package com.realestatebackend.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
public interface GoogleVerifierService {
    GoogleIdToken.Payload verify(String idToken);
}
