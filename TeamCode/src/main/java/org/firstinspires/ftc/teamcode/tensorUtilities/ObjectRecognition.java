package org.firstinspires.ftc.teamcode.tensorUtilities;

public class ObjectRecognition {

    // unique id for what is recognized
    private final String id;

    // recognition name
    private final String title;

    // a score for how good the recognition is
    private final Float confidence;

    // location of recognized object
    private RectProperties location;

    public ObjectRecognition(final String id, final String title, final Float confidence, final RectProperties location) {
        this.id = id;
        this.title = title;
        this.confidence = confidence;
        // error popping up here, may be exclusive to my home computer
        //this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Float getConfidence() {
        return confidence;
    }

    public RectProperties getLocation() {
        return new RectProperties(location);
    }

    /* public void setLocation(RectProperties location) { this.location = location; } */

    @Override
    public String toString() {
        String resultString = "";
        if (id != null) {
            resultString += "[" + id + "] ";
        }

        if (title != null) {
            resultString += title + " ";
        }

        if (confidence != null) {
            resultString += String.format("(%.1f%%) ", confidence * 100.0f);
        }

        if (location != null) {
            resultString += location + " ";
        }

        return resultString.trim();
    }
}