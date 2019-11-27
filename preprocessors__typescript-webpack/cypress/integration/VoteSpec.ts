describe("Voting", function() {
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

	it("checks that there are up and down buttons", function() {
		cy.get(":nth-child(1) > form > .btn").should("exist")
		cy.get(":nth-child(3) > form > .btn").should("exist")
		cy.contains("0")
	})

	it("checks that you can like", function() {
		cy.get(":nth-child(1) > form > .btn").click()
		cy.get(":nth-child(1) > form > .btn").should("have.class", "btn-primary")
		cy.get(":nth-child(3) > form > .btn").should("have.class", "btn-secondary")
		cy.contains("1")
	})

	it("checks that you can undo a like", function() {
		cy.get(":nth-child(1) > form > .btn").click()
		cy.get(":nth-child(1) > form > .btn").click()
		cy.get(":nth-child(1) > form > .btn").should("have.class", "btn-secondary")
		cy.get(":nth-child(3) > form > .btn").should("have.class", "btn-secondary")
		cy.contains("0")
	})

	it("checks that you can dislike", function() {
		cy.get(":nth-child(3) > form > .btn").click()
		cy.get(":nth-child(1) > form > .btn").should("have.class", "btn-secondary")
		cy.get(":nth-child(3) > form > .btn").should("have.class", "btn-danger")
		cy.contains("-1")
	})

	it("checks that you can undo a dislike", function() {
		cy.get(":nth-child(3) > form > .btn").click()
		cy.get(":nth-child(3) > form > .btn").click()

		cy.get(":nth-child(1) > form > .btn").should("have.class", "btn-secondary")
		cy.get(":nth-child(3) > form > .btn").should("have.class", "btn-secondary")
		cy.contains("0")
	})

	it("checks that you can like and then dislike", function() {
		cy.get(":nth-child(1) > form > .btn").click()
		cy.get(":nth-child(3) > form > .btn").click()
		cy.get(":nth-child(1) > form > .btn").should("have.class", "btn-secondary")
		cy.get(":nth-child(3) > form > .btn").should("have.class", "btn-danger")
		cy.contains("-1")
	})

	it("checks that you can dislike and then like", function() {
		cy.get(":nth-child(3) > form > .btn").click()
		cy.get(":nth-child(1) > form > .btn").click()
		cy.get(":nth-child(1) > form > .btn").should("have.class", "btn-primary")
		cy.get(":nth-child(3) > form > .btn").should("have.class", "btn-secondary")
		cy.contains("1")
	})
})
