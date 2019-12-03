import { gotToSearch, submitSearch } from "../helpers.ts/methods"

describe("The following and followers Pages", function() {
	beforeEach(function() {
		cy.visit("/search")
		cy.get("input[name=username]").type("itsmemario")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		gotToSearch()
		cy.get("input[name=searchTerms]").type("revil")
		submitSearch()
		cy.get("form > .btn").click()

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

	it("checks that can follow", function() {
		gotToSearch()
		submitSearch()

		cy.contains("mario").should("not.exist")
		cy.get(':nth-child(1) > .container > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > .row > .col-2 > form > .btn').click()
		cy.location("pathname").should("eq", "/following")
		cy.contains("ajinkya").click()
		cy.location("pathname").should("eq", "/posts/ajinkya69")
	})

	it("checks that can unfollow", function() {
		cy.visit("/following")
		cy.get(".btn").click()
		cy.location("pathname").should("eq", "/following")
		cy.contains("mario").should("not.exist")
	})

	it("checks that can see followers", function() {
		cy.visit("/followers")
		cy.location("pathname").should("eq", "/followers")
		cy.contains("mario").click()
		cy.location("pathname").should("eq", "/posts/itsmemario")
	})
})

// Write about how you planned not to do sucha a complicated project so that you could finish it in time
// Talk about how you wanted to jsut make a simple a product awhere you can use normal letteres
// And then after a proccsss you slowly arrived at the emoji only idea.
// Its good to write on the first slide all of the different features and then show which ines actually made the cut.
// The presentation is justa bout the product its about how you designed, and what decisions you made.
