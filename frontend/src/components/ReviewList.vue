<template>
    <div class="review-list-wrapper">
        <div class="header-area">
            <BButton
                size="sm"
                variant="warning"
                @click="$store.commit('registerReview')"
            >
                리뷰 작성
            </BButton>
            <BButton
                v-if="!isEditMode"
                :disabled="!reviews.length"
                class="ml-2"
                size="sm"
                variant="info"
                @click="toggleEditMode"
            >
                편집
            </BButton>
            <BButton
                v-if="isEditMode"
                class="ml-2"
                size="sm"
                variant="info"
                @click="toggleEditMode"
            >
                편집 종료
            </BButton>
            <BButton
                v-if="isEditMode"
                class="ml-2"
                size="sm"
                variant="danger"
                @click="deleteCheckedReviews"
            >
                선택 삭제
            </BButton>
        </div>
        <div
            class="review-list-area"
            @scroll="onScroll"
        >
            <BCheckbox
                v-if="isEditMode"
                v-model="isAllSelected"
                class="ml-4"
                @change="checkAllReviews"
                >전체 선택
            </BCheckbox>
            <ul v-if="reviews.length > 0">
                <li
                    v-for="review in reviews"
                    :key="review.id"
                >
                    <div class="review-item">
                        <div
                            v-if="isEditMode"
                            class="checkbox-area"
                        >
                            <BCheckbox
                                v-model="checkedReviewIds"
                                :value="review.id"
                                @change="checkReview"
                            />
                        </div>
                        <div
                            class="image-area"
                            @click="goToReview(review)"
                        >
                            <BImgLazy
                                :alt="review.title"
                                :src="`${imgDirPath}/${review.id}/${review.fileName}`"
                                blank
                                blank-color="grey"
                                class="review-image"
                                rounded
                            />
                        </div>
                        <div
                            class="review-info-area"
                            @click="goToReview(review)"
                        >
                            <div class="review-title">
                                {{ review.title }}
                            </div>
                            <div class="review-address">
                                {{ review.address }}
                            </div>
                            <div class="review-update-date">
                                {{ getReviewUpdateDateStr(review.reviewUpDateStr) }}
                            </div>
                            <BFormRating
                                v-model="review.grade"
                                class="review-rating"
                            />
                        </div>
                    </div>
                </li>
                <li
                    v-if="processingCount > 0"
                    class="progress-list"
                >
                    <ProgressSpinner />
                </li>
            </ul>
            <div
                v-else
                class="no-review-notice"
            >
                등록된 리뷰가 없습니다.
            </div>
        </div>
    </div>
</template>

<script>
import { IMG_DIR_PATH } from '@/common/Config.js';
import { utcDateStrToVisualLocalDateStr } from '@/common/DateUtil.js';
import { process } from '@/common/Api.js';
import { confirm, ok } from '@/common/Dialog.js';
import axios from 'axios';
import ProgressSpinner from '@/components/ProgressSpinner.vue';

export default {
    name: 'ReviewList',
    data() {
        return {
            processingCount: 0,
            imgDirPath: IMG_DIR_PATH,
            isEditMode: false,
            checkedReviewIds: [],
            isAllSelected: false,
            reviewUpdateDate: undefined,
            reviewId: undefined,
        };
    },
    components: {
        ProgressSpinner,
    },
    computed: {
        reviews() {
            return this.$store.state.reviews;
        },
        isEndOfList() {
            return this.$store.state.isEndOfList;
        },
    },
    created() {
        this.getReviews();
    },
    methods: {
        async onScroll(e) {
            if (this.isEndOfList) return;
            let { scrollTop, clientHeight, scrollHeight } = e.target;
            if (scrollTop + clientHeight >= scrollHeight) {
                await this.getReviews();
            }
        },
        async getReviews() {
            const params = {
                that: this,
                reviewUpdateDate: this.reviewUpdateDate,
                reviewId: this.reviewId,
            };
            await this.$store.dispatch('setReviewsByKeySet', params);
            if (this.reviews.length > 0) {
                console.log('# reviews', this.reviews);
                const lastReview = this.reviews[this.reviews.length - 1];
                console.log('# lastReview', lastReview);
                this.reviewUpdateDate = lastReview.reviewUpDateStr;
                this.reviewId = lastReview.id;
            }
        },
        checkAllReviews() {
            this.checkedReviewIds = [];
            if (this.isAllSelected) this.checkedReviewIds = this.reviews.map((re) => re.id);
        },
        checkReview() {
            this.isAllSelected = false;
        },
        deleteCheckedReviews() {
            process(this, async () => {
                const isConfirmed = await confirm(this, '선택한 리뷰를 삭제하시겠습니까?');
                if (!isConfirmed) return;

                await axios.delete('/api/review/deleteReviews', {
                    data: {
                        reviewIds: this.checkedReviewIds,
                    },
                });

                await ok(this, '삭제되었습니다.');
                this.toggleEditMode();
                await this.$store.dispatch('setReviewsByKeySet', { that: this });
            });
        },
        toggleEditMode() {
            this.isAllSelected = false;
            this.checkedReviewIds = [];
            this.isEditMode = !this.isEditMode;
        },
        getReviewUpdateDateStr(reviewUpDateStr) {
            return utcDateStrToVisualLocalDateStr(reviewUpDateStr);
        },
        goToReview(review) {
            this.$store.commit('setReview', review);
            this.$store.commit('setIsVisibleReviewList', false);
            this.$store.dispatch('setFileList', this);
        },
    },
};
</script>

<style lang="scss" scoped>
.review-list-wrapper {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    padding: 10px;
    display: flex;
    flex-direction: column;

    > .header-area {
        padding: 10px;
        flex-shrink: 0;
    }

    > .review-list-area {
        overflow-y: auto;
        flex: 1;

        > ul {
            list-style: none;
            padding: 10px;
            margin: 0;

            > li {
                padding: 10px;
                background-color: rgba(0, 0, 0, 0.4);
                border-radius: 5px;
                margin-bottom: 10px;

                &:hover {
                    cursor: pointer;
                    background-color: rgba(0, 0, 0, 0.5);
                }

                > .review-item {
                    display: flex;

                    > .checkbox-area {
                        margin: 10px;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }

                    > .image-area {
                        margin-right: 20px;

                        > .review-image {
                            width: 120px;
                            height: 100px;
                            object-fit: cover;
                        }
                    }

                    > .review-info-area {
                        flex: 1;
                        display: flex;
                        flex-direction: column;

                        > .review-title {
                            display: flex;
                            font-size: 25px;
                            flex: 1;
                        }

                        > .review-address {
                            display: flex;
                            align-items: center;
                            font-size: 12px;
                            flex: 1;
                            padding-bottom: 5px;
                        }

                        > .review-update-date {
                            display: flex;
                            align-items: center;
                            font-size: 11px;
                            flex: 1;
                            opacity: 0.5;
                            padding-bottom: 5px;
                        }

                        > .review-rating {
                            flex: 1;
                            width: 100px;
                            font-size: 15px;
                            background-color: transparent;
                            border: none;
                            padding: 0;
                            margin: 0;
                            color: #ffdd00;
                            height: unset;
                        }
                    }
                }
            }

            > .progress-list {
                position: relative;
                padding: 50px 0;
            }
        }

        > .no-review-notice {
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 25px;
        }
    }
}
</style>
