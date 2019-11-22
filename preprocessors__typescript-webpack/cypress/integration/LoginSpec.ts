describe("The Login Page", function() {
	beforeEach(function() {
		cy.visit("/")
	})

	it("checks that fields must be filled in", function() {
		cy.contains("Login").click()
		cy.contains("required")
	})

	it("checks that credentials are valid", function() {
		cy.get("input[name=username]").type("dasdhasjkdhkjsdhkashda")
		cy.get("input[name=password]").type("dasdhasjkdhkjsdhkashda")
		cy.contains("Login").click()
		cy.contains("INVALID CREDENTIALS!")
	})

	it("Logins in successfully", function() {
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.location("pathname").should("eq", "/home")
		cy.getCookie("PLAY_SESSION").should("exist")
	})
})
