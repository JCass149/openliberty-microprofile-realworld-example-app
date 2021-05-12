package api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Table(name = "tags")
@NamedQuery(name = "Tag.findAll", query = "SELECT t FROM Tag t")
@Schema(name="tagsExample", type = SchemaType.OBJECT, example = 
		"{\n"
		+ "  \"tags\": [\n"
		+ "    \"reactjs\",\n"
		+ "    \"angularjs\"\n"
		+ "  ]\n"
		+ "}")
public class Tag implements Serializable {
	private static final long serialVersionUID = 8458821809037857106L;
	
	@Id
    @Column(name = "tagTag", nullable = false) private String tag;
    
	public Tag() {
	}

	public Tag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}
}