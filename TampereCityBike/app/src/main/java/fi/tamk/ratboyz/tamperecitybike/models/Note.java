package fi.tamk.ratboyz.tamperecitybike.models;

import java.util.Date;


public class Note {
    private String id;
    private String title;
    private String description;
    private String category;
    private Date created;
    private Date expires;
    private double latitude;
    private double longitude;
    private User author;

    public Note(
            String id,
            String title,
            String category,
            double latitude,
            double longitude
    ) {
        setId(id);
        setTitle(title);
        setCategory(category);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    // Used in API calls in exchange for a Note object.
    public class Proto {
        private String title;
        private String description;
        private String category;
        private double latitude;
        private double longitude;

        public Proto(
                String title,
                String description,
                String category,
                double latitude,
                double longitude
        ) {
            this.title = title;
            this.description = description;
            this.category = category;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
