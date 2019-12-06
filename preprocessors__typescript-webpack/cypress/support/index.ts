// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import "./commands"

// Alternatively you can use CommonJS syntax:
// require('./commands')

// beforeEach(function() {
// 	cy.exec("./clearDB.sh")
// })
// Cypress.Cookies.debug(true)

beforeEach(function() {
	cy.log("I run before every test in every spec file!!!!!!")
	cy.exec("./clearDB.sh")

	cy.request("POST", "/signUp", {
		name: "mariokid",
		username: "itsmemario",
		password1: "12345678",
		password2: "12345678"
	})

	cy.clearCookies()

	cy.request("POST", "/signUp", {
		name: "hello",
		username: "revilotom",
		password1: "12345678",
		password2: "12345678"
	})

	cy.clearCookies()

	cy.request("POST", "/signUp", {
		name: "hello",
		username: "123pollyo",
		password1: "12345678",
		password2: "12345678"
	})

	cy.clearCookies()

	cy.request("POST", "/signUp", {
		name: "hello",
		username: "kunal",
		password1: "12345678",
		password2: "12345678"
	})

	cy.clearCookies()

	cy.request("POST", "/signUp", {
		name: "hello",
		username: "ajinkya69",
		password1: "12345678",
		password2: "12345678"
	})
})
