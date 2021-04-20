/**
 * If there's an API call before mount or something and so you can't use `wrapper.vm.methodName` to override the method
 * (overriding of method occurs after the before mount hook), use this to mock the Api.js methods.
 * Use `jest.mock("./path/to/the/real/Api.js")` to mock, then use `const { Api } = require("./path/to/the/real/Api.js")
 * and ``Api._setWhatever()` to set the response to the required response
 */

const responses = {
  countryDataOrFallback: () => Promise.resolve([])

}

export const Api = {
  countryDataOrFallback: () => responses.countryDataOrFallback(),
  _setCountryDataOrFallback: val => responses.countryDataOrFallback = val
}