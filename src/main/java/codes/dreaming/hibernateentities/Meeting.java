package codes.dreaming.hibernateentities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Meeting {
    private Long id;
    private String name;

    @Id
    @SequenceGenerator(name = "Meeting", sequenceName = "meeting_seq")
    @GeneratedValue(generator = "Meeting")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
