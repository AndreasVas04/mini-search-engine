package project;

public class Element {

    private WebPage webPage;
    private int probeLength;

    public Element(WebPage webPage) {
        this.webPage = webPage;
        this.probeLength = 0;
    }

    public WebPage getWebPage() {
        return this.webPage;
    }

    public int getProbeLength() {
        return this.probeLength;
    }

    public void incrementProbeLength() {
        this.probeLength++;
    }

    public void resetProbeLength() {
        this.probeLength = 0;
    }
}