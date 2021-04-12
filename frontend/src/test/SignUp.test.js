import {mount} from "@vue/test-utils";
import {globalStateMocks} from "./testHelper";
import {ApiRequestError} from "../ApiRequestError";
import SignUp from "../components/SignUp";

jest.useFakeTimers();

const getData = () => {
  return {
    firstName: "FN",
    middleName: "MN",
    lastName: "LN",
    nickName: "NN",

    email: "example@example.com",

    password: "Passw0rd",
    confirmPassword: "Passw0rd",

    dateOfBirth: "2000-01-01",

    addressAsString: "10 Downing Street, Covent Garden, SW123, London, Greater London, United Kingdom",

    countryCode: 64,
    phoneNumber: "220230345",

    bio: "Every day is Mac Pro day",
  }
}

/**
 * Mounts sign up with data, overwriting results of `getData` with provided properties from data
 * @param {*} data data object any properties to override
 * @param {object} otherOptions other options to instantiate the component with e.g. mock
 * @returns
 */
const mountWithData = (data, otherOptions = undefined) => {
  if (otherOptions === undefined) {
    otherOptions = {};
  }

  return mount(SignUp, {
    ...otherOptions,
    data: () => ({
      ...getData(),
      ...data
    }),
    mocks: {
      ...globalStateMocks()
    }
  });
}

let wrapper;

window.HTMLElement.prototype.scrollIntoView = jest.fn();

afterEach(() => wrapper.destroy());

describe("Date of birth", () => {
  const now = new Date(2020, 1, 22); // 2020-02-22
  test("Too young by years", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth("2010-03-05", now)).toBeTruthy();
  });
  test("Too young by months", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth("2007-03-05", now)).toBeTruthy();
  });
  test("Too young by days", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth("2007-02-24", now)).toBeTruthy();
  });
  test("13th birthday", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth("2007-02-22", now)).toBeFalsy();
  });
  test("same year old enough", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth("2007-01-22", now)).toBeFalsy();
  });
  test("bad format", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth("blaaa", now)).toBeTruthy();
  });
  test("bad format", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth(undefined, now)).toBeTruthy();
  });
  test("bad format", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth("20202-53-52", now)).toBeTruthy();
  });
  test("bad date", () => {
    wrapper = mount(SignUp, {});
    expect(wrapper.vm.validateDateOfBirth("2001-02-29", now)).toBeTruthy();
  });
});

describe("Sign up error handling", () => {
  test("passwords different", async () => {
    wrapper = mountWithData({
      confirmPassword: "notpassword"
    });
    wrapper.vm.callApi = jest.fn();
    await wrapper.vm.register();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.confirmPasswordErrorMessage).toBeTruthy();
  });

  test("email used", async () => {
    wrapper = mountWithData({
      email: "notregistered@example.com"
    }, {});
    wrapper.vm.callApi = jest.fn(() => {
      const error = new ApiRequestError("Some error message");
      error.status = 409;
      return Promise.reject(error);
    });
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    // only way to find out if email is used is by calling the API
    expect(wrapper.vm.callApi.mock.calls.length).toBe(1);
    expect(wrapper.vm.emailErrorMessage).toBeTruthy();
  });

  test("country code but not phone", async () => {
    wrapper = mountWithData({
      phoneNumber: ""
    });
    wrapper.vm.callApi = jest.fn();
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.phoneErrorMessage).toBeTruthy();
  });

  test("phone but not country code", async () => {
    wrapper = mountWithData({
      countryCode: null
    });
    wrapper.vm.callApi = jest.fn();
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.countryCodeErrorMessage).toBeTruthy();
    expect(wrapper.vm.phoneErrorMessage).toBeFalsy();
  });

  test("Errors being cleared before submit", async () => {
    wrapper = mountWithData({
      errorMessage: "Some message",
      emailErrorMessage: "Some message",
      confirmPasswordErrorMessage: "Some message",
      dateOfBirthErrorMessage: "Some message",
      phoneErrorMessage: "Some message",
      countryCodeErrorMessage: "Some message",
    });

    wrapper.vm.callApi = jest.fn(() => Promise.resolve({
      data: {
        userId: 0
      }
    }));

    await wrapper.vm.register();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(1);

    expect(wrapper.vm.errorMessage).toBeFalsy();
    expect(wrapper.vm.phoneErrorMessage).toBeFalsy();
    //Ensures all the error messages are cleared
    expect(wrapper.vm.countryCodeErrorMessage).toBeFalsy();
    expect(wrapper.vm.confirmPasswordErrorMessage).toBeFalsy();
    expect(wrapper.vm.emailErrorMessage).toBeFalsy();
    expect(wrapper.vm.dateOfBirthErrorMessage).toBeFalsy();
  });

  test("Errors being cleared after failure", async () => {
    wrapper = mountWithData({
      errorMessage: "Some message",
      emailErrorMessage: "Some message",
      confirmPasswordErrorMessage: "Some message",
      dateOfBirthErrorMessage: "Some message",
      phoneErrorMessage: "Some message",
      countryCodeErrorMessage: "Some message",
      confirmPassword: "notPassword"
    });

    await wrapper.vm.register();

    expect(wrapper.vm.errorMessage).toBeTruthy();
    expect(wrapper.vm.phoneErrorMessage).toBeFalsy();
    //Ensures all the error messages are cleared
    expect(wrapper.vm.countryCodeErrorMessage).toBeFalsy();
    expect(wrapper.vm.confirmPasswordErrorMessage).toBeTruthy();
    expect(wrapper.vm.emailErrorMessage).toBeFalsy();
    expect(wrapper.vm.dateOfBirthErrorMessage).toBeFalsy();
  });
});
