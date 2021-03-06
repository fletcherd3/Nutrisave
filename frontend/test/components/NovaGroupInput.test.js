import {mount} from "@vue/test-utils"

import Vuetify from 'vuetify'
import NovaGroupInput from "@/components/NovaGroupInput";

let vuetify = new Vuetify();
let wrapper;

beforeEach(() => {
  wrapper = mount(NovaGroupInput, {
    vuetify,
    propsData: {
      value: null
    }
  });
});

afterEach(() => wrapper.destroy());

describe("Nutrition score input test", () => {
  test("Score is emitted to parent component", async () => {
    let inputEl = wrapper.find("input");
    inputEl.trigger("focus");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.space");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.down");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.down");
    await wrapper.vm.$nextTick();
    inputEl.trigger("keydown.enter");
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().input.length).toBe(1);
    expect(wrapper.emitted().input[0][0]).toBe(1);
  });
});