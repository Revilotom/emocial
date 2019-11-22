describe("The Home Page", function() {
	beforeEach(function() {
		cy.visit("/")
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()

		// cy.getCookie("PLAY_SESSION")
	})

	it("checks that can post", function() {
		cy.get("textarea[name=content]").type("This is my first post!!!")
		cy.contains("Submit").click()
		cy.contains("This is my first post!!!")
	})

	it("checks that can see following people's posts", function() {})

	// it("checks that credentials are valid", function() {

	// 	cy.contains("INVALID CREDENTIALS!")
	// })

	// it("Logins in successfully", function() {
	// 	cy.get("input[name=username]").type("revilotom")
	// 	cy.get("input[name=password]").type("12345678")
	// 	cy.contains("Login").click()
	// 	cy.location("pathname").should("eq", "/home")
	// 	cy.getCookie("PLAY_SESSION").should("exist")
	// })
})
