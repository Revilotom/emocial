describe("The SignUp Page", function() {
	beforeEach(function() {
		cy.visit("/signUp")
	})

	it("checks that fields must be filled in", function() {
		cy.contains("Sign up!").click()
		cy.contains("required")
	})

	it("checks that name must be at least 5 characters", function() {
		cy.get("input[name=name]").type("1")
		cy.contains("Sign up!").click()
		cy.contains("Error[name]: Minimum length is 5")
	})

	it("checks that username must be at least 5 characters", function() {
		cy.get("input[name=username]").type("1")
		cy.contains("Sign up!").click()
		cy.contains("Error[username]: Minimum length is 5")
	})

	it("checks that username must match pattern", function() {
		cy.get("input[name=username]").type("!@$!@{$}!@:$!@")
		cy.contains("Sign up!").click()
		cy.contains("Error[username]: Must satisfy ^[a-zA-Z0-9 ]*$")
	})

	it("checks that password must be at least 6 characters", function() {
		cy.get("input[name=password1]").type("12345")
		cy.get("input[name=password2]").type("12345")
		cy.contains("Sign up!").click()
		cy.contains("Error[password1]: Minimum length is 8")
	})

	it("checks that passwords must be at the same", function() {
		cy.get("input[name=password1]").type("1234567765")
		cy.get("input[name=password2]").type("1234542342")
		cy.contains("Sign up!").click()
		cy.contains("Passwords dont match!")
	})

	it("checks that passwords must not contain special characters", function() {
		cy.get("input[name=password1]").type("123456[][][]")
		cy.get("input[name=password2]").type("123456[][][]")
		cy.contains("Sign up!").click()
		cy.contains("Error[password1]: Must satisfy ^[a-zA-Z0-9 ]*$")
	})

	it("checks that taken username cannot be selected", function() {
		cy.get("input[name=name]").type("hello")
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password1]").type("12345678")
		cy.get("input[name=password2]").type("12345678")

		cy.contains("Sign up!").click()
		cy.contains("Error[username]: USERNAME TAKEN")
	})

	it("checks sign up can be done successfully", function() {
		cy.get("input[name=name]").type("hello")
		cy.get("input[name=username]").type("newUser")
		cy.get("input[name=password1]").type("12345678")
		cy.get("input[name=password2]").type("12345678")

		cy.contains("Sign up!").click()
		cy.url().should("eq", Cypress.config().baseUrl + "/") // tests won't fail in case the port changes
	})
})
