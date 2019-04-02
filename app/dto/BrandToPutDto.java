package dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BrandToPutDto {
    @NotNull()
    private long id;

    @NotNull()
    @Size(max=255)
    private String title;

    @NotNull()
    private int yearFounded;

    @NotNull()
    @Size(max=1000)
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYearFounded() {
        return yearFounded;
    }

    public void setYearFounded(int yearFounded) {
        this.yearFounded = yearFounded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
