package api.rest;

import api.dao.ProfileDAO;
import api.model.Profile;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

import static api.utils.BuildReturnObject.buildProfileObject;

/**
 * A resource for processing requests related to Users
 */
@RequestScoped
@Path("system/profiles")
@Produces(MediaType.APPLICATION_JSON)
public class ProfileResource {

    @Inject
    private ProfileDAO profileDAO;

    @Inject
    @Claim("sub")
    private ClaimValue<String> currentUsersName;

    @Inject
    @Claim("upn")
    private ClaimValue<String> currentUsersEmail;

    @GET
    @Path("{username}")
    public Response getProfile(@HeaderParam("Authorization") String authHeader,
                               @PathParam("username") String username) {

        Profile profile = profileDAO.findProfileByUsername(username);

        Profile currentUser = null;
        if (authHeader != null) {
            currentUser = profileDAO.readProfile(currentUsersEmail.getValue());
        }

        JsonObject profileObject = buildProfileObject(profile, currentUser);

        return Response.ok(profileObject).build();
    }

    @POST
    @Path("{username}/follow")
    @RolesAllowed("user")
    @Transactional
    public Response followUser(@HeaderParam("Authorization") String authHeader,
                               @PathParam("username") String username) {

        Profile profile = profileDAO.findProfileByUsername(username);
        Profile currentUser = profileDAO.readProfile(currentUsersEmail.getValue());

        Set<Profile> following = currentUser.getFollowing();

        if (!following.contains(profile)) {
            following.add(profile);
            currentUser.setFollowing(following);
            profileDAO.updateProfile(currentUser);
        }

        JsonObject profileObject = buildProfileObject(profile, currentUser);

        return Response.ok(profileObject).build();
    }

    @DELETE
    @Path("{username}/follow")
    @RolesAllowed("user")
    @Transactional
    public Response unfollowUser(@HeaderParam("Authorization") String authHeader,
                                 @PathParam("username") String username) {

        Profile profile = profileDAO.findProfileByUsername(username);
        Profile currentUser = profileDAO.readProfile(currentUsersEmail.getValue());

        Set<Profile> following = currentUser.getFollowing();

        if (following.contains(profile)) {
            following.remove(profile);
            currentUser.setFollowing(following);
            profileDAO.updateProfile(currentUser);
        }

        JsonObject profileObject = buildProfileObject(profile, currentUser);

        return Response.ok(profileObject).build();
    }

}