package api.rest;

import api.dao.ProfileDAO;
import api.model.Profile;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static api.utils.BuildReturnObject.*;
import static api.utils.GenerateJWT.buildJwt;

/**
 * A resource for processing Authentication and Registration requests.
 */
@RequestScoped
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    private ProfileDAO profileDAO;

    /**
     * Logs in an existing user.
     * <p>
     * Requires: email, password
     */
    @POST
    @Path("/login")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authentication(JsonObject user) {
        // unwrap user
        JsonObject userObject = user.getJsonObject("user");

        String email = userObject.getString("email", null);
        String password = userObject.getString("password", null);

        Map<String, Object> fields = new HashMap<>();
        fields.put("email", email);
        fields.put("password", password);
        JsonObject invalidFields = validateRequiredFields(fields);
        if (invalidFields != null) {
            return Response.status(422).entity(invalidFields).build();
        }

        Profile thisProfile = profileDAO.readProfile(email);

        if (thisProfile == null) {
            return Response.status(422).entity(buildErrorObject("Invalid email")).build();
        }
        if (!thisProfile.getPassword().equals(password)) {
            return Response.status(422).entity(buildErrorObject("Incorrect password")).build();
        }

        String token;
        try {
            token = buildJwt(thisProfile.getUsername(), thisProfile.getEmail());
        } catch (IOException e) {
            return Response.status(422).entity(buildErrorObject(e.getLocalizedMessage())).build();
        }

        JsonObject thisUser = buildUserObject(thisProfile, token);

        return Response.ok(thisUser).build();
    }

    /**
     * Creates a new user from the submitted data by the user.
     * <p>
     * Requires: username, email, password
     */
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registration(JsonObject user) {

        // unwrap user
        JsonObject userObject = user.getJsonObject("user");

        String email = userObject.getString("email", null);
        String username = userObject.getString("username", null);
        String password = userObject.getString("password", null);

        Map<String, Object> fields = new HashMap<>();
        fields.put("email", email);
        fields.put("username", username);
        fields.put("password", password);
        JsonObject invalidFields = validateRequiredFields(fields);
        if (invalidFields != null) {
            return Response.status(422).entity(invalidFields).build();
        }

        Profile newProfile = new Profile(email, username, password);

        profileDAO.createProfile(newProfile);

        String token = "";
        try {
            token = buildJwt(newProfile.getUsername(), newProfile.getEmail());
        } catch (IOException e) {
            return Response.status(422).entity(buildErrorObject(e.getLocalizedMessage())).build();
        }

        JsonObject newUser = buildUserObject(newProfile, token);

        return Response.ok(newUser).build();
    }

}