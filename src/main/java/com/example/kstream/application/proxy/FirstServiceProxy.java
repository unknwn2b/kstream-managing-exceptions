package com.example.kstream.application.proxy;

import org.javatuples.Pair;

public interface FirstServiceProxy {

    Pair<String, ?> service1 (Pair<String, ?> pair);

    Pair<String, ?> service2 (Pair<String, ?> pair);
}
