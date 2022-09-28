<template>
    <div class="side-bar-wrapper">
        <VueResizable
            v-if="isVisibleSideBar"
            :active="['r']"
            :max-width="Infinity"
            :min-width="500"
            :width="500"
            class="resizable-side-bar"
        >
            <ReviewList v-if="isVisibleReviewList" />
            <ReviewForm v-else />
        </VueResizable>
        <BButton
            class="side-bar-active-btn"
            size="sm"
            @click="showSideBar"
        >
            <FontAwesomeIcon :icon="isVisibleSideBar ? 'angle-left' : 'angle-right'" />
        </BButton>
    </div>
</template>

<script>
import VueResizable from 'vue-resizable';
import ReviewForm from '@/components/ReviewForm.vue';
import ReviewList from '@/components/ReviewList.vue';
import { mapState } from 'vuex';

export default {
    name: 'SideBar',
    components: {
        ReviewForm,
        ReviewList,
        VueResizable,
    },
    data() {
        return {
            processingCount: 0,
            isVisibleSideBar: true,
        };
    },
    computed: {
        ...mapState({
            curReviewId: (state) => state.curReviewId,
            isDisabledInput: (state) => state.isDisabledInput,
            isVisibleReviewList: (state) => state.isVisibleReviewList,
        }),
    },
    methods: {
        showSideBar() {
            this.isVisibleSideBar = !this.isVisibleSideBar;
        },
    },
};
</script>

<style lang="scss" scoped>
.side-bar-wrapper {
    display: flex;
    color: #fff;

    > .side-bar-active-btn {
        flex-shrink: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #000000;
        padding: 0;
        border: none;
        border-radius: unset;
        color: #fff;
        opacity: 0.5;
        width: 40px;
        height: 40px;
    }
}
</style>
