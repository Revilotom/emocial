import { submitPost, gotToSearch, submitSearch, marioWritePost, kunalWritePost } from "../helpers.ts/methods"

describe("The Home Page", function() {
	beforeEach(function() {
		marioWritePost()
		cy.clearCookies()

		kunalWritePost()

		cy.visit("/")
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		gotToSearch()
		cy.get("input[name=searchTerms]").type("mar")
		submitSearch()
		cy.get("form > .btn").click()
		cy.visit("/home")
	})

	it("checks that can post", function() {
		cy.get("textarea[name=content]").type("🎸")
		submitPost()
		cy.contains("🎸")
		cy.contains("content [The content of all posts must consist exclusively of emojis!!]").should("not.exist")
	})

	it("checks that cannot post normal characters", function() {
		cy.get("textarea[name=content]").type("poopyboy")
		submitPost()
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
		submitPost()
		cy.contains("👺")
	})

	it("checks that you can change the order", function() {
		cy.visit("/search")
		cy.get("input[name=searchTerms]").type("kun").click()
		submitSearch()
		cy.get("form > .btn").click()
		cy.visit("/")

		cy
			.get(
				":nth-child(1) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col-10 > .container > .row > .card-title"
			)
			.should("contain.text", "kunal")

		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col-10 > .container > .row > .card-title"
			)
			.should("contain.text", "mario")

		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col-2 > .container > :nth-child(1) > form > .emojis > .btn"
			)
			.click()

		cy.get(".col > form > .btn > input").click()

		cy
			.get(
				":nth-child(1) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col-10 > .container > .row > .card-title"
			)
			.should("contain.text", "mario")

		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col-10 > .container > .row > .card-title"
			)
			.should("contain.text", "kunal")

		cy.get(".col-10 > form > .btn > input").click()

		cy
			.get(
				":nth-child(1) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col-10 > .container > .row > .card-title"
			)
			.should("contain.text", "kunal")

		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col-10 > .container > .row > .card-title"
			)
			.should("contain.text", "mario")
	})
})
