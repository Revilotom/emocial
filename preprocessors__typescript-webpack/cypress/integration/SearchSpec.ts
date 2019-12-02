import { gotToSearch, submitSearch, submitPost, marioWritePost, kunalWritePost } from "../helpers.ts/methods"

describe("The Home Page", function() {
	beforeEach(function() {
		kunalWritePost()

		cy.visit("/")
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		gotToSearch()
		cy.get("input[name=searchTerms]").type("mar")
		submitSearch()
		cy.get("form > .btn").click()
		cy.visit("/search")
	})

	it("checks that searching for nothing shows all users except for ones you already follow", function() {
		submitSearch()
		cy.contains("mario").should("not.exist")

		cy.contains("kunal")
		cy.contains("ajinkya69")
	})

	it("checks that you can click on a users name to see their posts", function() {
		submitSearch()
		cy.contains("mario").should("not.exist")
		cy.contains("kunal").click()
		cy.contains("🤡")
	})
})
