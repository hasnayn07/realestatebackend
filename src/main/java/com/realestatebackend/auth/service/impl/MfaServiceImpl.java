package com.realestatebackend.auth.service.impl;

import com.realestatebackend.auth.service.MfaService;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MfaServiceImpl implements MfaService {
    @Value("${mfa.issuer}") private String issuer;
    private final CodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());

    @Override public String generateSecret(){ return new DefaultSecretGenerator().generate(); }

    @Override public String otpauthUrl(String secret, String email){
        return new QrData.Builder().label(email).secret(secret).issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1).digits(6).period(30).build().getUri();
    }

    @Override public boolean verify(String secret, String code){ return verifier.isValidCode(secret, code); }
}
