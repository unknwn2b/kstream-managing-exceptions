package com.example.kstream.application.proxy.impl;

import com.example.kstream.application.proxy.FirstServiceProxy;
import com.example.kstream.core.firstdomain.api.FirstService;
import org.javatuples.Pair;

public class DefaultFirstServiceProxy implements FirstServiceProxy {

    private FirstService firstService;

    public DefaultFirstServiceProxy(FirstService firstService) {
        this.firstService = firstService;
    }

    @Override
    public Pair<String, ?> service1(Pair<String, ?> pair) {
        String stringOutput = firstService.service1((String) pair.getValue1());
        return pair.setAt1(stringOutput);
    }

    @Override
    public Pair<String, ?> service2(Pair<String, ?> pair) {
        String stringOutput = firstService.service2((String) pair.getValue1());
        return pair.setAt1(stringOutput);
    }
}
