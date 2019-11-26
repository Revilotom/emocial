describe("The Home Page", function() {
	beforeEach(function() {
		cy.visit("/")
		cy.get("input[name=username]").type("itsmemario")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.get("textarea[name=content]").type("Mario is posting~~~")
		cy.contains("Submit").click()
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

	it("checks that searching for nothing shows all users except for ones you already follow", function() {
		cy.contains("Go!").click()
		cy.contains("mario").should("not.exist")

		cy.contains("kunal54")
		cy.contains("ajinkya69")
	})
})
