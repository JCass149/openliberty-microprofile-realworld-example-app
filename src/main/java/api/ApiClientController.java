package api;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * A client facing REST end-point which makes https requests to the system.
 */
@RequestScoped
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiClientController {

    @Inject
    @RestClient
    private ApiClient apiClient;

    @POST
    @Path("/users/login")
    @Tag(ref = "Login")
    @Operation(summary = "Login as an existing User", description = "Enter a valid username and email")
    @RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(ref = "authenticationExample")))
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Login request sent", content = @Content(mediaType = "application/json", schema = @Schema(ref = "userExample")))})
    public String authenticationClientSide(JsonObject user) {
        return apiClient.authentication(user);
    }

    @POST
    @Path("/users")
    @Tag(ref = "Login")
    @Operation(summary = "Create a new User", description = "Enter a unique username and email")
    @RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(ref = "registrationExample")))
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "New User request sent", content = @Content(mediaType = "application/json", schema = @Schema(ref = "userExample")))})
    public String registrationClientSide(JsonObject user) {
        return apiClient.registration(user);
    }

    @GET
    @Path("/user")
    @Tag(ref = "User")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Get Current User", description = "Returns a User that's the current user")
    public String getCurrentUserClientSide(@HeaderParam("Authorization") String authHeader) {
        return apiClient.getCurrentUser(authHeader);
    }

    @PUT
    @Path("/user")
    @Tag(ref = "User")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Update User", description = "Returns the updated User. Accepted fields: email, username, password, image, bio")
    @RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(ref = "updateExample")))
    public String updateUserClientSide(@HeaderParam("Authorization") String authHeader, JsonObject user) {
        return apiClient.updateUser(authHeader, user);
    }

    @GET
    @Path("/profiles/{username}")
    @Tag(ref = "Profile")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Get Profile", description = "Returns a Profile")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Profile retrieved", content = @Content(mediaType = "application/json", schema = @Schema(ref = "profileExample")))})
    public String getProfileClientSide(@HeaderParam("Authorization") String authHeader,
                                       @PathParam("username") String username) {
        return apiClient.getProfile(authHeader, username);
    }

    @POST
    @Path("/profiles/{username}/follow")
    @Tag(ref = "Profile")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Follow User", description = "Returns a Profile")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Profile followed", content = @Content(mediaType = "application/json", schema = @Schema(ref = "profileExample")))})
    public String followUserClientSide(@HeaderParam("Authorization") String authHeader,
                                       @PathParam("username") String username) {
        return apiClient.followUser(authHeader, username);
    }

    @DELETE
    @Path("/profiles/{username}/follow")
    @Tag(ref = "Profile")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Unfollow User", description = "Returns a Profile")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Profile unfollowed", content = @Content(mediaType = "application/json", schema = @Schema(ref = "profileExample")))})
    public String unfollowUserClientSide(@HeaderParam("Authorization") String authHeader,
                                         @PathParam("username") String username) {
        return apiClient.unfollowUser(authHeader, username);
    }

    @GET
    @Path("/articles")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "List Articles", description = "Returns most recent articles globally by default, provide tag, author or favorited query parameter to filter results")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Articles retrieved", content = @Content(mediaType = "application/json", schema = @Schema(ref = "multipleArticlesExample")))})
    public String getListArticlesClientSide(@HeaderParam("Authorization") String authHeader,
                                            @QueryParam("tag") String tag, @QueryParam("author") String author,
                                            @QueryParam("favorited") String favorited, @DefaultValue("20") @QueryParam(value = "limit") int limit,
                                            @DefaultValue("0") @QueryParam("offset") int offset) {
        return apiClient.getListArticles(authHeader, tag, author, favorited, limit, offset);
    }

    @GET
    @Path("/articles/feed")
    @Tag(ref = "Articles")
    @Operation(summary = "Feed Articles", description = "Can also take limit and offset query parameters like List Articles")
    @SecurityRequirement(name = "Authentication")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Articles retrieved", content = @Content(mediaType = "application/json", schema = @Schema(ref = "multipleArticlesExample")))})
    public String getFeedArticlesClientSide(@HeaderParam("Authorization") String authHeader,
                                            @QueryParam("limit") int limit, @QueryParam("offset") int offset) {
        return apiClient.getFeedArticles(authHeader, limit, offset);
    }

    @GET
    @Path("/articles/{slug}")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Get Article", description = "Will return single article")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Article retrieved", content = @Content(mediaType = "application/json", schema = @Schema(ref = "articleExample")))})
    public String getArticleClientSide(@HeaderParam("Authorization") String authHeader,
                                       @PathParam("slug") String slug) {
        return apiClient.getArticle(authHeader, slug);
    }

    @POST
    @Path("/articles")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Create Article", description = "Returns an Article\n\nRequired fields: title, description, body\nOptional fields: tagList as an array of Strings")
    @RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(ref = "createArticleExample")))
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Article created", content = @Content(mediaType = "application/json", schema = @Schema(ref = "articleExample")))})
    public String createArticleClientSide(@HeaderParam("Authorization") String authHeader, JsonObject article) {
        return apiClient.createArticle(authHeader, article);
    }

    @PUT
    @Path("articles/{slug}")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Update an Article", description = "Updates an Article\n\nOptional fields: title, description, body")
    @RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(ref = "updateArticleExample")))
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Article updated", content = @Content(mediaType = "application/json", schema = @Schema(ref = "articleExample")))})
    public String updateArticleClientSide(@HeaderParam("Authorization") String authHeader,
                                          @PathParam("slug") String slug, JsonObject article) {
        return apiClient.updateArticle(authHeader, slug, article);
    }

    @DELETE
    @Path("/articles/{slug}")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Delete Article", description = "Deletes an Article")
    @APIResponses(value = {@APIResponse(responseCode = "200", description = "Article deleted")})
    public String deleteArticleClientSide(@HeaderParam("Authorization") String authHeader,
                                          @PathParam("slug") String slug) {
        return apiClient.deleteArticle(authHeader, slug);
    }

    @POST
    @Path("/articles/{slug}/comments")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Add comment", description = "Adds a comment to an Article\n\nRequired field: body")
    @RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(ref = "addCommentExample")))
    @APIResponses(value = {@APIResponse(responseCode = "200", description = "Comment added", content = @Content(mediaType = "application/json", schema = @Schema(ref = "commentExample")))})
    public String addCommentClientSide(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug,
                                       JsonObject comment) {
        return apiClient.addComment(authHeader, slug, comment);
    }

    @GET
    @Path("/articles/{slug}/comments")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Get comments", description = "Get Comments from an Article")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Comments retrieved", content = @Content(mediaType = "application/json", schema = @Schema(ref = "multipleCommentsExample")))})
    public String getCommentsClientSide(@HeaderParam("Authorization") String authHeader,
                                        @PathParam("slug") String slug) {
        return apiClient.getComments(authHeader, slug);
    }

    @DELETE
    @Path("/articles/{slug}/comments/{id}")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Delete comment", description = "Delete a Comment from an Article")
    @APIResponses(value = {@APIResponse(responseCode = "200", description = "Comment deleted")})
    public String deleteCommentClientSide(@HeaderParam("Authorization") String authHeader,
                                          @PathParam("slug") String slug, @PathParam("id") int commentId) {
        return apiClient.deleteComment(authHeader, slug, commentId);
    }

    @POST
    @Path("/articles/{slug}/favorite")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Favorite Article", description = "Favorite an Article")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Article favorited", content = @Content(mediaType = "application/json", schema = @Schema(ref = "articleExample")))})
    public String favoriteArticleClientSide(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug) {
        return apiClient.favoriteArticle(authHeader, slug);
    }

    @DELETE
    @Path("/articles/{slug}/favorite")
    @Tag(ref = "Articles")
    @SecurityRequirement(name = "Authentication")
    @Operation(summary = "Unfavorite Article", description = "Unfavorite an Article")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Article unfavorited", content = @Content(mediaType = "application/json", schema = @Schema(ref = "articleExample")))})
    public String unfavoriteArticleClientSide(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug) {
        return apiClient.unfavoriteArticle(authHeader, slug);
    }

    @GET
    @Path("/tags")
    @Tag(ref = "Tags")
    @Operation(summary = "Get Tags", description = "Returns a list of all the tags")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "All tags", content = @Content(mediaType = "application/json", schema = @Schema(ref = "listOfTagsExample")))})
    public String getTagsClientSide() {
        return apiClient.getTags();
    }

}