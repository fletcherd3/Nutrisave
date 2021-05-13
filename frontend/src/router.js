import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "./components/Home.vue"
import Landing from "./components/Landing.vue"
import {store} from "./store"

export default [];

Vue.use(VueRouter);

export const router = new VueRouter({
  hashbang: false,
  mode: "history",
  base: process.env.VUE_APP_BASE_URL,
  routes: [
    {
      name: "landing",
      path: "/",
      component: Landing
    },
    {
      name: "home",
      path: "/home",
      component: Home,
      beforeEnter: (to, from, next) => {
        if (store.getters.isLoggedIn()) {
          // This is magic! It somehow uses the same instance of store that is used
          // by the rest of the
          next()
        } else {
          next({name: 'landing'});
        }
      },
      meta: {title: "Home | Wasteless"}
    },
    {
      name: "login",
      path: "/login",
      component: () => import("./components/Login.vue"),
      meta: {title: "Login | Wasteless"}
    },
    {
      name: "signUp",
      path: "/signUp",
      component: () => import("./components/SignUp.vue"),
      meta: {title: "Sign Up | Wasteless"}
    },
    {
      name: "profile",
      path: "/profile/:userId(\\d+)?",
      component: () => import("./components/Profile.vue"),
      props: route => {
        let userId = route.params.userId ? parseInt(route.params.userId, 10)
            : NaN;
        // Using \d so parseInt should never fail
        if (isNaN(userId)
            && store.getters.isLoggedIn()) {
          userId = store.getters.getAuthUser().id;
        }
        return {userId};
      }
    },
    {
      name: "search",
      path: "/searchresults/:query(.*)",
      component: () => import('./components/SearchResults.vue'),
      meta: {title: route => `'${route.params.query}' | Search`},
      props: route => ({search: route.params.query}),
    },
    {
      name: "registerBusiness",
      path: "/registerbusiness",
      meta: {title: "Create Business | Wasteless"},
      component: () => import("./components/RegisterBusiness.vue"),
    },
    {
      name: "createProduct",
      path: "/business/:businessId(\\d+)/createproduct",
      component: () => import("./components/CreateProduct.vue"),
      props: route => ({businessId: parseInt(route.params.businessId, 10)})
    },
    {
      name: "productCatalogue",
      path: "/business/:businessId(\\d+)/catalog",
      component: () => import("./components/ProductCatalogue.vue"),
      props: route => ({businessId: parseInt(route.params.businessId, 10)})
    },
    {
      name: "productDetail",
      path: "/business/:businessId(\\d+)/product/:productId(\\d+)",
      component: () => import("./components/ProductDetail.vue"),
      props: route => ({
        productId: parseInt(route.params.productId, 10),
        businessId: parseInt(route.params.businessId, 10)
      })
    },
    {
      name: "editProductImages",
      path: "/business/:businessId(\\d+)/products/:productId(\\d+)/images",
      component: () => import("./components/EditProductImages.vue"),
      props: route => ({
        productId: parseInt(route.params.productId, 10),
        businessId: parseInt(route.params.businessId, 10)
      })
    },
    {
      name: "businessProfile",
      path: "/business/:businessId(\\d+)",
      meta: {title: "Business Profile | Wasteless"},
      component: () => import("./components/BusinessProfile.vue"),
      props: route => {
        // If business ID is optional, user can switch back to acting as user in navbar, causing page to fail
        let businessId = parseInt(route.params.businessId, 10);
        const showBackButton = route.params.showBackButton; // Optional
        return {businessId, showBackButton};
      }
    },
    {
      name: "businessListings",
      path: "/business/:businessId(\\d+)/listings",
      meta: { title: "Business Listings TODO | Wasteless"},
      component: () => import("./views/BusinessListings.vue"),
      props: route => ({ businessId: parseInt(route.params.businessId, 10)})
    },
    {
      name: "error",
      path: "/error",
      meta: {title: "Error 😢 | Wasteless"},
      component: () => import("./components/Errors/Error.vue")
    },
    {
      name: "error401",
      path: "/error401",
      meta: {title: "401 Not Authorized | Wasteless"},
      component: () => import("./components/Errors/Error401.vue")
    },
    {
      name: "error404",
      path: "/*",
      meta: {title: "Not Found 😢 | Wasteless"},
      component: () => import("./components/Errors/Error404.vue"),
      props: route => ({path: route.fullPath})
    }
  ],
});


/**
 * Sets page title on navigate
 * Uses `route.meta.title`; either a string or function that, given the route, returns a string
 * See https://github.com/vuejs/vue-router/issues/914#issuecomment-384477609
 * To set title after navigate, use the `setDocumentTitle` prop passed to the router component in `App.vue`
 */
router.afterEach((to) => {
  
  // Navbar is static now so when the user clicks a link, close the navbar so it doesn't cover the new content
  if (document) {
    const hamburger = document.getElementById("hamburger-button");
    if (hamburger && !hamburger.classList.contains("collapsed")) {
      hamburger.click();
    }
  }
  Vue.nextTick(() => {
    let title = "Wasteless";
    if (typeof to.meta.title == "string") {
      title = to.meta.title;
    } else if (typeof to.meta.title == "function") {
      title = to.meta.title(to);
    }
    document.title = title;
  });
})
