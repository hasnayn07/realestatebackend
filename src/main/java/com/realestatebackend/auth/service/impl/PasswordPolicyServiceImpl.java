package com.realestatebackend.auth.service.impl;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.realestatebackend.auth.service.PasswordPolicyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordPolicyServiceImpl implements PasswordPolicyService {
    private final Zxcvbn zxcvbn = new Zxcvbn();

    @Override
    public void validate(String password, String email) {
        // pass user context as a List<String> so zxcvbn penalizes passwords containing personal info
        Strength res = zxcvbn.measure(password, List.of(email));
        if (res.getScore() < 3) {
            throw new IllegalArgumentException("Weak password. Use a longer passphrase with symbols/numbers.");
        }
    }
}
