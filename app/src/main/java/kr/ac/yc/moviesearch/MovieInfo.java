package kr.ac.yc.moviesearch;

public class MovieInfo {
    private String title;
    private String link;
    private String imgurl;
    private String year;
    private String director;
    private String actor;
    private String rate;

    public MovieInfo(String title, String link, String imgurl, String year, String director, String actor, String rate) {
        this.title = title;
        this.link = link;
        this.imgurl = imgurl;
        this.year = year;
        this.director = director;
        this.actor = actor;
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getImgurl() {
        return imgurl;
    }

    public String getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getActor() {
        return actor;
    }

    public String getRate() {
        return rate;
    }
}
