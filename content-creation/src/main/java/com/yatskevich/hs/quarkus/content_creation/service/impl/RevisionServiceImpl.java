package com.yatskevich.hs.quarkus.content_creation.service.impl;

import com.yatskevich.hs.quarkus.content_creation.dto.RevisionDataDto;
import com.yatskevich.hs.quarkus.content_creation.entity.Content;
import com.yatskevich.hs.quarkus.content_creation.entity.Revision;
import com.yatskevich.hs.quarkus.content_creation.service.DeltaService;
import com.yatskevich.hs.quarkus.content_creation.service.RevisionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class RevisionServiceImpl implements RevisionService {

    private final DeltaService deltaService;

    @Override
    public List<Revision> getAllByContentIdAndContentAuthorId(UUID contentId, UUID authorId) {
        log.debug("Searching for all the revisions for the content {} of the author {} in the database.",
            contentId, authorId);
        return Revision.findAllByContentIdAndContentAuthorId(contentId, authorId);
    }

    @Override
    public void deleteById(UUID contentId) {
        log.debug("Removing all the revisions for the content {} in the database.", contentId);
        Revision.deleteAllByContentId(contentId);
    }

    @Override
    public Revision findLastByContentAndAuthor(UUID contentId, UUID authorId) {
        log.debug("Searching for the last revision for the content {} in the database.", contentId);
        Optional<Revision> optionalRevision = Revision.findLastByContentIdAndContentAuthorId(contentId, authorId);
        return optionalRevision.orElseThrow(() -> {
                log.error("There are no revisions of the content {}  in the database.", contentId);
                //FIXME create exception
                return new RuntimeException("There are no revisions of the content %s  in the database."
                    .formatted(contentId));
            });
    }

    @Override
    public void create(Content content, RevisionDataDto revisionDataDto) {
        Optional<Revision> revisionOptional = Revision
            .findAllByContentIdAndContentAuthorId(content.getId(), content.getAuthorId()).stream()
            .max(Comparator.comparingInt(Revision::getRevisionNumber));

        Integer revisionNumber = revisionOptional.map(value -> value.getRevisionNumber() + 1).orElse(1);

        Revision revision = new Revision();
        revision.setContent(content);
        revision.setRevisionNumber(revisionNumber);
        revision.setDescription(revisionDataDto.getDescription());
        revision.setTitleDelta(deltaService.getDelta(content.getTitle(), revisionDataDto.getContentTitle()));
        revision.setDescriptionDelta(deltaService.getDelta(content.getDescription(), revisionDataDto.getContentDescription()));
        revision.setBodyDelta(deltaService.getDelta(content.getBody(), revisionDataDto.getContentBody()));

        revision.persist();
    }
}
