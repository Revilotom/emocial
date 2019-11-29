export const submitPost = () => cy.get(".col-8 > .btn").click()

export const gotToSearch = () => cy.get(":nth-child(2) > .nav-link").click()

export const submitSearch = () => cy.get(".btn").click()

export const marioWritePost = () => {
	cy.visit("/")
	cy.get("input[name=username]").type("itsmemario")
	cy.get("input[name=password]").type("12345678")
	cy.contains("Login").click()
	cy.get("textarea[name=content]").type("🍄")
	submitPost()
}
