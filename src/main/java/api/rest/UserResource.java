package api.rest;

import api.dao.ProfileDAO;
import api.model.Profile;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static api.utils.BuildReturnObject.buildUserObject;

/**
 * A resource for processing requests related to Users
 */
@RequestScoped
@Path("system/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private ProfileDAO profileDAO;

    @Inject
    @Claim("sub")
    private ClaimValue<String> currentUsersName;

    @Inject
    @Claim("upn")
    private ClaimValue<String> currentUsersEmail;

    /**
     * This method returns the current user
     */
    @GET
    @RolesAllowed("user")
    @Transactional
    public Response getCurrentUser(@HeaderParam("Authorization") String authHeader) {

        Profile profile = profileDAO.readProfile(currentUsersEmail.getValue());

        JsonObject userObject = buildUserObject(profile, authHeader);

        return Response.ok(userObject).build();
    }

    /**
     * This method updates a new user from the submitted data (email, token, username, bio, image) by the user.
     */
    @PUT
    @RolesAllowed("user")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@HeaderParam("Authorization") String authHeader, JsonObject user) {

        // unwrap user
        JsonObject userObject = user.getJsonObject("user");

        String email = userObject.getString("email", currentUsersEmail.getValue());
        String username = userObject.getString("username", null);
        String password = userObject.getString("password", null);
        String bio = userObject.getString("bio", null);
        String image = userObject.getString("image", null);

        Profile newUser = profileDAO.readProfile(currentUsersEmail.getValue());

        if (email != null) {
            newUser.setEmail(email);
        } // && profileDAO.findProfileByUsername(username).size()==0
        if (username != null) {
            newUser.setUsername(username);
        }
        if (password != null) {
            newUser.setPassword(password);
        }
        if (bio != null) {
            newUser.setBio(bio);
        }
        if (image != null) {
            newUser.setImage(image);
        }

        profileDAO.updateProfile(newUser);
        newUser = profileDAO.readProfile(currentUsersEmail.getValue());

        JsonObject userObjectBuilt = buildUserObject(newUser, authHeader);

        return Response.ok(userObjectBuilt).build();
    }

    /**
     * Not an offical method. Only for testing
     * <p>
     * This method returns all profiles
     */
    @GET
    @Path("all")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "All users retireved", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, ref = "userExample", implementation = JsonObject.class)))})
    public String getAllUsers() {

        return profileDAO.readAllProfiles().toString();
    }

}