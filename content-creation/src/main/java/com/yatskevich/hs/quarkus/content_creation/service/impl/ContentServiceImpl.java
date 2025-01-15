package com.yatskevich.hs.quarkus.content_creation.service.impl;

import com.yatskevich.hs.quarkus.content_creation.dto.ContentDataDto;
import com.yatskevich.hs.quarkus.content_creation.dto.ContentDto;
import com.yatskevich.hs.quarkus.content_creation.dto.ContentTagsDto;
import com.yatskevich.hs.quarkus.content_creation.entity.Content;
import com.yatskevich.hs.quarkus.content_creation.entity.ContentStatus;
import com.yatskevich.hs.quarkus.content_creation.entity.Tag;
import com.yatskevich.hs.quarkus.content_creation.mapper.ContentMapper;
import com.yatskevich.hs.quarkus.content_creation.service.ContentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class ContentServiceImpl implements ContentService {

    private static final String ADD_OP_KEY_WORD = "added";
    private static final String DELETE_OP_KEY_WORD = "deleted";

    private final ContentMapper contentMapper;

    @Override
    @Transactional
    public List<ContentDto> getAll() {
        log.debug("Searching for all the content in the database.");
        return contentMapper.toDtoList(Content.listAll());
    }

    @Override
    @Transactional
    public ContentDto getById(UUID contentId) {
        return contentMapper.toDto(findByIdOrElseThrow(contentId));
    }

    private Content findByIdOrElseThrow(UUID contentId) {
        log.debug("Searching for the content {} in the database.", contentId);
        Optional<Content> optionalContent = Content.findByIdOptional(contentId);
        return optionalContent.orElseThrow(() -> {
            log.error("The content {} is not found in the database.", contentId);
            //FIXME create exception
            return new RuntimeException("The content %s is not found in the database."
                .formatted(contentId));
        });
    }

    @Override
    @Transactional
    public Content findByIdAndAuthorIdOrElseThrow(UUID contentId, UUID authorId) {
        log.debug("Searching for the content {} of the author {} in the database.", contentId, authorId);
        Optional<Content> optionalContent = Content.findByIdAndAuthorId(contentId, authorId);
        return optionalContent.orElseThrow(() -> {
            log.error("The content {} of the author {} is not found in the database.", contentId, authorId);
            //FIXME create exception
            return new RuntimeException("The content %s of the author %s is not found in the database."
                .formatted(contentId, authorId));
        });
    }

    @Override
    public void create(ContentDataDto contentDataDto, UUID authorId) {
        Content content = Content.builder()
            .title(contentDataDto.getTitle())
            .description(contentDataDto.getDescription())
            .body(contentDataDto.getBody())
            .authorId(authorId)
            .status(ContentStatus.DRAFT)
            .build();

        log.debug("Saving new content {} by author {} to the database.", contentDataDto.getTitle(), authorId);
        content.persist();
    }

    @Override
    public void addTags(ContentTagsDto contentTagsDto, UUID authorId) {
        updateContentTags(contentTagsDto, authorId, List::addAll, ADD_OP_KEY_WORD);
    }

    @Override
    public void deleteTags(ContentTagsDto contentTagsDto, UUID authorId) {
        updateContentTags(contentTagsDto, authorId, List::removeAll, DELETE_OP_KEY_WORD);
    }

    private void updateContentTags(ContentTagsDto contentTagsDto, UUID authorId,
                                   BiConsumer<List<Tag>, List<Tag>> operationOnTags, String opKeyWord) {
        Set<UUID> tagIds = new HashSet<>(contentTagsDto.getTagIds());
        UUID contentId = contentTagsDto.getId();

        List<Tag> tagsToBeProcessed = findAllTagsByIdsOrElseThrow(tagIds, contentId, authorId);
        Content content = findByIdAndAuthorIdOrElseThrow(contentId, authorId);
        List<Tag> currentTags = content.getTags();

        operationOnTags.accept(currentTags, tagsToBeProcessed);

        log.debug("Updating the content {} with {} tags {} to the database.", contentId, opKeyWord, tagIds);
        content.persist();
    }

    private List<Tag> findAllTagsByIdsOrElseThrow(Set<UUID> tagIds, UUID contentId, UUID authorId) {
        log.debug("Searching for the tags {} in the database.", tagIds);
        List<Tag> tags = Tag.findAllById(tagIds);
        List<UUID> nonExistingIds = tags.stream()
            .map(Tag::getId)
            .filter(e -> !tagIds.contains(e))
            .toList();

        if (!nonExistingIds.isEmpty()) {
            log.error("Provided non-existing tag IDs {} when updating the content {} by author {}.",
                tagIds, contentId, authorId);
            //FIXME create exception
            throw new RuntimeException("Provided non-existing tag IDs %s when updating the content %s by author %s."
                .formatted(tagIds, contentId, authorId));
        }

        return tags;
    }

}
