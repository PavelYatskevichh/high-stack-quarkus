package com.yatskevich.hs.quarkus.content_creation.mapper;

import com.yatskevich.hs.quarkus.content_creation.dto.ContentDto;
import com.yatskevich.hs.quarkus.content_creation.entity.Content;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi",
    uses = {TagMapper.class})
public interface ContentMapper {

    ContentDto toDto(Content content);

    List<ContentDto> toDtoList(List<Content> contents);
}
