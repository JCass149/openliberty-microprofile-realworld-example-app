package api.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import api.model.Profile;

@RequestScoped
public class ProfileDAO {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void createProfile(Profile profile) {
        em.persist(profile);
    }

    public Profile readProfile(String email) {
        return em.find(Profile.class, email);
    }

    public void updateProfile(Profile profile) {
        em.merge(profile);
    }

    public void deleteProfile(Profile profile) {
        em.remove(profile);
    }

    public List<Profile> readAllProfiles() {
        return em.createNamedQuery("Profile.findAll", Profile.class).getResultList();
    }
    
    public Profile findProfileByUsername(String username) {
        return em.createNamedQuery("Profile.findProfileByUsername", Profile.class)
            .setParameter("username", username).getSingleResult();
    }
}
