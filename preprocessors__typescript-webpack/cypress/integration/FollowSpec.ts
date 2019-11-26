describe("The following and followers Pages", function() {
	beforeEach(function() {
		cy.visit("/search")
		cy.get("input[name=username]").type("itsmemario")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.contains("Explore").click()
		cy.get("input[name=searchTerms]").type("revil")
		cy.contains("Go!").click()
		cy.get("form > .btn").click()

		cy.clearCookies()

		cy.visit("/")
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.contains("Explore").click()
		cy.get("input[name=searchTerms]").type("mar")
		cy.contains("Go!").click()
		cy.get("form > .btn").click()
		cy.visit("/search")
	})

	it("checks that can follow", function() {
		cy.contains("Go!").click()
		cy.contains("mario").should("not.exist")

		cy.get(":nth-child(1) > .row > .col-2 > form > .btn").click()

		cy.location("pathname").should("eq", "/following")

		cy.contains("ajinkya")
		cy.contains("mario")
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
		cy.contains("mario")
	})
})
