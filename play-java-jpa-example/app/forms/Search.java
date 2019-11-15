package forms;

import play.data.validation.Constraints;

public class Search {
    @Constraints.Required
    String searchTerms;

    public Search() {
    }

    public Search(@Constraints.Required String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }
}
