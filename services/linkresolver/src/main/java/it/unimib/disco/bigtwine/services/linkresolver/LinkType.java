package it.unimib.disco.bigtwine.services.linkresolver;

public enum LinkType {
    dbpediaResource;

    public static class UnrecognizedLinkException extends IllegalArgumentException {

    }

    public static boolean isDbpediaResourceLink(String url) {
        if (url == null) {
            return false;
        }

        return url.startsWith("http://dbpedia.org/resource/") || url.startsWith("https://dbpedia.org/resource/");
    }

    /**
     *
     * @param url
     * @return
     * @throws UnrecognizedLinkException
     */
    public static LinkType getTypeOfLink(String url) {
        if (isDbpediaResourceLink(url)) {
            return dbpediaResource;
        }else {
            throw new UnrecognizedLinkException();
        }
    }
}
