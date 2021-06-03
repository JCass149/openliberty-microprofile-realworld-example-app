package api.model;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c")
@Schema(name = "commentExample", type = SchemaType.OBJECT, example =
        "{\n"
                + "  \"comment\": {\n"
                + "    \"id\": 1,\n"
                + "    \"createdAt\": \"2016-02-18T03:22:56.637Z\",\n"
                + "    \"updatedAt\": \"2016-02-18T03:22:56.637Z\",\n"
                + "    \"body\": \"It takes a Jacobian\",\n"
                + "    \"author\": {\n"
                + "      \"username\": \"jake\",\n"
                + "      \"bio\": \"I work at statefarm\",\n"
                + "      \"image\": \"https://i.stack.imgur.com/xHWG8.jpg\",\n"
                + "      \"following\": false\n"
                + "    }\n"
                + "  }\n"
                + "}")
public class Comment implements Serializable {
    private static final long serialVersionUID = -3828470419034556607L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "commentId", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY) // Many Comments can belong to one Author
    @JoinColumn(name = "commentAuthor", nullable = false)
    private Profile author;

    @Column(name = "commentCreatedAt", nullable = true)
    private LocalDateTime createdAt;
    @Column(name = "commentUpdatedAt", nullable = true)
    private LocalDateTime updatedAt;
    @Column(name = "commentBody", nullable = true)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY) // Many Comments can belong to one Article
    @JoinColumn(name = "commentArticle", nullable = false)
    private Article article; // the Article this Comment belongs to


    public Comment() {
    }

    public Comment(Profile author, String body, Article article) {
        this.author = author;
        LocalDateTime createdAt = LocalDateTime.now();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.body = body;
        this.article = article;
    }

    public int getId() {
        return id;
    }

    public Profile getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getBody() {
        return body;
    }
}
