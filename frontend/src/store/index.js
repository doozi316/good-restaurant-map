import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        curLon: undefined,
        curLat: undefined,
        reviews: undefined
    },
    mutations: {
        setReviews: (state, reviews) => {
            state.reviews = reviews
        },
        setLonLat: (state, { lon, lat }) => {
            state.curLon = lon;
            state.curLat = lat;
        }
    },
    actions: {},
    modules: {}
})
