package com.yatskevich.hs.quarkus.content_creation.resource;

import com.yatskevich.hs.quarkus.content_creation.dto.RevisionDataDto;
import com.yatskevich.hs.quarkus.content_creation.dto.RevisionDto;
import com.yatskevich.hs.quarkus.content_creation.service.ContentVersionService;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.ResponseStatus;

@Slf4j
@Path("/v1/revisions")
@RequiredArgsConstructor
public class RevisionResource {

    private final ContentVersionService contentVersionService;

    @GET
    public List<RevisionDto> getAllForContent(@QueryParam("contentId") UUID contentId,
                                              @QueryParam("authorId") UUID authorId) {
        log.debug("Getting all the revisions of the content {} of the author {}.", contentId, authorId);
        return contentVersionService.getAllByContentAndAuthor(contentId, authorId);
    }

    @POST
    @ResponseStatus(201)
    public void create(@QueryParam("authorId") UUID authorId,
                       @Valid RevisionDataDto revisionDataDto) {
        log.debug("Creating new revision for the content {} by author {}.", revisionDataDto.getContentId(), authorId);
        contentVersionService.createRevision(revisionDataDto, authorId);
    }
}
