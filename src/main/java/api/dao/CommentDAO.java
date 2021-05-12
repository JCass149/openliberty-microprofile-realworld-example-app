package api.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import api.model.Comment;

@RequestScoped
public class CommentDAO {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void createComment(Comment comment) {
        em.persist(comment);
    }

    public Comment readComment(int commentID) {
        return em.find(Comment.class, commentID);
    }

    public void updateComment(Comment comment) {
        em.merge(comment);
    }

    public void deleteComment(Comment comment) {
        em.remove(comment);
    }

    public List<Comment> readAllComments() {
        return em.createNamedQuery("Comment.findAll", Comment.class).getResultList();
    }
    
}
