import { gotToSearch, submitSearch, loginAs, writePost, inputSearchTerms } from "../helpers.ts/methods"

describe("The following and followers Pages", function() {
	beforeEach(function() {
		loginAs("123pollyo")
		writePost("😇")
		writePost("😍")
		writePost("👺")
		cy
			.get(
				":nth-child(1) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col-2 > .container > :nth-child(1) > form > .emojis > .btn"
			)
			.click()
		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col-2 > .container > :nth-child(1) > form > .emojis > .btn"
			)
			.click()
		loginAs("revilotom")
		gotToSearch()
		inputSearchTerms("po")
		submitSearch()
		cy.get("form > .btn").click()
		cy.visit("/home")
		cy
			.get(
				":nth-child(3) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col-2 > .container > :nth-child(1) > form > .emojis > .btn"
			)
			.click()
		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(2) > .col-2 > .container > :nth-child(1) > form > .emojis > .btn"
			)
			.click()

		loginAs("123pollyo")
		cy.get(":nth-child(3) > .nav-link").click()
	})

	it("checks that can delete own liked", function() {
		cy
			.get(
				":nth-child(1) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col > form > .btn"
			)
			.click()

		cy.contains("👺").should("not.exist")
	})

	it("checks that can delete own + other liked", function() {
		cy
			.get(
				":nth-child(2) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col > form > .btn"
			)
			.click()

		cy.contains("😍").should("not.exist")
	})

	it("checks that can delete oother liked", function() {
		cy
			.get(
				":nth-child(3) > :nth-child(1) > .justify-content-center > .col-8 > .o-hidden > .card > .card-body > :nth-child(1) > .col > form > .btn"
			)
			.click()

		cy.contains("😇").should("not.exist")
	})
})

// Write about how you planned not to do sucha a complicated project so that you could finish it in time
// Talk about how you wanted to jsut make a simple a product awhere you can use normal letteres
// And then after a proccsss you slowly arrived at the emoji only idea.
// Its good to write on the first slide all of the different features and then show which ines actually made the cut.
// The presentation is justa bout the product its about how you designed, and what decisions you made.
