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
                class="ml-2"
                size="sm"
                variant="info"
                @click="toggleEditMode"
                v-if="!isEditMode"
            >
                편집
            </BButton>
            <BButton
                class="ml-2"
                size="sm"
                variant="info"
                @click="toggleEditMode"
                v-if="isEditMode"
            >
                편집 종료
            </BButton>
            <BButton
                class="ml-2"
                size="sm"
                variant="danger"
                @click="deleteCheckedReviews"
                v-if="isEditMode"
            >
                선택 삭제
            </BButton>
        </div>
        <div class="review-list-area">
            <BCheckbox
                class="ml-4"
                v-if="isEditMode"
                @change="checkAllReviews"
                v-model="isAllSelected"
            >전체 선택</BCheckbox>
            <ul>
                <li
                    v-for="review in reviews"
                    :key="review.id"
                >
                    <div class="review-item">
                        <div class="checkbox-area">
                            <BCheckbox
                                v-if="isEditMode"
                                v-model="checkedReviewIds"
                                :value="review.id"
                                @change="checkReview"
                            />
                        </div>
                        <div class="image-area" @click="goToReview(review)">
                            <BImgLazy
                                class="review-image"
                                :src="`${imgDirPath}/${review.id}/${review.fileName}`"
                                :alt="review.title"
                                rounded
                                blank
                                blank-color="grey"
                            />
                        </div>
                        <div class="review-info-area" @click="goToReview(review)">
                            <div class="review-title">
                                {{review.title}}
                            </div>
                            <div class="review-address">
                                {{review.address}}
                            </div>
                            <div class="review-update-date">
                                {{getReviewUpdateDateStr(review.reviewUpDateStr)}}
                            </div>
                            <BFormRating
                                class="review-rating"
                                v-model="review.grade"
                            />
                        </div>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import { IMG_DIR_PATH } from '@/common/Config.js'
import { utcDateStrToVisualLocalDateStr } from '@/common/DateUtil.js'
import { process } from '@/common/Api.js'
import { confirm, ok } from '@/common/Dialog.js';
import axios from "axios";

export default {
    name: 'ReviewList',
    data() {
        return {
            imgDirPath: IMG_DIR_PATH,
            isEditMode: false,
            checkedReviewIds: [],
            isAllSelected: false,
        }
    },
    computed: {
        reviews() {
            return this.$store.state.reviews;
        }
    },
    methods: {
        checkAllReviews() {
            this.checkedReviewIds = [];
            if (this.isAllSelected)
                this.checkedReviewIds = this.reviews.map(re => re.id);
        },
        checkReview() {
            this.isAllSelected = false;
        },
        deleteCheckedReviews() {
            process(this, async () => {
                const isConfirmed = await confirm(this, '선택한 리뷰를 삭제하시겠습니까?');
                if (! isConfirmed) return;

                await axios.delete('/api/review/deleteReviews', {
                    data: {
                        reviewIds: this.checkedReviewIds
                    }
                });

                await ok(this, '삭제되었습니다.');
                await this.$store.dispatch('setReviews', this);
                this.toggleEditMode();
            })
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
        }
    }
}
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
            list-style:none;
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
                        width: 100px;
                        height: 80px;

                        > .review-image {
                            width: 100%;
                            height: 100%;
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
        }

    }
}

</style>