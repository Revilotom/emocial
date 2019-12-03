describe("The Login Page", function() {
	beforeEach(function() {
		cy.visit("/")
	})

	it("checks that we can access rpublic content before logging in", function() {
		cy.visit("/logout")
		cy.location("pathname").should("eq", "/logout")
		cy.visit("/signUp")
		cy.location("pathname").should("eq", "/signUp")
		cy.visit("/")
		cy.location("pathname").should("eq", "/")
	})

	it("checks that we cannot access restricted content before logging in", function() {
		cy.visit("/home")
		cy.location("pathname").should("eq", "/")

		cy.visit("/search")
		cy.location("pathname").should("eq", "/")

		cy.visit("/following")
		cy.location("pathname").should("eq", "/")

		cy.visit("/followers")
		cy.location("pathname").should("eq", "/")

		cy.visit("/myPosts")
		cy.location("pathname").should("eq", "/")

		cy.visit("/makePost")
		cy.location("pathname").should("eq", "/")
	})

	it("checks that fields must be filled in", function() {
		cy.contains("Login").click()
		cy.contains("required")
	})

	it("checks that credentials are invalid", function() {
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
	})

	it("Given logged in successfully we redirect signup and login pages to the home page", function() {
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.location("pathname").should("eq", "/home")

		cy.visit("/")
		cy.location("pathname").should("eq", "/home")

		cy.visit("/signUp")
		cy.location("pathname").should("eq", "/home")
	})
})
