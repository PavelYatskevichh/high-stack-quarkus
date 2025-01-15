package com.yatskevich.hs.quarkus.content_creation.mapper;

import com.yatskevich.hs.quarkus.content_creation.dto.TagDto;
import com.yatskevich.hs.quarkus.content_creation.entity.Tag;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface TagMapper {

    List<TagDto> toDtoList(List<Tag> tags);
}
