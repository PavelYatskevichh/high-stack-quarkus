package com.yatskevich.hs.quarkus.content_creation.service;

import com.yatskevich.hs.quarkus.content_creation.dto.ContentStatusDto;
import com.yatskevich.hs.quarkus.content_creation.dto.RevisionDataDto;
import com.yatskevich.hs.quarkus.content_creation.dto.RevisionDto;
import java.util.List;
import java.util.UUID;

public interface ContentVersionService {

    List<RevisionDto> getAllByContentAndAuthor(UUID contentId, UUID authorId);

    void createRevision(RevisionDataDto revisionDataDto, UUID authorId);

    void updateStatus(ContentStatusDto contentStatusDto);
}
