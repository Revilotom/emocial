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
		cy.get("input[name=username]").type("kunal54")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.get("textarea[name=content]").type("Im kunal!!")
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
		cy.visit("/home")
	})

	it("checks that can post", function() {
		cy.get("textarea[name=content]").type("This is my first post!!!")
		cy.contains("Submit").click()
		cy.contains("This is my first post!!!")
	})

	it("checks that can see following people's posts", function() {
		cy.contains("Mario is posting")
	})

	it("checks that cannot see posts from people you're not following", function() {
		cy.contains("kunal").should("not.exist")
	})
})
