package api.model;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "article")
@NamedQuery(name = "Article.findAll", query = "SELECT a FROM Article a")
@NamedQuery(name = "Article.findArticleBySlug", query = "SELECT a FROM Article a WHERE a.slug LIKE :slug")
@NamedQuery(name = "Article.findListArticles", query = ""
        + "SELECT DISTINCT a "
        + "FROM Article a LEFT JOIN a.tagList t LEFT JOIN a.favoritedBy f "
        + "WHERE (:tag is null or t.tag = :tag) AND (:author is null or a.author.username = :author) AND (:favorited is null or f.username = :favorited) "
        + "ORDER BY a.createdAt desc")
@NamedQuery(name = "Article.findFeedArticles", query = ""
        + "SELECT a "
        + "FROM Profile u JOIN u.following f JOIN f.published a "
        + "WHERE u.username = :requestedBy")
@Schema(name = "articleExample", type = SchemaType.OBJECT, example =
        "{\n"
                + "  \"article\": {\n"
                + "    \"slug\": \"how-to-train-your-dragon\",\n"
                + "    \"title\": \"How to train your dragon\",\n"
                + "    \"description\": \"Ever wonder how?\",\n"
                + "    \"body\": \"It takes a Jacobian\",\n"
                + "    \"tagList\": [\"dragons\", \"training\"],\n"
                + "    \"createdAt\": \"2016-02-18T03:22:56.637Z\",\n"
                + "    \"updatedAt\": \"2016-02-18T03:48:35.824Z\",\n"
                + "    \"favorited\": false,\n"
                + "    \"favoritesCount\": 0,\n"
                + "    \"author\": {\n"
                + "      \"username\": \"jake\",\n"
                + "      \"bio\": \"I work at statefarm\",\n"
                + "      \"image\": \"https://i.stack.imgur.com/xHWG8.jpg\",\n"
                + "      \"following\": false\n"
                + "    }\n"
                + "  }\n"
                + "}")
public class Article implements Serializable {
    private static final long serialVersionUID = -6855689848742321079L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "articleId", nullable = false)
    private int id;
    @Column(name = "articleSlug", nullable = false)
    private String slug;
    @Column(name = "articleTitle", nullable = false)
    private String title;
    @Column(name = "articleDescription", nullable = true)
    private String description;
    @Column(name = "articleBody", nullable = true)
    private String body;

    @OneToMany // One article can have many tags
    @JoinColumn(name = "articleTagList", nullable = true)
    private Set<Tag> tagList;

    @Column(name = "articleCreatedAt", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "articleUpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany // One article can be favorited by many authors
    @JoinColumn(name = "articleFavoritedBy", nullable = true)
    private Set<Profile> favoritedBy;

    @ManyToOne(fetch = FetchType.LAZY) // Many articles can belong to one author
    @JoinColumn(name = "articleAuthor", nullable = false)
    private Profile author;

    @OneToMany // One article can have many comments
    @JoinColumn(name = "articleComments", nullable = true)
    private Set<Comment> comments;

    public Article() {

    }

    @Override
    public String toString() {
        return "Article [slug=" + slug + ", title=" + title + ", description=" + description + ", body=" + body
                + ", tagList=" + tagList + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", favoritedBy="
                + favoritedBy.size() + ", author=" + author + ", comments=" + comments + "]";
    }

    public Article(String title, String description, String body, Set<Tag> tagList, Profile author) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.tagList = tagList;
        LocalDateTime createdAt = LocalDateTime.now();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.favoritedBy = new HashSet<Profile>();
        this.author = author;
        this.comments = new HashSet<Comment>();

        this.slug = toSlug(title);
    }

    private String toSlug(String title) {
        // "[^a-z0-9-]" For each character NOT (^) present in the the set of characters: a to z, 0 to 9, and -
        // also add a hashcode to make the field unique
        return title.toLowerCase().replaceAll(" ", "-").replaceAll("[^a-z0-9-]", "") + hashCode();

    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.slug = toSlug(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Set<Tag> getTagList() {
        return tagList;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<Profile> getFavoritedBy() {
        return favoritedBy;
    }

    public void addFavoritedBy(Profile favoritedBy) {
        this.favoritedBy.add(favoritedBy);
    }

    public void removeFavoritedBy(Profile favoritedBy) {
        this.favoritedBy.remove(favoritedBy);
    }

    public Profile getAuthor() {
        return author;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }
}
