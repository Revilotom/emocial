describe("The Home Page", function() {
	beforeEach(function() {
		cy.visit("/")
		cy.get("input[name=username]").type("itsmemario")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.get("textarea[name=content]").type("🍄")
		cy.contains("Submit").click()
		cy.clearCookies()

		cy.visit("/")
		cy.get("input[name=username]").type("kunal54")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		cy.get("textarea[name=content]").type("💩")
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
		cy.get("textarea[name=content]").type("🎸")
		cy.contains("Submit").click()
		cy.contains("🎸")
		cy.contains("content [The content of all posts must consist exclusively of emojis!!]").should("not.exist")
	})

	it("checks that cannot post normal characters", function() {
		cy.get("textarea[name=content]").type("poopyboy")
		cy.contains("Submit").click()
		cy.contains("poopyboy")
		cy.contains("content [The content of all posts must consist exclusively of emojis!!]")
	})

	it("checks that can see following people's posts", function() {
		cy.contains("🍄")
	})

	it("checks that cannot see posts from people you're not following", function() {
		cy.contains("💩").should("not.exist")
	})

	it("checks that the emoji picker can be used", function() {
		cy.get("#emoji-button").click()
		cy.get(".active > .emoji-picker__emojis > :nth-child(88)").click()
		cy.contains("Submit").click()
		cy.contains("👺")
	})
})
