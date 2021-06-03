package api;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.ws.rs.*;

/**
 * The methods that the System, at https://localhost:9443/system, needs to implement.
 */
@RegisterRestClient(baseUri = "https://localhost:9443/system")
@ApplicationScoped
public interface ApiClient extends AutoCloseable {

    @POST
    @Path("/users/login")
    public String authentication(JsonObject user);

    @POST
    @Path("/users")
    public String registration(JsonObject user);

    @GET
    @Path("/user")
    public String getCurrentUser(@HeaderParam("Authorization") String authHeader);

    @PUT
    @Path("/user")
    public String updateUser(@HeaderParam("Authorization") String authHeader, JsonObject user);

    @GET
    @Path("/profiles/{username}")
    public String getProfile(@HeaderParam("Authorization") String authHeader, @PathParam("username") String username);

    @POST
    @Path("/profiles/{username}/follow")
    public String followUser(@HeaderParam("Authorization") String authHeader, @PathParam("username") String username);

    @DELETE
    @Path("/profiles/{username}/follow")
    public String unfollowUser(@HeaderParam("Authorization") String authHeader, @PathParam("username") String username);

    @GET
    @Path("/articles")
    public String getListArticles(@HeaderParam("Authorization") String authHeader, @QueryParam("tag") String tag,
                                  @QueryParam("author") String author, @QueryParam("favorited") String favorited,
                                  @DefaultValue("20") @QueryParam(value = "limit") int limit,
                                  @DefaultValue("0") @QueryParam("offset") int offset);

    @GET
    @Path("/articles/feed")
    public String getFeedArticles(@HeaderParam("Authorization") String authHeader, @QueryParam("limit") int limit,
                                  @QueryParam("offset") int offset);

    @GET
    @Path("/articles/{slug}")
    public String getArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug);

    @POST
    @Path("/articles")
    public String createArticle(@HeaderParam("Authorization") String authHeader, JsonObject article);

    @PUT
    @Path("articles/{slug}")
    public String updateArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug,
                                JsonObject article);

    @DELETE
    @Path("/articles/{slug}")
    public String deleteArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug);

    @POST
    @Path("/articles/{slug}/comments")
    public String addComment(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug, JsonObject comment);

    @GET
    @Path("/articles/{slug}/comments")
    public String getComments(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug);

    @DELETE
    @Path("/articles/{slug}/comments/{id}")
    public String deleteComment(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug, @PathParam("id") int commentId);

    @POST
    @Path("/articles/{slug}/favorite")
    public String favoriteArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug);

    @DELETE
    @Path("/articles/{slug}/favorite")
    public String unfavoriteArticle(@HeaderParam("Authorization") String authHeader, @PathParam("slug") String slug);

    @GET
    @Path("/tags")
    public String getTags();

}