package api.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import api.model.Article;
import api.model.Comment;
import api.model.Profile;
import api.model.Tag;

public class BuildReturnObject {

	public static JsonObject buildUserObject(Profile profile, String authHeader) {
		JsonObjectBuilder builder = buildProfileBuilder(profile);
		JsonObjectBuilder wrapper = Json.createObjectBuilder();

		builder.add("email", profile.getEmail())
				.add("token", authHeader.replace("Token ", ""));

		wrapper.add("user", builder.build());
		return wrapper.build();
	}

	public static JsonObject buildAuthorObject(Profile author, Profile currentUser) {
		JsonObjectBuilder builder = buildProfileBuilder(author);
		JsonObjectBuilder wrapper = Json.createObjectBuilder();

		builder.add("following", (currentUser != null && currentUser.getFollowing().contains(author)) ? true : false);

		wrapper.add("author", builder.build());
		return wrapper.build();
	}
	
	public static JsonObject buildProfileObject(Profile profile, Profile currentUser) {
		JsonObjectBuilder builder = buildProfileBuilder(profile);
		JsonObjectBuilder wrapper = Json.createObjectBuilder();
		
		builder.add("following", (currentUser != null && currentUser.getFollowing().contains(profile)) ? true : false);

		wrapper.add("profile", builder.build());
		return wrapper.build();
	}
	
	private static JsonObjectBuilder buildProfileBuilder(Profile profile) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add("username", profile.getUsername())
				.add("bio", profile.getBio() == null ? JsonObject.NULL : Json.createValue(profile.getBio()))
				.add("image", profile.getImage() == null ? JsonObject.NULL : Json.createValue(profile.getImage()));
		
		return builder;
	}

	/**
	 * @param article     the article to build a Json object for
	 * @param currentUser the user making the request. null represents an unauthenticated request.
	 * @return the built Json object for the Article
	 */
	public static JsonObject buildArticleObject(Article article, Profile currentUser) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObjectBuilder wrapper = Json.createObjectBuilder();

		builder.add("slug", article.getSlug()).add("title", article.getTitle())
				.add("description", article.getDescription() == null ? JsonObject.NULL : Json.createValue(article.getDescription()))
				.add("body", article.getBody() == null ? JsonObject.NULL : Json.createValue(article.getBody()))
				.add("tagList", buildTagList(article.getTagList())).add("createdAt", formatTime(article.getCreatedAt()))
				.add("updatedAt", formatTime(article.getUpdatedAt()))
				.add("favorited", article.getFavoritedBy().contains(currentUser))
				.add("favoritesCount", article.getFavoritedBy().size())
				.add("author", buildAuthorObject(article.getAuthor(), currentUser));

		wrapper.add("article", builder.build());
		return wrapper.build();
	}

	public static JsonObject buildArticlesObject(List<Article> articles, Profile currentUser) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder wrapper = Json.createObjectBuilder();

		for (Article article : articles) {
			arrayBuilder.add(buildArticleObject(article, currentUser).get("article"));
		}
		JsonArray articlesArrayBuilt = arrayBuilder.build();
		wrapper.add("articles", articlesArrayBuilt);
		wrapper.add("articlesCount", articlesArrayBuilt.size());
		return wrapper.build();
	}

	private static JsonArray buildTagList(Set<Tag> tagList) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		tagList.forEach(tag -> builder.add(tag.getTag()));

		return builder.build();
	}

	public static JsonObject buildCommentObject(Comment comment, Profile currentUser) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObjectBuilder wrapper = Json.createObjectBuilder();

		builder.add("id", comment.getId())
				.add("createdAt", formatTime(comment.getCreatedAt()))
				.add("updatedAt", formatTime(comment.getUpdatedAt()))
				.add("body", comment.getBody())
				.add("author", buildAuthorObject(comment.getAuthor(), currentUser));

		wrapper.add("comment", builder.build());
		return wrapper.build();
	}

	public static JsonObject buildCommentsObject(Iterable<Comment> comments, Profile currentUser) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		JsonObjectBuilder wrapper = Json.createObjectBuilder();

		comments.forEach(comment -> builder.add(buildCommentObject(comment, currentUser).get("comment")));

		wrapper.add("comments", builder.build());
		return wrapper.build();
	}

	private static String formatTime(LocalDateTime time) {
		return time.toString().substring(0, 23).concat("Z");
	}

	public static JsonObject buildErrorObject(String errorMessage) {
		return buildErrorObject(Collections.singletonList(errorMessage));
	}

	public static JsonObject validateRequiredFields(Map<String, Object> parameters) {
		List<String> errorMessages = new ArrayList<>();

		parameters.entrySet().forEach(e -> {
			if (e.getValue() == null) {
				errorMessages.add(e.getKey() + " must be provided");
			} else if (e.getValue().equals("")) {
				errorMessages.add(e.getKey() + " can't be empty");
			} else if (e.getValue() instanceof Integer) {
				if ((Integer) e.getValue() < 0) {
					errorMessages.add(e.getKey() + " can't be negative");
				}
			}
		});

		if (errorMessages.size() == 0) {
			return null;
		} else {
			return buildErrorObject(errorMessages);
		}
	}

	/**
	 * 
	 * @param errorMessages
	 * @return e.g. <code> { "errors":{ "body": [ "can't be empty" ] } }<code>
	 */
	public static JsonObject buildErrorObject(List<String> errorMessages) {
		JsonObjectBuilder errorsWrapper = Json.createObjectBuilder();
		JsonObjectBuilder bodyWrapper = Json.createObjectBuilder();
		JsonArrayBuilder errorsArraybuilder = Json.createArrayBuilder();

		errorMessages.forEach(em -> errorsArraybuilder.add(em));

		bodyWrapper.add("body", errorsArraybuilder.build());
		errorsWrapper.add("errors", bodyWrapper.build());
		return errorsWrapper.build();
	}

}
