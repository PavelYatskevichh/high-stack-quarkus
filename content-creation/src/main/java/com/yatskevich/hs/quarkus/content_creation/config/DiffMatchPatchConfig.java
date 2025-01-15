package com.yatskevich.hs.quarkus.content_creation.config;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

@Dependent
public class DiffMatchPatchConfig {

    @Produces
    public DiffMatchPatch diffMatchPatch() {
        return new DiffMatchPatch();
    }
}
