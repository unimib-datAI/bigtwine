package it.unimib.disco.bigtwine.services.socials.connect.support;

public class NameComponents {
    private String fullName;
    private String firstName;
    private String lastName;

    public NameComponents() {
    }

    public NameComponents(String fullName) {
        this.fullName = fullName;
    }

    public NameComponents(String fullName, String firstName, String lastName) {
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
