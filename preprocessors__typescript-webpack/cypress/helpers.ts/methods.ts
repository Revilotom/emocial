export const submitPost = () => cy.get(".col-8 > .btn").click()

export const gotToSearch = () => cy.get(":nth-child(2) > .nav-link").click()

export const submitSearch = () => cy.get(".btn").click()

export const marioWritePost = (emoji: string = "🍄") => {
	cy.visit("/")
	cy.get("input[name=username]").type("itsmemario")
	cy.get("input[name=password]").type("12345678")
	cy.contains("Login").click()
	cy.get("textarea[name=content]").type(emoji)
	submitPost()
}

export const kunalWritePost = () => {
	cy.clearCookies()
	cy.visit("/")
	cy.get("input[name=username]").type("kunal")
	cy.get("input[name=password]").type("12345678")
	cy.contains("Login").click()
	cy.get("textarea[name=content]").type("🤡")
	submitPost()
	cy.clearCookies()
}

export const loginAs = (username: string) => {
	cy.clearCookies()
	cy.visit("/")
	cy.get("input[name=username]").type(username)
	cy.get("input[name=password]").type("12345678")
	cy.contains("Login").click()
}

export const writePost = (content: string) => {
	cy.visit("/")
	cy.get("textarea[name=content]").type(content)
	submitPost()
}

export const inputSearchTerms = (sT: string) => {
	cy.get("input[name=searchTerms]").type(sT)
}
