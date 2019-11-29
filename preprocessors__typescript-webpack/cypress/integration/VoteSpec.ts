import { marioWritePost } from "../helpers.ts/methods"

describe("Voting", function() {
	beforeEach(function() {
		marioWritePost()
	})

	it("checks that there are up and down buttons", function() {
		cy.get(":nth-child(1) > form > .emojis > .btn").should("exist")
		cy.get(":nth-child(3) > form > .emojis > .btn").should("exist")
		cy.contains("0")
	})

	it("checks that you can like", function() {
		cy.get(":nth-child(1) > form > .emojis > .btn").click()
		cy.get(":nth-child(1) > form > .emojis > .btn").should("have.class", "btn-success")
		cy.get(":nth-child(3) > form > .emojis > .btn").should("have.class", "btn-light")
		cy.contains("1")
	})

	it("checks that you can undo a like", function() {
		cy.get(":nth-child(1) > form > .emojis > .btn").click()
		cy.get(":nth-child(1) > form > .emojis > .btn").click()
		cy.get(":nth-child(1) > form > .emojis > .btn").should("have.class", "btn-light")
		cy.get(":nth-child(3) > form > .emojis > .btn").should("have.class", "btn-light")
		cy.contains("0")
	})

	it("checks that you can dislike", function() {
		cy.get(":nth-child(3) > form > .emojis > .btn").click()
		cy.get(":nth-child(1) > form > .emojis > .btn").should("have.class", "btn-light")
		cy.get(":nth-child(3) > form > .emojis > .btn").should("have.class", "btn-danger")
		cy.contains("-1")
	})

	it("checks that you can undo a dislike", function() {
		cy.get(":nth-child(3) > form > .emojis > .btn").click()
		cy.get(":nth-child(3) > form > .emojis > .btn").click()

		cy.get(":nth-child(1) > form > .emojis > .btn").should("have.class", "btn-light")
		cy.get(":nth-child(3) > form > .emojis > .btn").should("have.class", "btn-light")
		cy.contains("0")
	})

	it("checks that you can like and then dislike", function() {
		cy.get(":nth-child(1) > form > .emojis > .btn").click()
		cy.get(":nth-child(3) > form > .emojis > .btn").click()
		cy.get(":nth-child(1) > form > .emojis > .btn").should("have.class", "btn-light")
		cy.get(":nth-child(3) > form > .emojis > .btn").should("have.class", "btn-danger")
		cy.contains("-1")
	})

	it("checks that you can dislike and then like", function() {
		cy.get(":nth-child(3) > form > .emojis > .btn").click()
		cy.get(":nth-child(1) > form > .emojis > .btn").click()
		cy.get(":nth-child(1) > form > .emojis > .btn").should("have.class", "btn-success")
		cy.get(":nth-child(3) > form > .emojis > .btn").should("have.class", "btn-light")
		cy.contains("1")
	})
})
