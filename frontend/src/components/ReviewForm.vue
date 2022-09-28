<template>
    <div class="review-form">
        <div class="title-area">
            <div class="goto-review-list-btn">
                <span @click="goToReviewList">
                    <FontAwesomeIcon icon="angle-left" />
                    리뷰 목록
                </span>
            </div>
            <BInput
                v-model="title"
                :disabled="isDisabledInput"
                placeholder="이름을 입력해주세요."
            />
        </div>
        <div
            v-if="!isDisabledInput"
            class="image-area"
        >
            <div
                v-if="!fileList.length && !curFileList.length"
                class="file-input-wrapper"
                @dragover="onDragOver"
                @drop="onDrop"
            >
                <input
                    accept="image/*"
                    class="file-input"
                    multiple
                    type="file"
                    @change="onChangeFiles"
                />
                사진을 업로드 해주세요
            </div>
            <div
                v-else
                class="file-list"
            >
                <ul v-if="fileList.length > 0">
                    <li
                        v-for="(file, idx) in fileList"
                        :key="idx"
                    >
                        {{ file.name }}
                        <FontAwesomeIcon
                            class="delete-file-icon"
                            icon="times"
                            @click="deleteFile(idx)"
                        />
                    </li>
                </ul>
                <ul v-if="curFileList.length > 0">
                    <li
                        v-for="(file, idx) in curFileList"
                        :key="idx"
                    >
                        {{ file.fileName }}
                        <FontAwesomeIcon
                            class="delete-file-icon"
                            icon="times"
                            @click="addDeletedFileId(idx)"
                        />
                    </li>
                </ul>
                <ul>
                    <li class="file-btn-area">
                        <BButton
                            class="file-delete-btn"
                            size="sm"
                            @click="deleteAllFile"
                        >
                            전체 삭제
                            <FontAwesomeIcon icon="times" />
                        </BButton>
                        <BButton
                            class="file-add-btn"
                            size="sm"
                        >
                            추가
                            <FontAwesomeIcon icon="plus" />
                            <input
                                accept="image/*"
                                class="file-input"
                                multiple
                                type="file"
                                @change="onChangeFiles"
                            />
                        </BButton>
                    </li>
                </ul>
            </div>
        </div>
        <div
            v-else
            class="slide-image-area"
        >
            <BCarousel
                v-if="curFileList.length > 0"
                controls
                indicators
            >
                <BCarouselSlide
                    v-for="(fileInfo, idx) in curFileList"
                    :key="idx"
                    :img-src="`${imgDirPath}/${fileInfo.reviewId}/${fileInfo.fileName}`"
                    reloadable
                />
            </BCarousel>
            <div
                v-else
                class="no-image-text"
            >
                <span>등록된 사진이 없습니다.</span>
            </div>
        </div>
        <div class="location-info-area">
            <FontAwesomeIcon icon="location-dot" />
            <BInput
                v-model="address"
                :disabled="isDisabledInput"
                placeholder="위치 정보 직접 입력하기"
            />
        </div>
        <div class="rate-area">
            <BFormRating
                v-model="grade"
                :readonly="isDisabledInput"
            />
        </div>
        <div class="review-area">
            <BFormTextarea
                ref="textarea"
                v-model="review"
                :disabled="isDisabledInput"
                placeholder="후기를 입력해주세요."
            />
        </div>
        <div class="bottom-btn-area">
            <BButton
                v-if="!isDisabledInput"
                class="save-btn"
                @click="saveReview"
            >
                저장
            </BButton>
            <BButton
                v-if="isDisabledInput"
                class="mr-2"
                variant="success"
                @click="modifyReview"
            >
                수정하기
            </BButton>
            <BButton
                v-if="isDisabledInput"
                variant="danger"
                @click="removeReview"
            >
                삭제하기
            </BButton>
        </div>
        <ProgressSpinner v-if="processingCount > 0" />
    </div>
</template>

<script>
import axios from 'axios';
import ProgressSpinner from '@/components/ProgressSpinner.vue';
import { IMG_DIR_PATH } from '@/common/Config.js';
import { confirm, ok } from '@/common/Dialog.js';
import { process } from '@/common/Api.js';
import { mapState } from 'vuex';

export default {
    name: 'SideBar',
    components: {
        ProgressSpinner,
    },
    data() {
        return {
            imgDirPath: IMG_DIR_PATH,
            fileList: [],
            deletedFileIds: [],
            isVisibleSideBar: true,
            processingCount: 0,
        };
    },
    computed: {
        ...mapState({
            reviewId: (state) => state.curReviewId,
            curAddress: (state) => state.curAddress,
            curGrade: (state) => state.curGrade,
            curReview: (state) => state.curReview,
            curTitle: (state) => state.curTitle,
            isDisabledInput: (state) => state.isDisabledInput,
            curFileList: (state) => state.curFileList,
        }),
        address: {
            get() {
                return this.curAddress;
            },
            set(newVal) {
                this.$store.commit('setCurAddress', newVal);
            },
        },
        grade: {
            get() {
                return this.curGrade;
            },
            set(newVal) {
                this.$store.commit('setCurGrade', newVal);
            },
        },
        review: {
            get() {
                return this.curReview;
            },
            set(newVal) {
                this.$store.commit('setCurReview', newVal);
            },
        },
        title: {
            get() {
                return this.curTitle;
            },
            set(newVal) {
                this.$store.commit('setCurTitle', newVal);
            },
        },
    },
    methods: {
        goToReviewList() {
            this.$store.commit('setIsVisibleReviewList', true);
            this.$store.commit('setIsDisabledInput', true);
        },
        addDeletedFileId(idx) {
            this.deletedFileIds.push(this.curFileList[idx].fileId);
            const newCurFileList = this.curFileList.reduce((arr, item, i) => {
                if (i !== idx) arr.push(item);
                return arr;
            }, []);
            this.$store.commit('setCurFileList', newCurFileList);
        },
        deleteAllFile() {
            this.curFileList.forEach((file, idx) => {
                this.addDeletedFileId(idx);
            });
            this.$store.commit('setCurFileList', []);
            this.fileList = [];
        },
        deleteFile(idx) {
            this.fileList.splice(idx, 1);
        },
        onChangeFiles(e) {
            this.fileList.push(...e.target.files);
        },
        onDrop(e) {
            this.fileList.push(...e.dataTransfer.files);
        },
        onDragOver(e) {
            e.preventDefault();
        },
        showSideBar() {
            this.isVisibleSideBar = !this.isVisibleSideBar;
        },
        modifyReview() {
            this.$store.commit('setIsDisabledInput', false);
        },
        removeReview() {
            process(this, async () => {
                const isConfirmed = await confirm(this, `'${this.title}' 리뷰를 삭제하시겠습니까?`);
                if (!isConfirmed) return;

                await axios.delete('/api/review/deleteReviews', {
                    data: {
                        reviewIds: [this.reviewId],
                    },
                });

                await ok(this, '삭제되었습니다.');

                await this.$store.dispatch('setReviews', this);
            });
        },
        saveReview() {
            process(this, async () => {
                await axios.post(
                    '/api/review/saveReview',
                    {
                        id: this.reviewId,
                        title: this.title,
                        address: this.address,
                        grade: this.grade,
                        review: this.review,
                        lon: this.$store.state.curLon,
                        lat: this.$store.state.curLat,
                        files: this.fileList,
                        fileIds: this.deletedFileIds,
                    },
                    {
                        transformRequest: function (data) {
                            const formData = new FormData();
                            for (let key in data) {
                                const value = data[key];

                                if (!value) continue;

                                if (key === 'files')
                                    value.forEach((file) => {
                                        formData.append(key, file);
                                    });
                                else formData.append(key, value);
                            }
                            return formData;
                        },
                    }
                );
                await ok(this, '저장 완료되었습니다.');
                this.fileList = [];
                await this.$store.dispatch('setReviews', this);
                await this.$store.dispatch('setFileList', this);
                this.$store.commit('setIsDisabledInput', true);
            });
        },
    },
};
</script>

<style lang="scss" scoped>
.review-form {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    padding: 10px;

    > .title-area {
        padding: 20px 10px;

        > .goto-review-list-btn {
            color: white;
            width: fit-content;

            &:hover {
                cursor: pointer;
                text-decoration: underline;
            }
        }

        input,
        input::placeholder,
        input:focus {
            font-family: 'Nanum Square', serif;
            font-size: 2rem;
            font-weight: bold;
            color: #fff;
            box-shadow: none;
            background: none;
            border: none;
        }
    }

    > .image-area {
        padding: 0 10px;

        > .file-input-wrapper,
        .file-list {
            position: relative;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 1.3rem;
            border: 5px dashed rgb(255, 255, 255, 0.5);
            border-radius: 10px;
            height: 250px;
            background-color: rgb(255, 255, 255, 0.5);
            overflow-y: auto;
            flex-direction: column;

            > ul {
                padding: 0 10px;

                > li {
                    list-style: none;
                    font-size: 1rem;
                    word-break: break-all;

                    > .delete-file-icon {
                        cursor: pointer;
                        padding: 10px 0 0 5px;
                    }
                }

                > .file-btn-area {
                    display: flex;
                    padding-top: 10px;

                    > .file-delete-btn {
                        display: flex;
                        align-items: center;
                        font-size: 0.7rem;
                        margin-right: 5px;
                    }

                    > .file-add-btn {
                        display: flex;
                        align-items: center;
                        font-size: 0.7rem;
                        position: relative;

                        > .file-input {
                            cursor: pointer;
                            position: absolute;
                            right: 0;
                            top: 0;
                            bottom: 0;
                            left: 0;
                            opacity: 0;
                        }
                    }
                }
            }

            > .file-input {
                cursor: pointer;
                position: absolute;
                right: 0;
                top: 0;
                bottom: 0;
                left: 0;
                opacity: 0;
            }
        }
    }

    > .slide-image-area {
        padding: 0 10px;

        ::v-deep .img-fluid {
            height: 250px !important;
            object-fit: cover;
        }

        > .no-image-text {
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 1.3rem;
            border: 5px dashed rgb(255, 255, 255, 0.5);
            border-radius: 10px;
            height: 250px;
            background-color: rgb(255, 255, 255, 0.5);
        }
    }

    > .location-info-area {
        padding: 10px 20px;
        display: flex;
        align-items: center;

        input,
        input::placeholder,
        input:focus {
            font-size: 1rem;
            color: #fff;
            box-shadow: none;
            background: none;
            border: none;
        }
    }

    > .rate-area {
        padding: 0 20px;
        text-align: center;

        output {
            font-size: 2rem;
            color: #ffdd00;
            background: none;
            border: none;
            box-shadow: none;
        }
    }

    > .review-area {
        padding: 20px 10px;

        textarea,
        textarea::placeholder {
            min-height: 300px;
            resize: none;
            color: #fff;
            background: none;
            border: none;
            box-shadow: none;
        }
    }

    > .bottom-btn-area {
        text-align: right;
        padding-right: 10px;

        > .save-btn {
            color: #fff;
            font-weight: bold;
            background-color: #ee9e06;
        }
    }
}
</style>
