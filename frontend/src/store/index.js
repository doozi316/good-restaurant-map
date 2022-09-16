import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios';
import { process } from '@/common/Api.js';

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        reviews: [],
        curLon: undefined,
        curLat: undefined,
        curReviewId: undefined,
        curAddress: undefined,
        curTitle: undefined,
        curGrade: undefined,
        curReview: undefined,
        isDisabledInput: true,
        curFileList: [],
        isVisibleReviewList: true
    },
    mutations: {
        setIsDisabledInput: (state, bool) => {
            setIsDisabledInput(state, bool);
        },
        setCurReviewId: (state, id) => {
            state.curReviewId = id;
            setIsVisibleReviewList(state, false);
        },
        setCurTitle: (state, title) => {
            state.curTitle = title;
        },
        setCurAddress: (state, address) => {
            state.curAddress = address;
        },
        setCurGrade: (state, grade) => {
            state.curGrade = grade;
        },
        setCurReview: (state, review) => {
            state.curReview = review;
        },
        setReviews: (state, reviews) => {
            if (state.reviews &&
                reviews &&
                state.reviews.length !== reviews.length) {
                const ids = state.reviews.map(re => re.id);
                const curReview = reviews.find(review => !ids.includes(review.id));
                if (curReview)
                    state.curReviewId = curReview.id;
            }
            state.reviews = reviews;

            const review = reviews.find(review =>
                review.id === state.curReviewId
            );

            setReview(state, review);
        },
        setReview: (state, review) => {
            setReview(state, review);
        },
        setCurFileList: (state, images) => {
            state.curFileList = images;
        },
        setLonLat: (state, { lon, lat }) => {
            state.curLon = lon;
            state.curLat = lat;
        },
        setIsVisibleReviewList: (state, bool) => {
            setIsVisibleReviewList(state, bool);
        },
        registerReview(state) {
            setIsVisibleReviewList(state, false);
            setIsDisabledInput(state, false);
            setReview(state);
        }
    },
    actions: {
        async setReviews({commit}, that) {
            await process(that, async () => {
                const result = await axios.get('/api/review/getReviews');
                await commit('setReviews', result.data);
            })
        },
        async setFileList({commit, state}, that) {
            await process(that, async () => {
                const result = await axios.get('/api/file/getImages', {
                    params: { reviewId: state.curReviewId }
                });
                await commit('setCurFileList', result.data);
            })
        }
    },
    modules: {}
})

function setReview(state, review) {
    state.curReviewId = review ? review.id : review;
    state.curLat = review ? review.lat : review;
    state.curLon = review ? review.lon : review;
    state.curTitle = review ? review.title : review;
    state.curGrade = review ? review.grade : review;
    state.curAddress = review ? review.address : review;
    state.curReview = review ? review.review : review;
}

function setIsVisibleReviewList(state, bool) {
    state.isVisibleReviewList = bool;
}

function setIsDisabledInput(state, bool) {
    state.isDisabledInput = bool;
}