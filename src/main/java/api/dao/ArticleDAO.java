package api.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import api.model.Article;

@RequestScoped
public class ArticleDAO {

	@PersistenceContext(name = "jpa-unit")
	private EntityManager em;

	public void createArticle(Article article) {
		em.persist(article);
	}

	public Article readArticle(int id) {
		return em.find(Article.class, id);
	}
	
	public Article readArticleBySlug(String slug) {
		return em.createNamedQuery("Article.findArticleBySlug", Article.class)
				.setParameter("slug", slug)
				.getSingleResult();
	}

	public void updateArticle(Article article) {
		em.merge(article);
	}

	public void deleteArticle(Article article) {
		em.remove(article);
	}

	public List<Article> readAllArticles() {
		return em.createNamedQuery("Article.findAll", Article.class).getResultList();
	}

	public List<Article> readListArticles(String tag, String author, String favoritedBy, int limit, int offset) {
		return em.createNamedQuery("Article.findListArticles", Article.class)
				.setParameter("tag", tag)
				.setParameter("author", author)
				.setParameter("favorited", favoritedBy)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}

	public List<Article> readFeedArticles(String requestedBy) {
		return em.createNamedQuery("Article.findFeedArticles", Article.class)
				.setParameter("requestedBy", requestedBy)
				.getResultList();
	}

}
