package com.example.kstream.core.firstdomain.service;

import com.example.kstream.common.exception.ApplicativeError;
import com.example.kstream.common.exception.ApplicativeException;
import com.example.kstream.core.firstdomain.api.FirstService;

public class DefaultFirstService implements FirstService {

    @Override
    public String service1(String string) {
        if ("error".equals(string)) {
            throw new ApplicativeException(ApplicativeError.EXAMPLE_ERROR);
        }
        return string;
    }

    @Override
    public String service2(String string) {
        if ("error_bis".equals(string)) {
            throw new ApplicativeException(ApplicativeError.EXAMPLE_ERROR_BIS);
        }
        return string;
    }
}
