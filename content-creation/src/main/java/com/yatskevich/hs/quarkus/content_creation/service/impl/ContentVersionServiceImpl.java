package com.yatskevich.hs.quarkus.content_creation.service.impl;

import com.yatskevich.hs.quarkus.content_creation.dto.ContentStatusDto;
import com.yatskevich.hs.quarkus.content_creation.dto.RevisionDataDto;
import com.yatskevich.hs.quarkus.content_creation.dto.RevisionDto;
import com.yatskevich.hs.quarkus.content_creation.entity.Content;
import com.yatskevich.hs.quarkus.content_creation.entity.ContentStatus;
import com.yatskevich.hs.quarkus.content_creation.entity.Revision;
import com.yatskevich.hs.quarkus.content_creation.service.ContentService;
import com.yatskevich.hs.quarkus.content_creation.service.ContentVersionService;
import com.yatskevich.hs.quarkus.content_creation.service.DeltaService;
import com.yatskevich.hs.quarkus.content_creation.service.RevisionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class ContentVersionServiceImpl implements ContentVersionService {

    private final ContentService contentService;
    private final RevisionService revisionService;
    private final DeltaService deltaService;

    @Override
    public List<RevisionDto> getAllByContentAndAuthor(UUID contentId, UUID authorId) {
        log.debug("Searching for all the revisions for the content {} of the author {} in the database.",
            contentId, authorId);
        List<Revision> revisions = revisionService.getAllByContentIdAndContentAuthorId(contentId, authorId);
        Content content = contentService.findByIdAndAuthorIdOrElseThrow(contentId, authorId);
        List<RevisionDto> revisionDtos = new ArrayList<>();

        for (Revision revision : revisions) {
            RevisionDto revisionDto = new RevisionDto();
            revisionDto.setContentId(revision.getContent().getId());
            revisionDto.setRevisionNumber(revision.getRevisionNumber());
            revisionDto.setDescription(revision.getDescription());
            revisionDto.setContentTitle(
                deltaService.getText2FromDelta(content.getTitle(), revision.getTitleDelta()));
            revisionDto.setContentDescription(
                deltaService.getText2FromDelta(content.getDescription(), revision.getDescriptionDelta()));
            revisionDto.setContentBody(
                deltaService.getText2FromDelta(content.getBody(), revision.getBodyDelta()));
            revisionDto.setCreatedAt(revision.getCreatedAt());

            revisionDtos.add(revisionDto);
        }

        return revisionDtos;
    }

    @Override
    public void createRevision(RevisionDataDto revisionDataDto, UUID authorId) {
        UUID contentId = revisionDataDto.getContentId();
        Content content = contentService.findByIdAndAuthorIdOrElseThrow(contentId, authorId);

        revisionService.create(content, revisionDataDto);
    }

    @Override
    public void updateStatus(ContentStatusDto contentStatusDto) {
        UUID contentId = contentStatusDto.getId();
        ContentStatus status = ContentStatus.valueOf(contentStatusDto.getStatus().toUpperCase());

        if (status.equals(ContentStatus.SUBMITTED)) {
            log.debug("Searching for the content {} in the database.", contentId);
            Optional<Content> optionalContent = Content.findByIdOptional(contentId);
            Content content = optionalContent.orElseThrow(() -> {
                log.error("The content {} is not found in the database.", contentId);
                //FIXME create exception
                return new RuntimeException("The content %s is not found in the database.".formatted(contentId));
            });

            applyLastRevisionToContent(content);
            content.setStatus(ContentStatus.SUBMITTED);
            content.persist();

        } else {
            log.debug("Changing the status of the content {} to {} in the database.", contentId, status);
            Content.updateStatus(contentId, status);
        }
    }

    private void applyLastRevisionToContent(Content content) {
        UUID contentId = content.getId();
        Revision lastRevision = revisionService.findLastByContentAndAuthor(contentId, content.getAuthorId());

        log.debug("Applying last revision {} for the content {}.", lastRevision.getId(), contentId);
        content.setTitle(
            deltaService.getText2FromDelta(content.getTitle(), lastRevision.getTitleDelta()));
        content.setDescription(
            deltaService.getText2FromDelta(content.getDescription(), lastRevision.getDescriptionDelta()));
        content.setBody(
            deltaService.getText2FromDelta(content.getBody(), lastRevision.getBodyDelta()));

        revisionService.deleteById(contentId);

    }
}
