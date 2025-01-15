package com.yatskevich.hs.quarkus.content_creation.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@NamedQueries({
    @NamedQuery(name = "Revision.findLastByContentIdAndContentAuthorId", query = """
        FROM Revision r
        LEFT JOIN r.content c
        WHERE c.id = :contentId
        AND c.authorId = :contentAuthorId
        ORDER BY r.revisionNumber DESC
        LIMIT 1
        """),
    @NamedQuery(name = "Revision.findAllByContentIdAndContentAuthorId", query = """
        FROM Revision r
        LEFT JOIN r.content c
        WHERE c.id = :contentId
        AND c.authorId = :contentAuthorId
        ORDER BY r.revisionNumber ASC
        """),
    @NamedQuery(name = "Revision.deleteAllByContentId", query = """
        DELETE
        FROM Revision r
        LEFT JOIN r.content c
        WHERE c.id = :contentId
        """)
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "content_creation", name = "revisions")
public class Revision extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_item_id", nullable = false)
    private Content content;

    @Column(name = "revision_number", nullable = false)
    private Integer revisionNumber;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "title_delta", nullable = false)
    private String titleDelta;

    @Column(name = "description_delta", nullable = false)
    private String descriptionDelta;

    @Column(name = "body_delta", nullable = false)
    private String bodyDelta;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static List<Revision> findAllByContentIdAndContentAuthorId(UUID contentId, UUID authorId) {
        return list("#Revision.findAllByContentIdAndContentAuthorId",
            Parameters.with("contentId", contentId).and("contentAuthorId", authorId));
    }

    public static Optional<Revision> findLastByContentIdAndContentAuthorId(UUID contentId, UUID authorId) {
        return find("#Revision.findLastByContentIdAndContentAuthorId",
            Parameters.with("contentId", contentId).and("contentAuthorId", authorId)).firstResultOptional();
    }

    public static void deleteAllByContentId(UUID contentId) {
        delete("#Revision.deleteAllByContentId", Parameters.with("contentId", contentId));
    }
}
