package api.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import api.model.Tag;

@RequestScoped
public class TagDAO {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void createTag(Tag tag) {
        em.persist(tag);
    }

    public Tag readTag(String tag) {
        return em.find(Tag.class, tag);
    }

    public void updateTag(Tag tag) {
        em.merge(tag);
    }

    public void deleteTag(Tag tag) {
        em.remove(tag);
    }

    public List<Tag> readAllTags() {
        return em.createNamedQuery("Tag.findAll", Tag.class).getResultList();
    }

}
