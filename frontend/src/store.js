import Vue from 'vue';

// This is the state object. It is reactive and can not be accessed directly by components.
// Use getters and actions to modify this object.
const state = new Vue.observable({
  authUser: JSON.parse(localStorage.getItem("authUser")),
  welcomeMessage: "HERE WE GOOO",
  actingAs: JSON.parse(localStorage.getItem("actingAs"))
});

// This is the globally accessible store. It allows the state to be read and modified.
// Currently, vue has been set up to give access to this object using this.$stateStore within components.
export const store = {
  getters: {
    // Get the authenticated user
    getAuthUser: () => {
      return state.authUser;
    },
    // Get the current welcome message (example)
    getWelcomeMessage: () => {
      return state.welcomeMessage;
    },

    getActingAs:() => {
      return state.actingAs;
    },

    /**
     * Checks if a user is logged in or not
     */
    isLoggedIn() {
      return state.authUser !== null && state.authUser !== undefined;
    },

    /**
     * True if admin, false if not OR IF NOT LOGGED IN
     */
    isAdmin() {
      return state.authUser && state.authUser.role === "ROLE_ADMIN";
    },

    isActingAsBusiness() {
      return state.actingAs !== null;
    },


  },
  actions: {
    // Set the current welcome message (example)
    setWelcomeMessage: (message) => {
      state.welcomeMessage = message;
    },
    // Set the currently logged in user and sync with persistent storage
    setAuthUser: (authUser) => {
      state.authUser = authUser;
      localStorage.setItem("authUser", JSON.stringify(authUser));
      localStorage.setItem("userId", state.authUser.id);
    },
    // log the user out and sync with local storage
    deleteAuthUser: () => {
      state.authUser = null;
      localStorage.removeItem("authUser");
      localStorage.removeItem("actingAs");
      localStorage.removeItem("userId");
    },
    // Make the currently logged in user admin
    makeAdmin: () => {
      state.authUser.role = "ROLE_ADMIN";
      localStorage.setItem("authUser", JSON.stringify(state.authUser));
    },
    // Remove admin status from currently logged in user
    revokeAdmin: () => {
      state.authUser.role = "ROLE_USER";
      localStorage.setItem("authUser", JSON.stringify(state.authUser));
    },

    setActingAs: (business) => {
      state.actingAs = business;
      localStorage.setItem("actingAs", JSON.stringify(state.actingAs));
    },

    deleteActingAs: () => {
      state.actingAs = null;
      localStorage.setItem("actingAs", JSON.stringify(state.actingAs));
    }
  },
};
