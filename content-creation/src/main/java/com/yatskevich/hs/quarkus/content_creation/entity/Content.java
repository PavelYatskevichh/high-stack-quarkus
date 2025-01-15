package com.yatskevich.hs.quarkus.content_creation.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@NamedQueries({
    @NamedQuery(name = "Content.updateStatus", query = """
        UPDATE Content
        SET status = :status
        WHERE id = :id
        """)
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "content_creation", name = "content_items")
public class Content extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ContentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        schema = "content_creation",
        name = "content_item_tags",
        joinColumns = {@JoinColumn(name = "content_item_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")}
    )
    private List<Tag> tags;

    public static List<Content> findAllByAuthorId(UUID authorId) {
        return list("authorId", authorId);
    }

    public static Optional<Content> findByIdAndAuthorId(UUID contentId, UUID authorId) {
        return find("id = ?1 and authorId = ?2", contentId, authorId).firstResultOptional();
    }

    public static int updateStatus(UUID id, ContentStatus status) {
        return update("#Content.updateStatus", Parameters.with("status", status).and("id", id));
    }
}
