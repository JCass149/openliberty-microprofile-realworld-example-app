package api.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Table(name = "profiles")
@NamedQuery(name = "Profile.findAll", query = "SELECT u FROM Profile u")
@NamedQuery(name = "Profile.findProfileByUsername", query = "SELECT u FROM Profile u WHERE u.username LIKE :username")
@Schema(name="profileExample", type = SchemaType.OBJECT, example = 
		"{\n"
		+ "  \"profile\": {\n"
		+ "    \"username\": \"jake\",\n"
		+ "    \"bio\": \"I work at statefarm\",\n"
		+ "    \"image\": \"https://static.productionready.io/images/smiley-cyrus.jpg\",\n"
		+ "    \"following\": false\n"
		+ "  }\n"
		+ "}")
public class Profile implements Serializable {
	private static final long serialVersionUID = 8577272641643291862L;
	
	@Id
    @Column(name = "profileEmail", nullable = false) private String email;
    @Column(name = "profileUsername", unique = true, nullable = false) private String username;
    @Column(name = "profileBio", nullable = true) private String bio;
    @Column(name = "profileImage", nullable = true) private String image;
    
    @OneToMany // One Profile (user) can follow many other Profiles (users) 
    @JoinColumn(name = "profileFollowing",  nullable = true) private Set<Profile> following;
    
    @OneToMany // One Profile (user) can publish many Articles 
    @JoinColumn(name = "profilePublished",  nullable = true) private Set<Article> published;

	@Column(name = "profilePassword") private String password; //TODO: should this really be stored here? perhaps encrypt before storing? or should this be front-ends job?

	public Profile() {
    }

    public Profile(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.following = new HashSet<>();
        this.published = new HashSet<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public Set<Profile> getFollowing() {
		return following;
	}

	public void setFollowing(Set<Profile> following) {
		this.following = following;
	}
    
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void addPublished(Article published) {
		this.published.add(published);
	}
	
	public void removePublished(Article published) {
		this.published.remove(published);
	}

	@Override
	public String toString() {
		return "Profile [email=" + email + ", username=" + username + ", bio=" + bio + ", image=" + image
				+ ", following=" + following.size() + ", published=" + published.size() + "]";
	}
}
