package forms;

public class Search {

    private String searchTerms = "";

    public Search() {
    }

    public Search(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }
}
