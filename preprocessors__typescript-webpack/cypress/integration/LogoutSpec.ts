describe("The Logout Page", function() {
	beforeEach(function() {
		cy.visit("/logout")
	})

	it("checks that we can access logout before logging in", function() {
		cy.visit("/logout")
		cy.location("pathname").should("eq", "/logout")
	})

	it("Given logged in successfully we should be able to logout and not access home", function() {
		cy.visit("/")
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.location("pathname").should("eq", "/home")

		cy.visit("/logout")
		cy.get('.btn').click()

		cy.visit("/home")
		cy.location("pathname").should("eq", "/")
	})
})
