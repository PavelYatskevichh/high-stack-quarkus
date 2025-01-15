package com.yatskevich.hs.quarkus.content_creation.resource;

import com.yatskevich.hs.quarkus.content_creation.dto.ContentDataDto;
import com.yatskevich.hs.quarkus.content_creation.dto.ContentDto;
import com.yatskevich.hs.quarkus.content_creation.dto.ContentStatusDto;
import com.yatskevich.hs.quarkus.content_creation.dto.ContentTagsDto;
import com.yatskevich.hs.quarkus.content_creation.service.ContentService;
import com.yatskevich.hs.quarkus.content_creation.service.ContentVersionService;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.ResponseStatus;

@Slf4j
@Path("/v1/contents")
@RequiredArgsConstructor
public class ContentResource { //TODO try to implement ContentCreationFeign

    private final ContentService contentService;
    private final ContentVersionService contentVersionService;

    @GET
    public List<ContentDto> getAll() {
        log.debug("Getting all the contents.");
        return contentService.getAll();
    }

    @GET
    @Path("/{id}")
    public ContentDto getById(@PathParam("id") UUID contentId) {
        log.debug("Getting the content {}.", contentId);
        return contentService.getById(contentId);
    }

    @POST
    @ResponseStatus(201)
    public void create(@QueryParam("authorId") UUID authorId,
                       @Valid ContentDataDto contentDataDto) {
        log.debug("Creating new content {} by author {}.", contentDataDto.getTitle(), authorId);
        contentService.create(contentDataDto, authorId);
    }

    @PATCH
    @Path("/tags")
    public void addTags(@QueryParam("authorId") UUID authorId,
                        @Valid ContentTagsDto contentTagsDto) {
        log.debug("Adding tags {} to the content {} by author {}.",
            contentTagsDto.getTagIds(), contentTagsDto.getId(), authorId);
        contentService.addTags(contentTagsDto, authorId);
    }

    @DELETE
    @Path("/tags")
    @ResponseStatus(204)
    public void deleteTags(@QueryParam("authorId") UUID authorId,
                           @Valid ContentTagsDto contentTagsDto) {
        log.debug("Deleting tags {} from the content {} by author {}.",
            contentTagsDto.getTagIds(), contentTagsDto.getId(), authorId);
        contentService.deleteTags(contentTagsDto, authorId);
    }

    //TODO add role dependent logic
    @PUT
    @Path("/status")
    public void updateStatus(@Valid ContentStatusDto contentStatusDto) {
        log.debug("Change status to {} of the content {}.",
            contentStatusDto.getStatus(), contentStatusDto.getId());
        contentVersionService.updateStatus(contentStatusDto);
    }
}
