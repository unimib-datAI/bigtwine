package it.unimib.disco.bigtwine.services.socials.connect.support;

public class BasicFullNameSplitter implements FullNameSplitter {

    public BasicFullNameSplitter() {
    }

    public NameComponents split(String fullName) {
        int idx = fullName.lastIndexOf(' ');
        if (idx == -1)
            return new NameComponents(fullName, fullName, null);
        String firstName = fullName.substring(0, idx);
        String lastName  = fullName.substring(idx + 1);

        return new NameComponents(fullName, firstName, lastName);
    }

}
