import { gotToSearch, submitSearch, submitPost } from "../helpers.ts/methods"

describe("The Home Page", function() {
	beforeEach(function() {
		cy.visit("/")
		cy.get("input[name=username]").type("itsmemario")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.get("textarea[name=content]").type("👋🏻")
		submitPost()
		cy.clearCookies()

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

		cy.contains("kunal54")
		cy.contains("ajinkya69")
	})
})
