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
})
