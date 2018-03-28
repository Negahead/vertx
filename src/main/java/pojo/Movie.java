package pojo;

import java.math.BigDecimal;

public class Movie {
    private String title;
    private String director;
    private BigDecimal boxOffice;

    public Movie(String title, String director, BigDecimal boxOffice) {
        this.title = title;
        this.director = director;
        this.boxOffice = boxOffice;
    }

    public Movie() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public BigDecimal getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(BigDecimal boxOffice) {
        this.boxOffice = boxOffice;
    }
}
