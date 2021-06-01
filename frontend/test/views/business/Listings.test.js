import BusinessListings from "@/views/business/Listings";
import { mount } from "@vue/test-utils";

jest.mock("@/Api");
import { Api } from "@/Api";
import { globalStateMocks } from "#/testHelper";
import SortedPaginatedItemList from "@/components/SortedPaginatedItemList";
import {ApiRequestError} from "@/ApiRequestError";

let wrapper;
beforeEach(() => {
  wrapper = mount(BusinessListings, {
    mocks: globalStateMocks(),
    stubs: ["error-modal", "router-link"], // Add the name of the business listings item component to here
    propsData: {
      businessId: 1
    }
  });
});

afterEach(() => wrapper.destroy());

describe("API handling", () => {
  test("Items actually get set", async () => {
    const listings = [{id: 20, inventoryItem: {product: { images: []}}}, {id: 40, inventoryItem: {product: { images: []}}}]
    Api.getBusinessListings.mockResolvedValue({
      data: listings
    });
    await wrapper.vm.getListingsPipeline();
    expect(wrapper.findComponent(SortedPaginatedItemList).vm.$props.items).toEqual(listings);
  });


  test("API returns error", async () => {
    const message = "It's a Mario!";
    Api.getBusinessListings.mockImplementation(() => Promise.reject(new ApiRequestError(message)));
    await wrapper.vm.getListingsPipeline();
    expect(wrapper.vm.apiErrorMessage).toEqual(message);
  });
});