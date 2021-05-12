package api.rest;

import static api.utils.BuildReturnObject.buildArticleObject;
import static api.utils.BuildReturnObject.buildArticlesObject;
import static api.utils.BuildReturnObject.buildCommentObject;
import static api.utils.BuildReturnObject.buildCommentsObject;
import static api.utils.BuildReturnObject.buildErrorObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import api.dao.ArticleDAO;
import api.dao.CommentDAO;
import api.dao.ProfileDAO;
import api.dao.TagDAO;
import api.model.Article;
import api.model.Comment;
import api.model.Profile;
import api.model.Tag;

/**
 * A resource for processing requests related to Users
 */
@RequestScoped
@Path("system/articles")
public class ArticleResource {

	@Inject
	private ProfileDAO profileDAO;

	@Inject
	private ArticleDAO articleDAO;

	@Inject
	private CommentDAO commentDAO;

	@Inject
	private TagDAO tagDAO;

	@Inject
	@Claim("sub")
	private ClaimValue<String> currentUsersName;

	@Inject
	@Claim("upn")
	private ClaimValue<String> currentUsersEmail;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getListArticles(@HeaderParam("Authorization") String authHeader, @QueryParam("tag") String tag,
			@QueryParam("author") String author, @QueryParam("favorited") String favorited,
			@QueryParam("limit") int limit, @QueryParam("offset") int offset) {

		List<Article> articles = articleDAO.readListArticles(tag, author, favorited, limit, offset);

		Profile currentUser = getCurrentUser();
		JsonObject articlesObject = buildArticlesObject(articles, currentUser);

		return Response.ok(articlesObject).build();
	}

	@GET
	@Path("/feed")
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFeedArticles(@HeaderParam("Authorization") String authHeader, @QueryParam("limit") int limit,
			@QueryParam("offset") int offset) {

		Profile currentUser = getCurrentUser();
		List<Article> articles = articleDAO.readFeedArticles(currentUser.getUsername());

		JsonObject articlesObject = buildArticlesObject(articles, currentUser);

		return Response.ok(articlesObject).build();
	}

	@GET
	@Path("/{slug}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug) {

		Article article = articleDAO.readArticleBySlug(slug);

		Profile currentUser = getCurrentUser();
		JsonObject newArticleObject = buildArticleObject(article, currentUser);

		return Response.ok(newArticleObject).build();
	}

	@POST
	@RolesAllowed("user")
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createArticle(@HeaderParam("Authorization") String authHeader, JsonObject article) {

		// unwrap user
		JsonObject articleObject = article.getJsonObject("article");

		String title = articleObject.getString("title");
		String description = articleObject.getString("description");
		String body = articleObject.getString("body");

		Set<Tag> tagList = buildTagList(articleObject);

		Profile currentUser = getCurrentUser();
		Article newArticle = new Article(title, description, body, tagList, currentUser);

		articleDAO.createArticle(newArticle);
		currentUser.addPublished(newArticle);
		profileDAO.updateProfile(currentUser);

		JsonObject newArticleObject = buildArticleObject(newArticle, currentUser);

		return Response.ok(newArticleObject).build();

	}

	@PUT
	@Path("/{slug}")
	@RolesAllowed("user")
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug,
			JsonObject article) {

		// unwrap user
		JsonObject articleObject = article.getJsonObject("article");

		String title = articleObject.getString("title", null);
		String description = articleObject.getString("description", null);
		String body = articleObject.getString("body", null);

		Article newArticle = articleDAO.readArticleBySlug(slug);

		if (title != null && !title.equals("")) {
			newArticle.setTitle(title);
		} // && profileDAO.findProfileByUsername(username).size()==0
		if (description != null && !description.equals("")) {
			newArticle.setDescription(description);
		}
		if (body != null && !body.equals("")) {
			newArticle.setBody(body);
		}

		articleDAO.updateArticle(newArticle);
		newArticle = articleDAO.readArticleBySlug(slug);

		Profile currentUser = getCurrentUser();

		JsonObject newArticleObject = buildArticleObject(newArticle, currentUser);

		return Response.ok(newArticleObject).build();
	}

	@DELETE
	@Path("/{slug}")
	@RolesAllowed("user")
	@Transactional
	public Response deleteArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug) {
		Article article = articleDAO.readArticleBySlug(slug);
		articleDAO.deleteArticle(article);

		Profile author = article.getAuthor();
		author.removePublished(article);
		profileDAO.updateProfile(author);

		for (Comment comment : article.getComments()) {
			commentDAO.deleteComment(comment);
		}

		return Response.ok().build();
	}

	@POST
	@Path("/{slug}/comments")
	@RolesAllowed("user")
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addComment(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug,
			JsonObject comment) {
		// unwrap comment
		String commentBody = comment.getJsonObject("comment").getString("body", null);
		if (commentBody == null) {
			return Response.status(422).entity(buildErrorObject("Comment body cannot be empty")).build();
		}
		Profile author = getCurrentUser();
		Article article = articleDAO.readArticleBySlug(slug);
		Comment newComment = new Comment(author, commentBody, article);

		commentDAO.createComment(newComment);

		article.addComment(newComment);
		articleDAO.updateArticle(article);

		JsonObject commentObject = buildCommentObject(newComment, author);

		return Response.ok(commentObject).build();
	}

	@GET
	@Path("/{slug}/comments")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public Response getComments(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug) {

		Article article = articleDAO.readArticleBySlug(slug);
		Set<Comment> articleComments = article.getComments();

		Profile currentUser = getCurrentUser();

		JsonObject commentObject = buildCommentsObject(articleComments, currentUser);

		return Response.ok(commentObject).build();
	}

	@DELETE
	@Path("/{slug}/comments/{id}")
	@RolesAllowed("user")
	@Transactional
	public Response deleteComment(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug,
			@PathParam("id") int commentId) {

		Article article = articleDAO.readArticleBySlug(slug);
		Comment comment = commentDAO.readComment(commentId);

		if (article.getComments().contains(comment)) {
			commentDAO.deleteComment(comment);
			article.removeComment(comment);
			articleDAO.updateArticle(article);
		} else {
			return Response.status(422).entity(buildErrorObject("The Comment does not belong to the given Article"))
					.build();
		}

		return Response.ok().build();
	}

	@POST
	@Path("/{slug}/favorite")
	@RolesAllowed("user")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public Response favoriteArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug) {
		Article article = articleDAO.readArticleBySlug(slug);
		Profile currentUser = getCurrentUser();

		article.addFavoritedBy(currentUser);
		articleDAO.updateArticle(article);

		JsonObject articleObject = buildArticleObject(article, currentUser);

		return Response.ok(articleObject).build();
	}

	@DELETE
	@Path("/{slug}/favorite")
	@RolesAllowed("user")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public Response unfavoriteArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug) {
		Article article = articleDAO.readArticleBySlug(slug);
		Profile currentUser = getCurrentUser();

		article.removeFavoritedBy(currentUser);
		articleDAO.updateArticle(article);

		JsonObject articleObject = buildArticleObject(article, currentUser);

		return Response.ok(articleObject).build();
	}

	/**
	 * @return the Profile of the user sending the request. null if not authenticated.
	 */
	private Profile getCurrentUser() {
		String currentUsersName = this.currentUsersName.getValue();
		return currentUsersName == null ? null : profileDAO.findProfileByUsername(currentUsersName);
	}

	/*
	 * This is awful. Would be happy to hear if there's a better way to turn JsonArray -> Set<Tag>
	 */
	private Set<Tag> buildTagList(JsonObject articleObject) {
		Set<Tag> tagList = new HashSet<>();

		articleObject.getJsonArray("tagList").forEach((tagJson) -> {

			String tag = tagJson.toString();
			tag = tag.substring(1, tag.length() - 1); // Trim off the quotes, e.g. "example" -> example

			Tag tagObject = tagDAO.readTag(tag); // See if the Tag already exists

			if (tagObject == null) { // If the Tag doesn't already exist, add it to the database
				tagObject = new Tag(tag);
				tagDAO.createTag(tagObject);
			}
			tagList.add(tagObject);

		});

		return tagList;
	}

}