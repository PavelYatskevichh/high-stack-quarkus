package com.yatskevich.hs.quarkus.content_creation.service;

public interface DeltaService {

    String getText2FromDelta(String text1, String delta);

    String getDelta(String text1, String text2);
}
