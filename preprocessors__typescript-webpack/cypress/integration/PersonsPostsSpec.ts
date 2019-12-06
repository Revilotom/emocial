import { marioWritePost, gotToSearch, submitSearch, kunalWritePost } from "../helpers.ts/methods"

describe("The Persons posts", function() {
	beforeEach(function() {
		marioWritePost()
		cy.get(":nth-child(1) > form > .emojis > .btn").click()
		cy.clearCookies()
		marioWritePost("💀")
		cy.clearCookies()
		cy.visit("/")
		cy.get("input[name=username]").type("revilotom")
		cy.get("input[name=password]").type("12345678")
		cy.contains("Login").click()
		gotToSearch()
		cy.get("input[name=searchTerms]").type("mar")
		submitSearch()
		cy.visit("/posts/itsmemario")
	})

	it("checks that changing order does not redirect to home", function() {
		cy.get(".col > form > .btn").click()
		cy.location("pathname").should("eq", "/posts/itsmemario")
	})

	it("checks that you can change the order", function() {
		cy
			.get(
				":nth-child(1) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col > .card-text"
			)
			.contains("💀")

		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col > .card-text"
			)
			.contains("🍄")

		cy.get(".col > form > .btn").click()

		cy
			.get(
				":nth-child(1) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col > .card-text"
			)
			.contains("🍄")

		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col > .card-text"
			)
			.contains("💀")

		cy.get(".col-10 > form > .btn").click()

		cy
			.get(
				":nth-child(1) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col > .card-text"
			)
			.contains("💀")

		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col > .card-text"
			)
			.contains("🍄")
	})
})
